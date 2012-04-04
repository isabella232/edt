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
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLPartSearchExceptionVariableDeclarationProposalHandler extends EGLPartSearchProposalHandler {

	/**
	 * @param viewer
	 * @param documentOffset
	 * @param prefix
	 * @param editor
	 */
	public EGLPartSearchExceptionVariableDeclarationProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor) {
		super(viewer, documentOffset, prefix, editor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAbstractProposalHandler#getProposalString(com.ibm.etools.egl.model.internal.core.search.PartDeclarationInfo)
	 */
	protected String getProposalString(PartDeclarationInfo part, boolean includePackageName) {
		String partName = part.getPartName();
		StringBuffer buffer = new StringBuffer();
		buffer.append("exception "); //$NON-NLS-1$
		buffer.append(partName);
		return buffer.toString();
	}

}
