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

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorPart;

public class EGLReferenceTypeProposalHandler extends EGLAbstractProposalHandler {

	public EGLReferenceTypeProposalHandler(ITextViewer viewer, int documentOffset, String prefix, IEditorPart editor) {
		super(viewer, documentOffset, prefix, editor);
	}

	public List getProposals(Node boundNode) {
		List proposals = new ArrayList();
		proposals.addAll(new EGLPartSearchProposalHandler(viewer, getDocumentOffset(), getPrefix(), editor).getProposals(
				IEGLSearchConstants.EXTERNALTYPE | IEGLSearchConstants.DELEGATE | IEGLSearchConstants.INTERFACE | 
				IEGLSearchConstants.SERVICE | IEGLSearchConstants.RECORD, "")); //$NON-NLS-1$
		
		if(boundNode != null) {
			proposals.addAll(new EGLAliasedTypeProposalHandler(viewer, getDocumentOffset(), getPrefix(), boundNode).getProposals());
		}
		return proposals;
	}
}
