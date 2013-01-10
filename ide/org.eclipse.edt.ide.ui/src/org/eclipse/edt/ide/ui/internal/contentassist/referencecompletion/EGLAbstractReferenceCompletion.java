/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.CaseStatement;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.IfStatement;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NodeTypes;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.TryStatement;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.edt.compiler.internal.EGLPropertyRule;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.IWorkingCopyCompileRequestor;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompilationResult;
import org.eclipse.edt.ide.core.internal.compiler.workingcopy.WorkingCopyCompiler;
import org.eclipse.edt.ide.core.internal.errors.EGLPartialParser;
import org.eclipse.edt.ide.core.internal.errors.ParseNode;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.internal.errors.ParseStackEntry;
import org.eclipse.edt.ide.core.internal.errors.TokenStream;
import org.eclipse.edt.ide.core.internal.model.WorkingCopy;
import org.eclipse.edt.ide.core.internal.model.document.EGLDocument;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.EGLModelException;
import org.eclipse.edt.ide.core.model.IEGLFile;
import org.eclipse.edt.ide.core.model.IWorkingCopy;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.EGLUI;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.editor.util.EGLModelUtility;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public abstract class EGLAbstractReferenceCompletion implements IReferenceCompletion{

	private static EGLPartialParser parser = new EGLPartialParser();
	
	protected ArrayList validStates = new ArrayList();
	protected IEditorPart editor;

	protected EGLAbstractReferenceCompletion() {
		precompileContexts();
	}
	
	protected static interface IBoundNodeProcessor {
		void processBoundNode(Node node);
	}
	
	protected static interface IWorkingCopyCompileNodeRequestor extends IWorkingCopyCompileRequestor {
		boolean foundDesiredNode();
	}

	public class BoundNodeWorkingCopyCompileRequestor implements IWorkingCopyCompileNodeRequestor {
		ITextViewer viewer;
		int documentOffset;
		private IBoundNodeProcessor boundNodeProcessor;
		private CompletedNodeVerifier completedNodeVerifier;
		private boolean isDone;
		
		protected BoundNodeWorkingCopyCompileRequestor(ITextViewer viewer, int documentOffset, CompletedNodeVerifier completedNodeVerifier, IBoundNodeProcessor boundNodeProcessor) {
			super();
			this.viewer = viewer;
			this.documentOffset =  documentOffset;
			this.boundNodeProcessor = boundNodeProcessor;
			this.completedNodeVerifier = completedNodeVerifier;
		}
		
		public void acceptResult(WorkingCopyCompilationResult result) {
			IEGLDocument document = (IEGLDocument) viewer.getDocument();
			Node boundPart = result.getBoundPart();
			if(!(boundPart instanceof File)) {
				Node nodeAtOffset = document.getNewModelNodeAtOffset(documentOffset, boundPart);
				if(nodeAtOffset != null) {
					if(completedNodeVerifier.nodeIsValid(nodeAtOffset)) {
						isDone = true;
						boundNodeProcessor.processBoundNode(nodeAtOffset);
					}
				}
			}
		}
		
		public boolean foundDesiredNode() {
			return isDone;
		}
	}

	public class BoundPartWorkingCopyCompileRequestor implements IWorkingCopyCompileNodeRequestor {
		ITextViewer viewer;
		int documentOffset;
		private IBoundNodeProcessor boundNodeProcessor;
		private CompletedNodeVerifier completedNodeVerifier;
		private boolean isDone;
		
		protected BoundPartWorkingCopyCompileRequestor(ITextViewer viewer, int documentOffset, CompletedNodeVerifier completedNodeVerifier, IBoundNodeProcessor boundNodeProcessor) {
			super();
			this.viewer = viewer;
			this.documentOffset =  documentOffset;
			this.boundNodeProcessor = boundNodeProcessor;
			this.completedNodeVerifier = completedNodeVerifier;
		}
		
		public void acceptResult(WorkingCopyCompilationResult result) {
			IEGLDocument document = (IEGLDocument) viewer.getDocument();
			Node nodeAtOffset = ((EGLDocument) document).getNewModelPartAtOffset(documentOffset, result.getBoundPart());
			if(nodeAtOffset != null) {
				if(completedNodeVerifier.nodeIsValid(nodeAtOffset)) {
					isDone = true;
					boundNodeProcessor.processBoundNode(nodeAtOffset);
				}
			}
		}
		
		public boolean foundDesiredNode() {
			return isDone;
		}
	}

	protected abstract void precompileContexts();

	protected void addContext(String prefix) {
		TokenStream stream = new TokenStream(prefix);
		ParseStack stack = parser.parse(stream);
		stack.performAllReductions(NodeTypes.ID);
		if(!validStates.contains(stack.getCurrentState())){
			validStates.add(new Integer(stack.getCurrentState()));	
		}
	}

	protected void addContext(int stateCode) {
		validStates.add(new Integer(stateCode));
	}

	protected abstract List returnCompletionProposals(ParseStack parseStack, String prefix, ITextViewer viewer, int documentOffset);

	public List computeCompletionProposals(
		ParseStack parseStack,
		String prefix,
		ITextViewer viewer,
		int documentOffset,
		IEditorPart editor) {

		this.editor = editor;
		IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
//		ISystemEnvironment env = SystemEnvironmentManager.findSystemEnvironment(editorInput.getFile().getProject(), null); 
//		EGLNewPropertiesHandler.setAnnoTypeMgr( env.getAnnotationTypeManager());
		
		int parseState = parseStack.getCurrentState();

		for (Iterator iter = validStates.iterator(); iter.hasNext();) {
			Integer integer = (Integer) iter.next();
			if (parseState == integer.intValue()) {
				return returnCompletionProposals(parseStack, prefix, viewer, documentOffset);
			}
		}

		return new ArrayList();
	}
	
	public boolean isState(ParseStack parseStack) {
		int parseState = parseStack.getCurrentState();

		for (Iterator iter = validStates.iterator(); iter.hasNext();) {
			Integer integer = (Integer) iter.next();
			if (parseState == integer.intValue()) {
				return true;
			}
		}

		return false;
	}

	protected boolean isState(ParseStack parseStack, int stateToLookFor) {
		for (int i = parseStack.getStack().size() - 1; i >= 0; i--) {
			ParseStackEntry entry = (ParseStackEntry) parseStack.getStack().get(i);
			if(entry.state == stateToLookFor)
				return true;
		}
		return false;
	}

	protected int getState(String prefix) {
		TokenStream stream = new TokenStream(prefix);
		ParseStack stack = new EGLPartialParser().parse(stream);
		stack.performAllReductions(NodeTypes.ID);
		return stack.getCurrentState();
	}

	protected boolean inBlock(ITextViewer viewer, int documentOffset) {
		Node node = ((IEGLDocument) viewer.getDocument()).getNewModelNodeAtOffset(documentOffset);
		//look up the chain until the setStatement is found.  If no set statement, use all states
		while (node != null) {
			if (node instanceof Statement) {
				return ((Statement) node).canIncludeOtherStatements();
			}
			node = node.getParent();
		}
		return false;
	}
	
	protected boolean canIncludeVariableDefinitionStatement(ITextViewer viewer, int documentOffset){
		Node node = ((IEGLDocument) viewer.getDocument()).getNewModelNodeAtOffset(documentOffset);
		//look up the chain until the setStatement is found.  If no set statement, use all states
		while (node != null) {
			if (node instanceof Statement) {
				return ((Statement) node).canIncludeOtherStatements() && 
						((node instanceof IfStatement) ||
						(node instanceof WhileStatement)||
						(node instanceof TryStatement) ||
						(node instanceof ForStatement) ||
						(node instanceof CaseStatement)
						);
			}
			node = node.getParent();
		}
		return false;
	}

	public Node getPart(ITextViewer viewer, int documentOffset) {
		IEGLDocument document = (IEGLDocument) viewer.getDocument();
		return EGLModelUtility.getPartNode(document, documentOffset);
	}
	
	protected Node getNestedPart(ITextViewer viewer, int documentOffset) {
		IEGLDocument document = (IEGLDocument) viewer.getDocument();
		return EGLModelUtility.getNestedPartNode(document, documentOffset);
	}


	/**
	 * @return
	 */
	public List getFunctionNames(ITextViewer viewer, int documentOffset) {
		final List functionNames = new ArrayList();
		Node eglPart = getPart(viewer, documentOffset);
		eglPart.accept(new AbstractASTPartVisitor() {
			public void visitPart(Part part) {
				for(Iterator iter = part.getContents().iterator(); iter.hasNext();) {
					((Node) iter.next()).accept(new DefaultASTVisitor() {
						public void endVisit(NestedFunction nestedFunction) {
							functionNames.add(nestedFunction.getName().getCanonicalName());
						}
					});
				}
			}
		});
		return functionNames;
	}

	/**
	 * return a list instead of a proposal.  This way all references do not have to check
	 * for null.  They can just do an addAll()
	 * 
	 * cursor position is the proposalString length
	 * selection length is 0
	 */
	public List createProposal(ITextViewer viewer, String proposalString, String prefix, String additionalInfo, int documentOffset) {
		return createProposal(viewer, proposalString, prefix, additionalInfo, documentOffset, proposalString.length(), 0);
	}

	/**
	 * return a list instead of a proposal.  This way all references do not have to check
	 * for null.  They can just do an addAll()
	 * 
	 * selection length is the proposalString length
	 */
	public List createProposal(ITextViewer viewer, String proposalString, String prefix, String additionalInfo, int documentOffset, int cursorPosition) {
		return createProposal(viewer, proposalString, prefix, additionalInfo, documentOffset, cursorPosition, proposalString.length());
	}

	/**
	 * return a list instead of a proposal.  This way all references do not have to check
	 * for null.  They can just do an addAll()
	 */
	public List createProposal(ITextViewer viewer, String proposalString, String prefix, String additionalInfo, int documentOffset, int cursorPosition, int postSelectionLength) {
		return createProposal(viewer, proposalString, proposalString, prefix, additionalInfo, documentOffset, cursorPosition, postSelectionLength);
	}

	/**
	 * return a list instead of a proposal.  This way all references do not have to check
	 * for null.  They can just do an addAll()
	 */
	public List createProposal(ITextViewer viewer, String displayString, String proposalString, String prefix, String additionalInfo, int documentOffset, int cursorPosition, int postSelectionLength) {
		List proposals = new ArrayList();
		if (compatiblePrefix(displayString, prefix)) {
			proposals.add(
				new EGLCompletionProposal(viewer,
					displayString,
					proposalString,
					additionalInfo,
					documentOffset - prefix.length(),
					prefix.length(),
					cursorPosition,
					EGLCompletionProposal.RELEVANCE_MEDIUM,
					postSelectionLength,
					EGLCompletionProposal.NO_IMG_KEY));
		}
		return proposals;
	}

	/**
	 * @param displayString
	 * @param prefix
	 * @return
	 */
	private boolean compatiblePrefix(String displayString, String prefix) {
		if (displayString.toUpperCase().startsWith(prefix.toUpperCase())) {
			return true;
		}
		//handle case where a property value needs to be surrounded by quotes
		if (displayString.startsWith("\"")) { //$NON-NLS-1$
			displayString = displayString.substring(1,displayString.length()-1);
			if (displayString.toUpperCase().startsWith(prefix.toUpperCase()))
				return true;
		}
		return false;
	}

	/**
	 * Analyze the stack for the previous token.  
	 * 
	 * @return String
	 */
	protected String getNodeText(ParseStack parseStack, int contextDeleted) {
		ParseStack tempStack = parseStack.copy();
		ParseNode[] parseNodes = tempStack.deleteContext(contextDeleted);
		return parseNodes[0].getText().trim();
	}	

	protected String getNodeText2(ParseStack parseStack, int contextDeleted) {
		ParseStack tempStack = parseStack.copy();
		ParseNode[] parseNodes = tempStack.deleteContext(contextDeleted);
		for (int i = parseNodes.length; i > 0; i--) {
			ParseNode node = parseNodes[i-1];
			if (node.getText() != null && node.getText().length()>0)
				return node.getText().trim();
		}
		return "";	 //$NON-NLS-1$
	}	

	
	protected static interface CompletedNodeVerifier {
		boolean nodeIsValid(Node astNode);
	}
	
	protected void getBoundASTNode(final ITextViewer viewer, final int documentOffset, IBoundNodeProcessor boundNodeProcessor) {
		getBoundASTNode(viewer, documentOffset, new String[] {""}, new CompletedNodeVerifier() { //$NON-NLS-1$
			public boolean nodeIsValid(Node astNode) {
				return true;
			}
		}, boundNodeProcessor);
	}
	
	protected void getBoundASTPart(final ITextViewer viewer, final int documentOffset, String[] completionsToTry, CompletedNodeVerifier completedNodeVerifier, IBoundNodeProcessor boundNodeProcessor) {
		getBoundASTNode(viewer, documentOffset, completionsToTry, new BoundPartWorkingCopyCompileRequestor(viewer, documentOffset, completedNodeVerifier, boundNodeProcessor));
	}
	
	protected void getBoundASTNode(final ITextViewer viewer, final int documentOffset, String[] completionsToTry, CompletedNodeVerifier completedNodeVerifier, IBoundNodeProcessor boundNodeProcessor) {
		getBoundASTNode(viewer, documentOffset, completionsToTry, new BoundNodeWorkingCopyCompileRequestor(viewer, documentOffset, completedNodeVerifier, boundNodeProcessor));
	}

	private void getBoundASTNode(final ITextViewer viewer, final int documentOffset, String[] completionsToTry, IWorkingCopyCompileNodeRequestor requestor) {
		IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
		IPath path = file.getFullPath();
		IWorkingCopy[] sharedWorkingCopies = EGLCore.getSharedWorkingCopies(EGLUI.getBufferFactory());
		int myIndexInSharedWorkingCopies = -1;
		IWorkingCopy myOriginalWorkingCopy = null;
		
		for(int i = 0; i < completionsToTry.length; i++) {
			String textToInsertAtOffset = completionsToTry[i];
			IEGLFile tempFile = (IEGLFile) EGLCore.create(file);
			
			for(int j = 0; j < sharedWorkingCopies.length && myOriginalWorkingCopy == null; j++) {
				if(sharedWorkingCopies[j].getOriginalElement().getResource().equals(file)) {
					myIndexInSharedWorkingCopies = j;
					myOriginalWorkingCopy = sharedWorkingCopies[j];
				}
			}
		
			try {
				WorkingCopy workingCopy = (WorkingCopy) tempFile.getWorkingCopy();
				char[] workingCopyText = ((WorkingCopy) myOriginalWorkingCopy).getBuffer().getCharacters();
				char[] newText = new char[workingCopyText.length + textToInsertAtOffset.length()];
				System.arraycopy(workingCopyText, 0, newText, 0, documentOffset);
				System.arraycopy(textToInsertAtOffset.toCharArray(), 0, newText, documentOffset, textToInsertAtOffset.length());
				System.arraycopy(workingCopyText, documentOffset, newText, documentOffset+textToInsertAtOffset.length(), workingCopyText.length-documentOffset);
				workingCopy.getBuffer().setContents(newText);
				sharedWorkingCopies[myIndexInSharedWorkingCopies] = workingCopy;
				
				WorkingCopyCompiler.getInstance().compileAllParts(
						file.getProject(),
						getPackageName(path),
						file,
						sharedWorkingCopies,
						requestor);			
				
				sharedWorkingCopies[myIndexInSharedWorkingCopies] = myOriginalWorkingCopy;
				
				workingCopy.destroy();
			} catch (EGLModelException e) {
				throw new RuntimeException(e);
			}
			
			if(requestor.foundDesiredNode()) {
				break;
			}
		}
	}
	
	protected void getBoundASTNodeForOffsetInStatement(final ITextViewer viewer, final int documentOffset, IBoundNodeProcessor boundNodeProcessor) {
		getBoundASTNode(viewer, documentOffset, new String[] {";", "", ";end end"}, new CompletedNodeVerifier() { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			public boolean nodeIsValid(Node astNode) {
				return astNode != null;
			}
		}, boundNodeProcessor);
	}
	
	private String getPackageName(IPath path) {
		String packageName = "";
		if (path.segmentCount() > 3) {
			boolean first = true;
			for (int i = 2; i < path.segmentCount()-1; i++) {
				if (first) {
					first = false;
				}
				else {
					packageName = packageName + ".";
				}
				packageName = packageName + path.segment(i);
			}
		}
		
		return packageName;
	}

	public String toString() {
		StringBuffer buf= new StringBuffer();
		buf.append(this);
		buf.append("validStates= "); 	//$NON-NLS-1$
		for (Iterator iter = validStates.iterator(); iter.hasNext();) {
			Integer state = (Integer) iter.next();
			buf.append(state);
			buf.append(","); 			//$NON-NLS-1$
		}
		return buf.toString();
	}
	/**
	 * @return Returns the editor.
	 */
	public IEditorPart getEditor() {
		return editor;
	}
	/**
	 * @param editor The editor to set.
	 */
	public void setEditor(IEditorPart editor) {
		this.editor = editor;
	}

	protected int getSearchConstantsForDeclarableParts() {
		return 
				IEGLSearchConstants.DELEGATE | 
				IEGLSearchConstants.EXTERNALTYPE | 
				IEGLSearchConstants.RECORD| 
				IEGLSearchConstants.HANDLER | 
				IEGLSearchConstants.INTERFACE | 
				IEGLSearchConstants.ENUMERATION | 
				IEGLSearchConstants.CLASS;
		
	}

	protected int getSearchConstantsForPartsWithStaticMembers() {
		return 
				IEGLSearchConstants.LIBRARY | 
				IEGLSearchConstants.EXTERNALTYPE | 
				IEGLSearchConstants.ENUMERATION | 
				IEGLSearchConstants.CLASS;
		
	}

}
