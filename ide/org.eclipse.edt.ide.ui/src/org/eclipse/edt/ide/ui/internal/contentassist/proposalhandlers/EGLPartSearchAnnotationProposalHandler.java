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

import java.util.List;

import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.ui.internal.PluginImages;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLPartSearchAnnotationProposalHandler extends EGLPartSearchProposalHandler {

	private boolean addPrefix;
	private List<AnnotationType> exclude;
	/**
	 * @param viewer
	 * @param documentOffset
	 * @param prefix
	 * @param editor
	 */
	public EGLPartSearchAnnotationProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor,
		boolean addPrefix,
		List<AnnotationType> exclude) {
		super(viewer, documentOffset, prefix, editor);
		this.addPrefix = addPrefix;
		this.exclude = exclude;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAbstractProposalHandler#getProposalString(com.ibm.etools.egl.model.internal.core.search.PartDeclarationInfo)
	 */
	protected String getProposalString(PartDeclarationInfo part, boolean includePackageName) {
		String partName = part.getPartName() + "{}";
		if (addPrefix) {
			return "@" + partName;
		}
		else {
			return partName;
		}
			
	}
	
	protected String getPartTypeImgKeyStr(String partType){
		if (IEGLConstants.KEYWORD_RECORD == partType) {
			return PluginImages.IMG_OBJS_ANNOTATION;
		}else  {
			return super.getPartTypeImgKeyStr(partType);
		}
	}
	
	protected boolean shouldInclude(PartDeclarationInfo partDeclarationInfo) {
		String qualifiedName;
		if (partDeclarationInfo.getPackageName().isEmpty()) {
			qualifiedName = partDeclarationInfo.getPartName();
		}
		else {
			qualifiedName = partDeclarationInfo.getPackageName() + "." + partDeclarationInfo.getPartName();
		}
		
		for (AnnotationType annType : exclude) {
			if(qualifiedName.equalsIgnoreCase(annType.getETypeSignature())) {
				return false;
			}
		}
		return true;
	}
	
	protected int getCursorPosition(String proposalString) {
		return proposalString.length() - 1;
	}



}
