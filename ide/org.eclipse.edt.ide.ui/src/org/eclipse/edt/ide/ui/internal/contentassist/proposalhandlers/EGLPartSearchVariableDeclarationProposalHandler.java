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

import org.eclipse.edt.ide.core.internal.search.PartDeclarationInfo;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLPartSearchVariableDeclarationProposalHandler extends EGLPartSearchProposalHandler {

	private boolean includeSemiInReplacement;

	/**
	 * @param viewer
	 * @param documentOffset
	 * @param prefix
	 * @param editor
	 */
	public EGLPartSearchVariableDeclarationProposalHandler(
			ITextViewer viewer,
			int documentOffset,
			String prefix,
			IEditorPart editor) {
		this(viewer, documentOffset, prefix, editor, true);
	}
	
	public EGLPartSearchVariableDeclarationProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor,
		boolean includeSemiInReplacement) {
		super(viewer, documentOffset, prefix, editor);
		this.includeSemiInReplacement = includeSemiInReplacement;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAbstractProposalHandler#getProposalString(com.ibm.etools.egl.model.internal.core.search.PartDeclarationInfo)
	 */
	protected String getProposalString(PartDeclarationInfo part, boolean includePackageName) {
		//Usability enhancement to always add @bindService when declaring an interface or service
		String bindServiceString = ""; //$NON-NLS-1$
		if (part.getPartType() == IEGLSearchConstants.INTERFACE || part.getPartType() == IEGLSearchConstants.SERVICE) {
			bindServiceString = " {@Resource}"; //$NON-NLS-1$
		}
		String partName = part.getPartName();
		StringBuffer buffer = new StringBuffer();
		buffer.append(partName.substring(0,1).toLowerCase());
		buffer.append(partName.substring(1));
		buffer.append(" "); //$NON-NLS-1$
		if(includePackageName) {
			buffer.append(part.getPackageName() + "." + partName);			
		}
		else {
			buffer.append(partName);
		}
		buffer.append("?");
		buffer.append(bindServiceString);
		if(includeSemiInReplacement) {
			buffer.append(";"); //$NON-NLS-1$
		}
		return buffer.toString();
	}

}
