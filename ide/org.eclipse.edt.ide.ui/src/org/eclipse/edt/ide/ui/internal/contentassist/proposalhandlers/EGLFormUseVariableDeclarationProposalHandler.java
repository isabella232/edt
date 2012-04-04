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

import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLFormUseVariableDeclarationProposalHandler extends EGLFormUseStatementProposalHandler {

	/**
	 * @param viewer
	 * @param documentOffset
	 * @param prefix
	 * @param editor
	 */
	public EGLFormUseVariableDeclarationProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		IEditorPart editor,
		Node boundNode) {
			super(viewer, documentOffset, prefix, editor, boundNode);
	}
	
	protected String getProposalString(FormBinding formBinding, boolean quotes) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(formBinding.getCaseSensitiveName().substring(0,1).toLowerCase());
		buffer.append(formBinding.getCaseSensitiveName().substring(1));
		buffer.append(" "); //$NON-NLS-1$
		buffer.append(formBinding.getCaseSensitiveName());
		return buffer.toString();
	}
}
