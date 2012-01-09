/*******************************************************************************
 * Copyright ©2000, 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public abstract class EGLAbstractProgramOrTransferNameReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, String prefix, ITextViewer viewer, int documentOffset) {
		List proposals = new ArrayList();

		//Add program proposals
		proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(IEGLSearchConstants.PROGRAM));

		String sysVarName = IEGLConstants.KEYWORD_SYSVAR + "." + IEGLConstants.SYSTEM_WORD_TRANSFERNAME; //$NON-NLS-1$
		if (sysVarName.toUpperCase().startsWith(prefix.toUpperCase()))
			proposals.add(addSysVarProposal(prefix, viewer, documentOffset, sysVarName));

		return proposals;
	}

	/**
	 * @return
	 */
	private EGLCompletionProposal addSysVarProposal(String prefix, ITextViewer viewer, int documentOffset, String proposalString) {
		return new EGLCompletionProposal(viewer,
			null,
			proposalString,
			UINlsStrings.CAProposal_SystemVariable,
			documentOffset - prefix.length(),
			prefix.length(),
			proposalString.length(),
			EGLCompletionProposal.NO_IMG_KEY);
	}

}
