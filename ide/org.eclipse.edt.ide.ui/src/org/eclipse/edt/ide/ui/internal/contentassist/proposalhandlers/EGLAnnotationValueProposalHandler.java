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
package org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLAnnotationValueProposalHandler extends EGLAbstractProposalHandler {
	
	private static EType elistType;
	private static class ProposalInfo {
		String proposalString;
		int cursorOffset;
		int highlightLen;
	}
	
	/**
	 * @param viewer
	 * @param documentOffset
	 * @param prefix
	 * @param editor
	 * @param mustHaveReturnCode
	 */
	public EGLAnnotationValueProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor) {
		super(viewer, documentOffset, prefix, editor);
	}
	
	public List getProposals(EField field, Node boundNode) {
		return createProposals(field, boundNode);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAbstractProposalHandler#getAdditionalInfo(com.ibm.etools.egl.internal.pgm.bindings.EGLTypeBinding)
	 */
	private String getAdditionalInfo(EField field) {
		return "";
	}

	/**
	 * @param dataBinding
	 * @return
	 */
	private List createProposals(EField field, Node boundNode) {
		
		EType eType = field.getEType();
		
		int dimDiff = getListDimensions(eType, 0) - getLiteralArrayDimensions(boundNode);;
		if (dimDiff > 0) {
			String str = "";
			for (int i = 0; i < dimDiff; i++) {
				str = str + "[";
			}
			for (int i = 0; i < dimDiff; i++) {
				str = str + "]";
			}
			List list = new ArrayList();
			list.add(
					new EGLCompletionProposal(viewer,
					str, //$NON-NLS-1$ //$NON-NLS-2$
					str,
					getAdditionalInfo(field),
					getDocumentOffset() - getPrefix().length(),
					getPrefix().length(),
					dimDiff,
					EGLCompletionProposal.RELEVANCE_MEMBER,
					0,
					null));
			return list;
		}
		
		eType = getBaseType(eType);
		
		if (eType instanceof Enumeration) {
			return new EGLVariableDotProposalHandler(viewer, getDocumentOffset(), getPrefix(), editor, (Enumeration)eType, false).getProposals(true);
		}
		
		if (eType.getETypeSignature().equalsIgnoreCase("org.eclipse.edt.mof.egl.FieldReference")) {
			return new EGLDeclarationProposalHandler(viewer, getDocumentOffset(), getPrefix(), boundNode).getProposals(boundNode);
		}

		if (eType.getETypeSignature().equalsIgnoreCase("org.eclipse.edt.mof.egl.FunctionReference")) {
			return new EGLFunctionMemberSearchProposalHandler(viewer, getDocumentOffset(), getPrefix(), editor, true, boundNode, false).getProposals();
		}

		if (eType.getETypeSignature().equalsIgnoreCase("org.eclipse.edt.mof.egl.MemberReference")) {
			List list = new ArrayList();
			list.addAll(new EGLDeclarationProposalHandler(viewer, getDocumentOffset(), getPrefix(), boundNode).getProposals(boundNode));
			list.addAll(new EGLFunctionMemberSearchProposalHandler(viewer, getDocumentOffset(), getPrefix(), editor, true, boundNode, false).getProposals());
			return list;
		}


		if (getPrefix().length() != 0) {
			return new ArrayList();
		}

		ProposalInfo info = getProposalInfo(eType);
		if (info != null) {
			List list = new ArrayList();
			list.add(
					new EGLCompletionProposal(viewer,
					info.proposalString, //$NON-NLS-1$ //$NON-NLS-2$
					info.proposalString,
					getAdditionalInfo(field),
					getDocumentOffset() - getPrefix().length(),
					getPrefix().length(),
					info.cursorOffset,
					EGLCompletionProposal.RELEVANCE_MEMBER,
					info.highlightLen,
					null));
			return list;
		}
		return new ArrayList();
	}
	
	
	private ProposalInfo getProposalInfo(EType eType) {
		
		if (isGenericElistType(eType)) {
			EType currType = ((EGenericType)eType).getETypeArguments().get(0);
			return getProposalInfo(currType);
		}
		
		if (eType instanceof EDataType) {
			String className = ((EDataType)eType).getJavaClassName();
			if (className.equals(EDataType.EDataType_JavaObject)) {
				return null;
			}

			if (className.equals(EDataType.EDataType_String)) {
				ProposalInfo info = new ProposalInfo();
				info.proposalString = "\"\"";
				info.cursorOffset = 1;
				return info;
			}

			if (className.equals(EDataType.EDataType_Boolean)) {
				ProposalInfo info = new ProposalInfo();
				info.proposalString = "yes";
				info.highlightLen = 3;
				return info;
			}

			if (className.equals(EDataType.EDataType_Int32)) {
				ProposalInfo info = new ProposalInfo();
				info.proposalString = "0";
				info.highlightLen = 1;
				return info;
			}

			if (className.equals(EDataType.EDataType_Float)) {
				ProposalInfo info = new ProposalInfo();
				info.proposalString = "0e0";
				info.highlightLen = 3;
				return info;
			}
			
			if (className.equals(EDataType.EDataType_Decimal)) {
				ProposalInfo info = new ProposalInfo();
				info.proposalString = "0.0";
				info.highlightLen = 3;
				return info;
			}
		}
		if (eType instanceof AnnotationType) {
			ProposalInfo info = new ProposalInfo();
			AnnotationType annotationType = (AnnotationType) eType;
			String str = "@" + annotationType.getCaseSensitiveName() + "{}";
			info.cursorOffset = str.length() - 1;			
			info.proposalString = str;
			return info;
		}
		
		return null;
	}
	
	private int getLiteralArrayDimensions(Node node) {
		if (node != null && !(node instanceof ArrayLiteral)) {
			node = node.getParent();
		}
		int count = 0;
		while (node instanceof ArrayLiteral) {
			count = count + 1;
			node = node.getParent();
		}
		return count;
	}
	
	private int getListDimensions(EType type, int dimCount) {
		if (isGenericElistType(type)) {
			EType currType = ((EGenericType)type).getETypeArguments().get(0);
			return getListDimensions(currType, dimCount + 1);
		}
		return dimCount;
	}

	private EType getBaseType(EType type) {
		if (isGenericElistType(type)) {
			EType currType = ((EGenericType)type).getETypeArguments().get(0);
			return getBaseType(currType);
		}
		return type;
	}

	private boolean isGenericElistType (EType type) {
		if (type instanceof EGenericType) {
			try {
				EType elistType = getElistType();
				EClassifier eclassifier = ((EGenericType) type).getEClassifier();
				return elistType.equals(eclassifier);
			} catch (Exception e) {
			} 
		}
		return false;
	}

	private static EType getElistType() {
		if (elistType == null) {
			try {
				elistType = (EType)Environment.getCurrentEnv().findType(MofConversion.Type_EList);
			} catch (Exception e) {
			} 
		}
		return elistType;
	}
	
	
}
