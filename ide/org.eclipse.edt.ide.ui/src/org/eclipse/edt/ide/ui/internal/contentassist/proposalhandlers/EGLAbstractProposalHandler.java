/*******************************************************************************
 * Copyright © 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.awt.Point;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.ImportDeclaration;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.compiler.tools.EGL2IR;
import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.core.model.EGLCore;
import org.eclipse.edt.ide.core.model.IEGLElement;
import org.eclipse.edt.ide.core.model.document.IEGLDocument;
import org.eclipse.edt.ide.core.search.IEGLSearchScope;
import org.eclipse.edt.ide.core.search.SearchEngine;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLProposalContextInformation;
import org.eclipse.edt.ide.ui.internal.editor.EGLEditor;
import org.eclipse.edt.ide.ui.internal.editor.util.EGLModelUtility;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.PatternType;
import org.eclipse.edt.mof.egl.SequenceType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public abstract class EGLAbstractProposalHandler {
	static final protected String PROPOSAL_PART_REFERENCE_STRING = "{0}/{1}/{2} ({3})"; //$NON-NLS-1$
	static final protected String PROPOSAL_VARIABLE_DOT_STRING = "{0} ({1})"; //$NON-NLS-1$
	
	static final String[] EGLPERSISTENCE = new String[] {"eglx", "persistence"};
	static final String[] EGLLANG = new String[]{"eglx", "lang"};

	private int documentOffset;
	private String prefix;
	protected IEditorPart editor;
	protected ITextViewer viewer;

	public EGLAbstractProposalHandler(ITextViewer viewer, int documentOffset, String prefix) {
		this(viewer, documentOffset, prefix, null);
	}

	public EGLAbstractProposalHandler(ITextViewer viewer, int documentOffset, String prefix, IEditorPart editor) {
		super();
		this.viewer = viewer;
		this.documentOffset = documentOffset;
		this.prefix = prefix;
		this.editor = editor;
		
		if(null != editor){
			IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
		}
	}

	/*
	 * use project scope to search past the current project.  It will also
	 * search referenced projects.
	 * This is a typical case where content assist will add the import
	 * statement for the part after the selection is made.
	 */
	public IEGLSearchScope createProjectSearchScope() {
		IFileEditorInput editorInput = (IFileEditorInput) editor.getEditorInput();
		return createSearchScope(editorInput.getFile().getProject());
	}

	/*
	 * use project scope to search past the current project.  It will also
	 * search referenced projects.
	 * This is a typical case where content assist will add the import
	 * statement for the part after the selection is made.
	 */
	private IEGLSearchScope createSearchScope(IResource resource) {
		IEGLElement[] elements = new IEGLElement[1];
		elements[0] = EGLCore.create(resource);
		return SearchEngine.createEGLSearchScope(elements);
	}

	/**
	 * @return
	 */
	public int getDocumentOffset() {
		return documentOffset;
	}

	/**
	 * @return
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @return
	 */
	public ITextViewer getViewer() {
		return viewer;
	}

	protected Node getPart() {
		IEGLDocument document = (IEGLDocument) getViewer().getDocument();
		return EGLModelUtility.getPartNode(document, getDocumentOffset());
	}

	/**
	 * @param replacementString
	 * @return
	 */
	protected Point getFirstParmSelection(String replacementString) {
		//select the first parameter if there is a parameter.
		//if no parameter, place the cursor at the end of the text
		int cursorPosition = replacementString.indexOf('(');
		int selectionLength = replacementString.indexOf(',');
		int closedParenOffset = replacementString.indexOf(')');
		if (selectionLength > 0)
			//more than 1 parameter
			selectionLength = selectionLength - cursorPosition-1;
		else
			//0 or 1 parameters
			selectionLength = closedParenOffset - cursorPosition-1;
		if (selectionLength == 0)
			//no parameters
			cursorPosition = replacementString.length()-1;
		if (cursorPosition > 0)
			return new Point(cursorPosition+1, selectionLength);
		return new Point(replacementString.length(), selectionLength);
	}

	protected boolean containsProperty(final String propertyString, List propertyBlockList) {
		if (propertyBlockList != null) {
			for (Iterator pbIter = propertyBlockList.iterator(); pbIter.hasNext();) {
				SettingsBlock propertyBlock = (SettingsBlock) pbIter.next();
				final boolean[] foundMatch = new boolean[] {false};
				for (Iterator pIter = propertyBlock.getSettings().iterator(); pIter.hasNext() && !foundMatch[0];) {
					((Node) pIter.next()).accept(new DefaultASTVisitor() {
						public boolean visit(Assignment assignment) {
							foundMatch[0] = assignment.getLeftHandSide().getCanonicalString().equalsIgnoreCase(propertyString);
							return false;
						}
						
						public boolean visit(SetValuesExpression setValuesExpression) {
							setValuesExpression.getExpression().accept(this);
							return false;
						}
						
						public boolean visit(AnnotationExpression annotationExpression) {
							foundMatch[0] = annotationExpression.getName().getCanonicalName().equalsIgnoreCase(propertyString);
							return false;
						}
						
						public boolean visit(SimpleName name) {
							foundMatch[0] = name.getCanonicalName().equalsIgnoreCase(propertyString);
							return false;
						}
					});				
				}
				if(foundMatch[0]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 */
	public String getPartReferenceAdditionalInformation(String packageName, PartDeclarationInfo part, String partTypeString) {
		String extension = IEGLConstants.EGL;
		if(part.isBinary()){
			extension = EGL2IR.EGLXML;
		}
		if(packageName.trim().length() > 0){
			return MessageFormat.format(
				PROPOSAL_PART_REFERENCE_STRING,
				new String[] {
					part.getProject(),
					part.getFolder(),
					packageName + "/" + part.getFileName() + "." + extension, //$NON-NLS-1$ //$NON-NLS-2$
					partTypeString });
		}
		else{
			return MessageFormat.format(
					PROPOSAL_PART_REFERENCE_STRING,
					new String[] {
						part.getProject(),
						part.getFolder(),
						part.getFileName() + "." + extension, //$NON-NLS-1$ //$NON-NLS-2$
						partTypeString });
		}
	}
 
	protected static String getTypeString(Type type) {
		if (type == null) {
			return "";
		}
		if (type instanceof ArrayType) {
			return getTypeString(((ArrayType)type).getElementType()) + "[]";
		}
		
		if (type instanceof FixedPrecisionType) {
			FixedPrecisionType fpType = (FixedPrecisionType) type;
			String len = fpType.getLength() == null ? "0" : fpType.getLength().toString();
			String dec = fpType.getDecimals() == null ? "0" : fpType.getDecimals().toString();
			
			return getTypeString(fpType.getClassifier()) + "(" + len + ", " + dec + ")";
		}

		if (type instanceof PatternType) {
			PatternType pType = (PatternType) type;
			String pattern = pType.getPattern() == null ? "" : pType.getPattern();
			
			return getTypeString(pType.getClassifier()) + "(\"" + pattern + "\")";
		}

		if (type instanceof SequenceType) {
			SequenceType spType = (SequenceType) type;
			String len = spType.getLength() == null ? "0" : spType.getLength().toString();
			
			return getTypeString(spType.getClassifier()) + "(" + len + ")";
		}

		return getNameFromElement(type);
	}
	
	protected static String getNameFromElement(Element elem) {
		if (elem == null) {
			return "";
		}
		if (elem instanceof NamedElement) {
			return ((NamedElement)elem).getCaseSensitiveName();
		}
		return "";
	}
	
	protected String getAdditionalInfo(Type type) {
		return getTypeString(type);
	}

	protected String getAdditionalInfo(Member mbr) {
		if (mbr != null)
			return getAdditionalInfo(mbr.getType());
		else
			return ""; //$NON-NLS-1$
	}

	public List getProposals(String[] strings, String additionalInfo) {
		return getProposals(strings, additionalInfo, EGLCompletionProposal.RELEVANCE_MEDIUM, 0, EGLCompletionProposal.NO_IMG_KEY);
	}

	public List getProposals(String[] strings, String additionalInfo, int relevance, String img_src) {
		List proposals = new ArrayList();
		for (int i = 0; i < strings.length; i++) {
			if (strings[i].toUpperCase().startsWith(getPrefix().toUpperCase())) {
				proposals.add(
					new EGLCompletionProposal(viewer,
						null,
						strings[i],
						additionalInfo,
						getDocumentOffset() - getPrefix().length(),
						getPrefix().length(),
						0,
						relevance,
						strings[i].length(),
						img_src));
			}
		}
		return proposals;
	}
	
	public List getProposals(String[] strings, String additionalInfo, int relevance, int postSelectionLength, String img_src) {
		List proposals = new ArrayList();
		for (int i = 0; i < strings.length; i++) {
			if (strings[i].toUpperCase().startsWith(getPrefix().toUpperCase())) {
				proposals.add(
					new EGLCompletionProposal(viewer,
						null,
						strings[i],
						additionalInfo,
						getDocumentOffset() - getPrefix().length(),
						getPrefix().length(),
						strings[i].length() - postSelectionLength,
						relevance,
						postSelectionLength,
						img_src));
			}
		}
		return proposals;
	}

	protected String getPackageName(PartDeclarationInfo part) {
		String packageName =
			(part.getPackageName() == null || part.getPackageName().length() == 0)
				? UINlsStrings.OpenPartDialog_DefaultPackage
				: part.getPackageName();
		return packageName;
	}

	protected EGLCompletionProposal createPartProposal(PartDeclarationInfo part, String partTypeName) {
		return createPartProposal(part, partTypeName, false);
	}

	protected EGLCompletionProposal createPartProposal(PartDeclarationInfo part, String partTypeName, boolean quoted) {
		String packageName = getPackageName(part);
		String displayString = buildDisplayName(part, partTypeName, packageName);
		boolean hasImportForPartWithADifferentPackage = hasImportForPartWithADifferentPackage(part.getPackageName(), part.getPartName());		
		String proposalString = getProposalString(part, hasImportForPartWithADifferentPackage);
		if (quoted)
			proposalString = "\"" + proposalString + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		
		String importPackageName = null;
		String importPartName = null;
		
		//Do not add import if default package
		if (part.getPackageName().length() > 0) {			
			if(!hasImportForPartWithADifferentPackage) {
				importPackageName = part.getPackageName();
				importPartName = part.getPartName();
			}
		}

		EGLCompletionProposal eglCompletionProposal =
			new EGLCompletionProposal(viewer,
				displayString,
				proposalString,
				getPartReferenceAdditionalInformation(part.getPackageName(), part, partTypeName),
				getDocumentOffset() - getPrefix().length(),
				getPrefix().length(),
				getCursorPosition(proposalString),
				getPartTypeImgKeyStr(partTypeName));

		eglCompletionProposal.setImportPackageName(importPackageName);
		eglCompletionProposal.setImportPartName(importPartName);
		
		return eglCompletionProposal;
	}
	
	protected int getCursorPosition(String proposalString) {
		return proposalString.length();
	}
	
	protected String getPartTypeImgKeyStr(String partType){
		String partTypeImgStr = "";
		if (IEGLConstants.KEYWORD_RECORD == partType) {
			partTypeImgStr = PluginImages.IMG_OBJS_RECORD;
		}else if(IEGLConstants.KEYWORD_HANDLER == partType){
			partTypeImgStr = PluginImages.IMG_OBJS_HANDLER;
		}else if(IEGLConstants.KEYWORD_INTERFACE == partType){
			partTypeImgStr = PluginImages.IMG_OBJS_INTERFACE;
		}else if(IEGLConstants.KEYWORD_DELEGATE == partType){
			partTypeImgStr = PluginImages.IMG_OBJS_DELEGATE;
		}else if(IEGLConstants.KEYWORD_EXTERNALTYPE == partType){
			partTypeImgStr = PluginImages.IMG_OBJS_EXTERNALTYPE;
		}else if(IEGLConstants.KEYWORD_SERVICE == partType){
			partTypeImgStr = PluginImages.IMG_OBJS_SERVICE;
		}else if(IEGLConstants.KEYWORD_ENUMERATION == partType){
			partTypeImgStr = PluginImages.IMG_OBJS_ENUMERATION;
		}else if(IEGLConstants.KEYWORD_PROGRAM == partType){
			partTypeImgStr = PluginImages.IMG_OBJS_PGM;
		}else if(IEGLConstants.KEYWORD_LIBRARY == partType){
			partTypeImgStr = PluginImages.IMG_OBJS_LIBRARY;
		}else if(IEGLConstants.KEYWORD_CLASS == partType){
			partTypeImgStr = PluginImages.IMG_OBJS_CLASS;
		}
		
		return partTypeImgStr;
	}

	private boolean hasImportForPartWithADifferentPackage(final String packageName, final String partName) {
		IEGLDocument document = (IEGLDocument) ((EGLEditor) editor).getDocumentProvider().getDocument(editor.getEditorInput());
		File newModelEGLFile = document.getNewModelEGLFile();
		final boolean[] result = new boolean[] {false};
		newModelEGLFile.accept(new DefaultASTVisitor() {
			public boolean visit(File file) {
				return true;
			}
			public boolean visit(ImportDeclaration importDeclaration) {
				if(!importDeclaration.isOnDemand()) {
					Name name = importDeclaration.getName();
					if(name.isQualifiedName()) {
						Name qualifier = ((QualifiedName) name).getQualifier();
						if(name.getIdentifier().equalsIgnoreCase(partName) &&
						   !qualifier.getCanonicalName().equalsIgnoreCase(packageName)) {
							result[0] = true;
						}
					}
				}
				return false;
			}
		});
		return result[0];
	}

	/**
	 * 
	 */
	private String buildDisplayName(PartDeclarationInfo part, String partTypeName, String packageName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(part.getPartName());
		buffer.append(" - "); //$NON-NLS-1$
		buffer.append(packageName);
		buffer.append(" ("); //$NON-NLS-1$
		buffer.append(partTypeName);
		buffer.append(")"); //$NON-NLS-1$
		return buffer.toString();
	}

	
	protected String getPackageName(org.eclipse.edt.mof.egl.Part part) {
		String packageName = part.getCaseSensitivePackageName();
		if(packageName != null && packageName.length() != 0) {
			return packageName;
		}
		return UINlsStrings.OpenPartDialog_DefaultPackage;
	}

	protected String getProposalString(PartDeclarationInfo part, boolean includePackageName) {
		return includePackageName ? part.getPackageName() + "." + part.getPartName() : part.getPartName();
	}
	
	protected String getDisplayString(FunctionMember functionBinding) {
		return getDisplayString(functionBinding, functionBinding.getParameters().size());
	}
	
	protected boolean isPrivateMember(Member mbr){
		return mbr != null && mbr.getAccessKind() == AccessKind.ACC_PRIVATE;
	}
	
	protected String getDisplayString(FunctionMember function, int numberOfParameters) {
		StringBuffer buffer = new StringBuffer(function.getCaseSensitiveName());
		buffer.append("("); //$NON-NLS-1$
		
		int i = 1;
		for (FunctionParameter parm : function.getParameters()) {
			
			buffer.append(parm.getCaseSensitiveName());
			buffer.append(" "); //$NON-NLS-1$
			buffer.append(getTypeString(parm.getType()));
			if(i < numberOfParameters) {
				buffer.append(", "); //$NON-NLS-1$
			}

			i = i + 1;
		}
		
		buffer.append(")"); //$NON-NLS-1$
		Type returnType = function.getReturnType();
		if(returnType != null) {
			buffer.append(" : "); //$NON-NLS-1$
			buffer.append(getTypeString(returnType));
		}
		buffer.append(" - " + getNameFromElement(function.getContainer()));	
		return buffer.toString();
	}
	
	protected List createFunctionInvocationProposals(FunctionMember function, String additionalInformation, int relevance, boolean addPrefix) {
		List proposals = new ArrayList();
		String prefix = ""; //$NON-NLS-1$
		if (addPrefix) {
			prefix = getNameFromElement(function.getContainer()) + "."; //$NON-NLS-1$
		}
		String upToParensString = prefix + function.getCaseSensitiveName();
		int numberOfArgs = function.getParameters().size();
		String displayString = getDisplayString(function, numberOfArgs);
		String replaceString = upToParensString + "(" + getParmString(function, numberOfArgs) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		int selectionLength = numberOfArgs > 0 ? function.getParameters().get(0).getName().length() : 0;
		int cursorPosition = numberOfArgs > 0 ? upToParensString.length()+1 : replaceString.length();
		String imgStr = function.getAccessKind() == AccessKind.ACC_PRIVATE? PluginImages.IMG_OBJS_PRIVATE_FUNCTION : PluginImages.IMG_OBJS_FUNCTION;
		
		EGLCompletionProposal completionProposal = new EGLCompletionProposal(viewer,
			displayString,
			replaceString,
			additionalInformation,
			getDocumentOffset() - getPrefix().length(),
			getPrefix().length(),
			cursorPosition,
			relevance,
			selectionLength,
			imgStr);
		EGLProposalContextInformation contextInformation = new EGLProposalContextInformation(completionProposal, getDocumentOffset() - getPrefix().length() + upToParensString.length() + 1, replaceString, getParameterListString(function.getParameters()));				
		completionProposal.setContextInformation(contextInformation);
		proposals.add(completionProposal);
		return proposals;
	}

	/**
	 * @param numberOfArgs
	 * @return
	 */

	protected String getPartTypeString(ITypeBinding typeBinding) {
		if (typeBinding != null) {
			switch(typeBinding.getKind()) {
				case ITypeBinding.FLEXIBLE_RECORD_BINDING :
					return IEGLConstants.KEYWORD_RECORD;
				case ITypeBinding.LIBRARY_BINDING :
					return IEGLConstants.KEYWORD_LIBRARY;
				case ITypeBinding.ARRAY_TYPE_BINDING :
					return UINlsStrings.CAProposal_Array;
				case ITypeBinding.DICTIONARY_BINDING :
					return IEGLConstants.MIXED_DICTIONARY_STRING;
				case ITypeBinding.SERVICE_BINDING :
					return IEGLConstants.KEYWORD_SERVICE;
				case ITypeBinding.INTERFACE_BINDING :
					return IEGLConstants.KEYWORD_INTERFACE;
				default :
					return typeBinding.getPackageQualifiedName();
			}
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	protected boolean isNewExpression() {
		Node node = ((IEGLDocument) getViewer().getDocument()).getNewModelNodeAtOffset(getDocumentOffset());
		while (node != null) {
			if (node instanceof NewExpression)
				return true;
			if (node instanceof FunctionDataDeclaration)
				return false;
			if (node instanceof ClassDataDeclaration)
				return false;
			node = node.getParent();
		}
		return false;
	}
	
	protected String getParmString(FunctionMember function) {
		return getParmString(function, function.getParameters().size());
	}
	
	protected String getParmString(FunctionMember function, int numberOfParms) {
		StringBuffer buffer = new StringBuffer();
		boolean first = true;
		for (FunctionParameter parm : function.getParameters()) {
			if (!first)
				buffer.append(", "); //$NON-NLS-1$
			first = false;
			buffer.append(parm.getCaseSensitiveName());
		}
		
		return buffer.toString();
	}
	
	protected int getFirstParmLength(FunctionMember function) {
		List<FunctionParameter> parameters = function.getParameters();
		if (parameters != null && !parameters.isEmpty())
			return parameters.get(0).getName().length();
		return 0;
	}
	
	protected String getParameterListString(List<FunctionParameter> parameters) {
		StringBuffer parameterListSB = new StringBuffer();
		boolean first = true;
		for(FunctionParameter parm : parameters) {
			if (!first) {
				parameterListSB.append(", ");
			}
			first = false;
			parameterListSB.append(toString(parm));
		}
		return parameterListSB.toString();
	}
	
	protected String toString(FunctionParameter parm) {
		StringBuffer sb = new StringBuffer();
		sb.append(parm.getCaseSensitiveName());
		sb.append(" ");
		Type type = parm.getType();
		sb.append(getTypeString(type));		
		if(parm.getParameterKind() == ParameterKind.PARM_IN) {
			sb.append(" ");
			sb.append(IEGLConstants.KEYWORD_IN);
		}
		else if(parm.getParameterKind() == ParameterKind.PARM_OUT) {
			sb.append(" ");
			sb.append(IEGLConstants.KEYWORD_OUT);
		}		
		else if(parm.getParameterKind() == ParameterKind.PARM_INOUT) {
			sb.append(" ");
			sb.append(IEGLConstants.KEYWORD_INOUT);
		}
		
		return sb.toString();
	}
	
	protected int getLParenOffsetAfter(int offset) {
		char ch = ' ';
		try {
			while(ch != '(') {
				ch = getViewer().getDocument().getChar(offset++);
			}
		}
		catch(BadLocationException e) {
			throw new RuntimeException(e);
		}
		return offset-1;
	}
}
