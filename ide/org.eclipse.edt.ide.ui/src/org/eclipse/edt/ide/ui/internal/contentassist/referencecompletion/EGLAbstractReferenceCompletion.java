/*******************************************************************************
 * Copyright ©2000, 2011 IBM Corporation and others.
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
import org.eclipse.edt.compiler.ISystemEnvironment;
import org.eclipse.edt.compiler.binding.FixedRecordBindingImpl;
import org.eclipse.edt.compiler.binding.FlexibleRecordBindingImpl;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayType;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NodeTypes;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.core.ast.PrimitiveType;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.core.ast.VariableFormField;
import org.eclipse.edt.compiler.internal.EGLNewPropertiesHandler;
import org.eclipse.edt.compiler.internal.EGLPropertyRule;
import org.eclipse.edt.ide.core.internal.compiler.SystemEnvironmentManager;
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
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPropertyValueProposalHandler;
import org.eclipse.edt.ide.ui.internal.editor.util.EGLModelUtility;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public abstract class EGLAbstractReferenceCompletion implements IReferenceCompletion{

	private static EGLPartialParser parser = new EGLPartialParser();
	
	static final String[] EGLCORE = new String[] {"egl", "core"}; //$NON-NLS-1$ //$NON-NLS-2$
	static final String[] EGLJAVA = new String[] {"egl", "java"}; //$NON-NLS-1$ //$NON-NLS-2$
	static final String[] EGLIDLJAVA = new String[] {"eglx", "java"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	static final String[] EGLJAVASCRIPT = new String[] {"eglx", "javascript"}; //$NON-NLS-1$ //$NON-NLS-2$
	static final String[] EGLPLATFORM = new String[] {"egl", "platform"}; //$NON-NLS-1$ //$NON-NLS-2$
	static final String[] EGLIOSQL = new String[] {"egl", "io", "sql"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	static final String[] EGLIOFILE = new String[] {"egl", "io", "file"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

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
		ISystemEnvironment env = SystemEnvironmentManager.findSystemEnvironment(editorInput.getFile().getProject(), null); 
		EGLNewPropertiesHandler.setAnnoTypeMgr( env.getAnnotationTypeManager());
		
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

	public Node getPart(ITextViewer viewer, int documentOffset) {
		IEGLDocument document = (IEGLDocument) viewer.getDocument();
		return EGLModelUtility.getPartNode(document, documentOffset);
	}
	
	protected Node getNestedPart(ITextViewer viewer, int documentOffset) {
		IEGLDocument document = (IEGLDocument) viewer.getDocument();
		return EGLModelUtility.getNestedPartNode(document, documentOffset);
	}

	public List getItemNames(final ITextViewer viewer, final int documentOffset, final String typeName) {
		return getItemNames(viewer, documentOffset, typeName, true);
	}

	public List getItemNames(final ITextViewer viewer, final int documentOffset, final String typeName, boolean returnNamesInRecords) {
		final List itemNames = new ArrayList();
		Node eglPart = getPart(viewer, documentOffset);
		//If eglPart is not a record it is a declaration that is being overridden.  Need to get to the
		//record to give a proposal of the record fields
		if (eglPart != null && ((Part) eglPart).getPartType() != Part.RECORD && returnNamesInRecords) {
			itemNames.addAll(getStructureItems());
		}
		else {
			eglPart.accept(new AbstractASTPartVisitor() {
				public void visitPart(org.eclipse.edt.compiler.core.ast.Part part) {
					for(Iterator iter = part.getContents().iterator(); iter.hasNext();) {
						((Node) iter.next()).accept(new DefaultASTVisitor() {
							public void endVisit(StructureItem structureItem) {
								if(structureItem.getName() != null) {
									itemNames.add(structureItem.getName().getCanonicalName());
								}
							}
							
							public boolean visit(FormGroup formGroup) {
								Node nestedPart = getNestedPart(viewer, documentOffset);
								nestedPart.accept(new AbstractASTVisitor() {
									public boolean visit(VariableFormField variableFormField) {
										if(variableFormField.getType().isArrayType()) {
											itemNames.add(variableFormField.getName().getCanonicalName());
										}
										return false;
									}
								});	
								return false;
							}
							
							public void endVisit(VariableFormField variableFormField) {
								if(variableFormField.getType().isArrayType()) {
									itemNames.add(variableFormField.getName().getCanonicalName());
								}
							}
							
							public void endVisit(ClassDataDeclaration classDataDeclaration) {
								Type type = classDataDeclaration.getType();
								boolean addNames = false;
								if (typeName == null)
									addNames = true;
								else if (type != null && type.getCanonicalName().equalsIgnoreCase(typeName))
									addNames = true;
								
								if(addNames) {							
									for(Iterator namesIter = classDataDeclaration.getNames().iterator(); namesIter.hasNext();) {
										itemNames.add(((Name) namesIter.next()).getCanonicalName());
									}
								}
							}
						});
					}
				}
			});
		}
		return itemNames;
	}

	public List getItemNames2(final ITextViewer viewer, final int documentOffset, final String typeName, Node eglPart) {
			final List itemNames = new ArrayList();
			eglPart.accept(new AbstractASTPartVisitor() {
				public void visitPart(org.eclipse.edt.compiler.core.ast.Part part) {
					for(Iterator iter = part.getContents().iterator(); iter.hasNext();) {
						((Node) iter.next()).accept(new DefaultASTVisitor() {
							public void endVisit(StructureItem structureItem) {
								if(structureItem.getName() != null) {
									itemNames.add(structureItem.getName().getCanonicalName());
								}
							}
							
							public boolean visit(FormGroup formGroup) {
								Node nestedPart = getNestedPart(viewer, documentOffset);
								nestedPart.accept(new AbstractASTVisitor() {
									public boolean visit(VariableFormField variableFormField) {
										itemNames.add(variableFormField.getName().getCanonicalName());
										return false;
									}
								});	
								return false;
							}
							
							public void endVisit(VariableFormField variableFormField) {
								itemNames.add(variableFormField.getName().getCanonicalName());
							}
							
							public void endVisit(ClassDataDeclaration classDataDeclaration) {
								Type type = classDataDeclaration.getType();
								boolean addNames = false;
								if (typeName == null)
									addNames = true;
								else if (type != null && type.getCanonicalName().equalsIgnoreCase(typeName))
									addNames = true;
								
								if(addNames) {							
									for(Iterator namesIter = classDataDeclaration.getNames().iterator(); namesIter.hasNext();) {
										itemNames.add(((Name) namesIter.next()).getCanonicalName());
									}
								}
							}
							
							public void endVisit(FunctionDataDeclaration funtionDataDeclaration) {
								Type type = funtionDataDeclaration.getType();
								boolean addNames = false;
								if (typeName == null)
									addNames = true;
								else if (type != null && type.getCanonicalName().equalsIgnoreCase(typeName))
									addNames = true;
								
								if(addNames) {							
									for(Iterator namesIter = funtionDataDeclaration.getNames().iterator(); namesIter.hasNext();) {
										itemNames.add(((Name) namesIter.next()).getCanonicalName());
									}
								}
							}
						});
					}
				}
			});
			return itemNames;
		}
	/**
	 * @return
	 */
	public List getArrayItemNames(final ITextViewer viewer, final int documentOffset, final String typeName) {
		final List itemNames = new ArrayList();
		Node eglPart = getPart(viewer, documentOffset);
		//If eglPart is not a record it is a declaration that is being overridden.  Need to get to the
		//record to give a proposal of the record fields
		if (eglPart != null && ((Part) eglPart).getPartType() != Part.RECORD) {
			itemNames.addAll(getStructureItems());
		}
		
		eglPart.accept(new AbstractASTPartVisitor() {
			public void visitPart(org.eclipse.edt.compiler.core.ast.Part part) {
				for(Iterator iter = part.getContents().iterator(); iter.hasNext();) {
					((Node) iter.next()).accept(new DefaultASTVisitor() {
						public void endVisit(StructureItem structureItem) {
							if(structureItem.getName() != null && structureItem.hasOccurs()) {
								itemNames.add(structureItem.getName().getCanonicalName());
							}
						}
						
						public boolean visit(FormGroup formGroup) {
							Node nestedPart = getNestedPart(viewer, documentOffset);
							nestedPart.accept(new AbstractASTVisitor() {
								public boolean visit(VariableFormField variableFormField) {
									if(variableFormField.getType().isArrayType()) {
										itemNames.add(variableFormField.getName().getCanonicalName());
									}
									return false;
								}
							});	
							return false;
						}
						
						public void endVisit(VariableFormField variableFormField) {
							if(variableFormField.getType().isArrayType()) {
								itemNames.add(variableFormField.getName().getCanonicalName());
							}
						}
						
						public void endVisit(ClassDataDeclaration classDataDeclaration) {
							Type type = classDataDeclaration.getType();
							boolean addNames = false;
							if (typeName == null)
								addNames = true;
							else if (type != null && type.getCanonicalName().equalsIgnoreCase(typeName))
								addNames = true;
							
							if(addNames) {
								addNames = type != null && type.isArrayType();
							}
							
							if(addNames) {							
								for(Iterator namesIter = classDataDeclaration.getNames().iterator(); namesIter.hasNext();) {
									itemNames.add(((Name) namesIter.next()).getCanonicalName());
								}
							}
						}
					});
				}
			}
		});
		return itemNames;
	}
	public List getIntBooleanItemNames(final ITextViewer viewer, final int documentOffset) {
		// currently this is only called for JSF handler class declarations
		final List itemNames = new ArrayList();
		Node eglPart = getPart(viewer, documentOffset);
		
		eglPart.accept(new AbstractASTPartVisitor() {
			public void visitPart(org.eclipse.edt.compiler.core.ast.Part part) {
				for(Iterator iter = part.getContents().iterator(); iter.hasNext();) {
					((Node) iter.next()).accept(new DefaultASTVisitor() {
						public void endVisit(ClassDataDeclaration classDataDeclaration) {
							Type type = classDataDeclaration.getType();
							if (type!= null && type.isPrimitiveType()) {
								int primitiveType = ((PrimitiveType) type).getPrimitive().getType();
								if (primitiveType == Primitive.INT_PRIMITIVE || primitiveType == Primitive.BOOLEAN_PRIMITIVE) {
									for(Iterator namesIter = classDataDeclaration.getNames().iterator(); namesIter.hasNext();) {
										itemNames.add(((Name) namesIter.next()).getCanonicalName());
									}
								}
							}
						}
					});
				}
			}
		});
		return itemNames;
	}

	public List getStructureItems() {
		return new ArrayList();
	}

	public List getStructureItems(Node boundNodex) {
		List itemNames = new ArrayList();
		Node node = boundNodex;
		while (node != null) {
			if (node instanceof ClassDataDeclaration || node instanceof FunctionDataDeclaration)
				break;
			node = node.getParent();
		}
		if (node != null) {
			NameType type = null;
			if (node instanceof ClassDataDeclaration) {
				Type type2 = ((ClassDataDeclaration) node).getType();
				type = type2.isNameType() ?
						(NameType) type2 : null;
				if (type == null && type2.isArrayType()) {
					Type baseType = ((ArrayType) type2).getBaseType();
					type = baseType.isNameType() ?
							(NameType) baseType : null;
				}
			} else if (node instanceof FunctionDataDeclaration) {
				Type type2 = ((FunctionDataDeclaration) node).getType();
				type = type2.isNameType() ?
						(NameType) type2 : null;
			}
			if (type != null) {
				Name name = type.getName();
				IBinding binding = name.resolveBinding();
				if (binding instanceof FixedRecordBindingImpl) {
					FixedRecordBindingImpl recordBinding = (FixedRecordBindingImpl) binding;
					List structureItems = recordBinding.getStructureItems();
					for (Iterator iter = structureItems.iterator(); iter.hasNext();) {
						StructureItemBinding structureItemBinding = (StructureItemBinding) iter.next();
						itemNames.add(structureItemBinding.getName());
					}
				}
				else if (binding instanceof FlexibleRecordBindingImpl) {
					FlexibleRecordBindingImpl recordBinding = (FlexibleRecordBindingImpl) binding;
					IDataBinding structureItems[] = recordBinding.getFields();
					for (int i = 0; i < structureItems.length; i++) {
						IDataBinding dataBinding = structureItems[i];
						itemNames.add(dataBinding.getName());
					}
				}
			}
		}
		return itemNames;
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
					postSelectionLength));
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

	/**
	 * @return
	 */
	public List getListValueKeyItemsProposals(ITextViewer viewer, int documentOffset, String prefix, boolean brackets) {
		List proposals = new ArrayList();
		//should really remove items already in the list
		List itemNames = getItemNames(viewer, documentOffset, null);
	
		// create the proposals
		for (Iterator iter = itemNames.iterator(); iter.hasNext();) {
			String displayString = (String) iter.next();
			String proposalString = displayString;
			proposalString = brackets ? "[" + proposalString + "]" : proposalString; //$NON-NLS-1$ //$NON-NLS-2$
			proposals.addAll(
				createProposal(
					viewer,
					displayString,
					proposalString,
					prefix,
					UINlsStrings.CAProposal_ItemName,
					documentOffset,
					brackets ? proposalString.length()-1 : proposalString.length(),
					0));
		}
		return proposals;
	}

	/**
	 * @return proposal list
	 */
	public List getListValueOutlineProposals(ITextViewer viewer, int documentOffset, String prefix, boolean brackets, ParseStack parseStack, EGLPropertyRule rule) {
		List proposals = new ArrayList();
		//get the outline properties already specified so we do not give them as proposals
		List currentProperties = new ArrayList();
		if(parseStack != null) {
			ParseStack tempStack = parseStack.copy();
			ParseNode parseNode;
			int stackSize = tempStack.getStack().size();
			while (stackSize > 0) {
				parseNode = tempStack.deleteContext(1)[0];
				stackSize--;
				if (parseNode.getText().startsWith(",")) { //$NON-NLS-1$
					parseNode = tempStack.deleteContext(1)[0];
					stackSize--;
					currentProperties.add(parseNode.getText());
				}
				if (parseNode.getText().startsWith("=")) //$NON-NLS-1$
					break;
			}
		}
		// create the proposals
		for (int i = 0; i < rule.getSpecificValues().length; i++) {
			String displayString = rule.getSpecificValues()[i];
			String proposalString = brackets ? "[" + displayString + "]" : displayString; //$NON-NLS-1$ //$NON-NLS-2$
			if (!currentProperties.contains(displayString))
				proposals.addAll(
					createProposal(
						viewer,
						displayString,
						proposalString,
						prefix,
						UINlsStrings.CAProposal_PropertyValue,
						documentOffset,
						brackets ? proposalString.length()-1 : proposalString.length(),
						0));
		}
		return proposals;
	}

	/**
	 * @return proposal list
	 */
	public List getListValueValidationBypassKeysProposals(ITextViewer viewer, int documentOffset, String prefix, boolean brackets) {
		List proposals = new ArrayList();
		String displayString = EGLPropertyValueProposalHandler.VALIDATIONBYPASSKEYS_PFN_LIST_PROPOSAL;
		String proposalString = brackets ? "[" + displayString + "]" : displayString; //$NON-NLS-1$ //$NON-NLS-2$
		proposals.addAll(createProposal(
			viewer,
			displayString,
			proposalString,
			prefix,
			UINlsStrings.CAProposal_KeyValue,
			documentOffset,
			brackets ? proposalString.length()-2: proposalString.length()-1,
			1));
		return proposals;
	}

	/**
	 * @return proposal list
	 */
	public List getListValueValidationBypassFunctionsProposals(ITextViewer viewer, int documentOffset, String prefix, boolean brackets) {
		List proposals = new ArrayList();
		//should really remove items already in the list
		List itemNames = getFunctionNames(viewer, documentOffset);
	
		// create the proposals
		for (Iterator iter = itemNames.iterator(); iter.hasNext();) {
			String displayString = (String) iter.next();
			String proposalString = displayString;
			proposalString = brackets ? "[" + proposalString + "]" : proposalString; //$NON-NLS-1$ //$NON-NLS-2$
			proposals.addAll(
				createProposal(
					viewer,
					displayString,
					proposalString,
					prefix,
					UINlsStrings.CAProposal_NestedFunction,
					documentOffset,
					brackets ? proposalString.length()-1 : proposalString.length(),
					0));
		}
		return proposals;
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
	
	private String[] getPackageName(IPath path) {
		String[] packageName = new String[path.segmentCount()-3];
		if (path.segmentCount() > 3) {
			for (int i = 2; i < path.segmentCount()-1; i++) {
				packageName[i-2] = path.segment(i);
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
		return IEGLSearchConstants.RECORD	| IEGLSearchConstants.ITEM |
		       IEGLSearchConstants.SERVICE	| IEGLSearchConstants.INTERFACE |
		       IEGLSearchConstants.DELEGATE	| IEGLSearchConstants.EXTERNALTYPE;
	}

	protected boolean isPlainAssignmentState(ParseStack parseStack) {
		EGLAssignmentStatementReferenceCompletion plainAssignmentCompletion = new EGLAssignmentStatementReferenceCompletion();
		int currentState = parseStack.getCurrentState();
		for(Iterator iter = plainAssignmentCompletion.validStates.iterator(); iter.hasNext();) {
			if(currentState == ((Integer) iter.next()).intValue()) {
				return true;
			}
		}
		return false;
	}
}
