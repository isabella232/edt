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

import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.jface.text.ITextViewer;

public class EGLDeclarationArrayProposalHandler extends EGLDeclarationProposalHandler {

	/**
	 * @param viewer
	 * @param documentOffset
	 * @param prefix
	 * @param eglPart
	 * @param nestedPart
	 */
	public EGLDeclarationArrayProposalHandler(
		ITextViewer viewer,
		int documentOffset,
		String prefix,
		Node boundNode) {
		super(viewer, documentOffset, prefix, boundNode);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler#precondition()
	 */
	protected boolean precondition(IDataBinding dBinding) {
		ITypeBinding tBinding = dBinding.getType();
		if(tBinding != null) {
			return ITypeBinding.ARRAY_TYPE_BINDING == tBinding.getKind();
		}
		
		return false;
	}

}
