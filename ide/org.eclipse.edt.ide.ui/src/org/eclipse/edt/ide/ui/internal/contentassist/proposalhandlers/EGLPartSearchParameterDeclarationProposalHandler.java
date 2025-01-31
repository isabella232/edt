/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
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

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLPartSearchParameterDeclarationProposalHandler extends
		EGLPartSearchVariableDeclarationProposalHandler {

	public EGLPartSearchParameterDeclarationProposalHandler(ITextViewer viewer,
			int documentOffset, String prefix, IEditorPart editor) {
		super(viewer, documentOffset, prefix, editor);
	}

	protected String getSuffix() {
		return ", ";
	}
}
