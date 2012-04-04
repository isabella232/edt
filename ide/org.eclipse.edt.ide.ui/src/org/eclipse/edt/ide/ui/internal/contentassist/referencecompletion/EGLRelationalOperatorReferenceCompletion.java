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
package org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLLogicalOperatorProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLRelationalOperatorProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLStringOperatorProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLRelationalOperatorReferenceCompletion
	extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() if (a"); //$NON-NLS-1$
		addContext("package a; function a() if (a[c].b"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(
		ParseStack parseStack,
		String prefix,
		ITextViewer viewer,
		int documentOffset) {
		
		List proposals = new ArrayList();
		if (isState(parseStack, ((Integer) validStates.get(0)).intValue())
			||isState(parseStack, ((Integer) validStates.get(1)).intValue())){
			//Get all relational operator proposals
			proposals =
				new EGLRelationalOperatorProposalHandler(
					viewer,
					documentOffset,
					prefix)
					.getProposals();
			proposals.addAll(
					new EGLLogicalOperatorProposalHandler(
						viewer,
						documentOffset,
						prefix)
						.getProposals());
			proposals.addAll(
					new EGLStringOperatorProposalHandler(
						viewer,
						documentOffset,
						prefix)
						.getProposals());
		}
		return proposals;
	}
}
