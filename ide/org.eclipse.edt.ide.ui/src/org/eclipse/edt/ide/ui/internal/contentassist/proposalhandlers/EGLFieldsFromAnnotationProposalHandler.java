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

import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLFieldsFromAnnotationProposalHandler extends EGLAbstractProposalHandler {
	
	private static EType elistType;
	private static class ProposalInfo {
		String proposalString;
		int cursorOffset;
		int highlightLen;
	}
	
	private List<String> fieldsAlreadySpecified;
	/**
	 * @param viewer
	 * @param documentOffset
	 * @param prefix
	 * @param editor
	 * @param mustHaveReturnCode
	 */
	public EGLFieldsFromAnnotationProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor,
		List<String> fieldsAlreadySpecified ) {
		super(viewer, documentOffset, prefix, editor);
		this.fieldsAlreadySpecified = fieldsAlreadySpecified;
	}
	
	public List getProposals(AnnotationType annotationType) {
		List proposals = new ArrayList();
		
		List<EField> fields = annotationType.getEFields();
		
		for(EField field : fields) {
				if (field.getName().toUpperCase().startsWith(getPrefix().toUpperCase()) && !fieldsAlreadySpecified.contains(field.getName().toUpperCase().toLowerCase())) {
					proposals.add(createProposal(field, annotationType));
				}
		}
		return proposals;
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
	private EGLCompletionProposal createProposal(EField field, AnnotationType annotationType) {
		String proposalString = field.getCaseSensitiveName();
		ProposalInfo info = getProposalInfo(field, annotationType);
		
		return new EGLCompletionProposal(viewer,
			proposalString, //$NON-NLS-1$ //$NON-NLS-2$
			info.proposalString,
			getAdditionalInfo(field),
			getDocumentOffset() - getPrefix().length(),
			getPrefix().length(),
			info.cursorOffset,
			EGLCompletionProposal.RELEVANCE_MEMBER,
			info.highlightLen,
			PluginImages.IMG_OBJS_ENV_VAR);
	}
	
	private ProposalInfo getProposalInfo(EField field, AnnotationType annotationType) {
		
		ProposalInfo info = new ProposalInfo();

		info.proposalString = field.getCaseSensitiveName() + " = ";
		info.cursorOffset = field.getCaseSensitiveName().length() + 3;
		populateInfo(info, field);			
				
		return info;
	}
	
	private void populateInfo(ProposalInfo info, EField field) {
		populateInfo(info, field.getEType());
	}

	private void populateInfo(ProposalInfo info, EType eType) {
		
		if (isGenericElistType(eType)) {
			info.proposalString = info.proposalString + "[";
			info.cursorOffset = info.cursorOffset + 1;
			EType currType = ((EGenericType)eType).getETypeArguments().get(0);
			populateInfo(info, currType);
			info.proposalString = info.proposalString + "]";			
		}
		
		if (eType instanceof EDataType) {
			String className = ((EDataType)eType).getJavaClassName();
			if (className.equals(EDataType.EDataType_JavaObject)) {
				return;
			}

			if (className.equals(EDataType.EDataType_String)) {
				info.proposalString = info.proposalString + "\"\"";
				info.cursorOffset = info.cursorOffset + 1;
				return;
			}

			if (className.equals(EDataType.EDataType_Boolean)) {
				info.proposalString = info.proposalString + "yes";
				info.highlightLen = 3;
				return;
			}

			if (className.equals(EDataType.EDataType_Int32)) {
				info.proposalString = info.proposalString + "0";
				info.highlightLen = 1;
				return;
			}

			if (className.equals(EDataType.EDataType_Float)) {
				info.proposalString = info.proposalString + "0e0";
				info.highlightLen = 3;
				return;
			}
			
			if (className.equals(EDataType.EDataType_Decimal)) {
				info.proposalString = info.proposalString + "0.0";
				info.highlightLen = 3;
				return;
			}
		}
		if (eType instanceof AnnotationType) {
			AnnotationType annotationType = (AnnotationType) eType;
			String str = "@" + annotationType.getCaseSensitiveName() + "{}";
			info.cursorOffset = info.cursorOffset + str.length() - 1;			
			info.proposalString = info.proposalString + str;
			return;
		}

		
		
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
