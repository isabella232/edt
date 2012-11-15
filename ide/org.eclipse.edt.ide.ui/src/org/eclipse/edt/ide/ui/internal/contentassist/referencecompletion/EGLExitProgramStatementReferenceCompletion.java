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

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.UINlsStrings;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLExitProgramStatementReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; handler a function a() exit Program("); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		int prefixLength = prefix.length();
		if (IEGLConstants.KEYWORD_SYSVAR.toUpperCase().startsWith(prefix.toUpperCase())) {
			String replacementString = IEGLConstants.KEYWORD_SYSVAR + "." + IEGLConstants.SYSTEM_WORD_RETURNCODE; //$NON-NLS-1$
			proposals.add(
				new EGLCompletionProposal(viewer,
					null,
					replacementString,
					UINlsStrings.CAProposal_ExitProgramStatement,
					documentOffset - prefixLength,
					prefixLength,
					replacementString.length(),
					EGLCompletionProposal.NO_IMG_KEY));
		}

		getBoundASTNodeForOffsetInStatement(viewer, documentOffset, new IBoundNodeProcessor() {public void processBoundNode(Node boundNode) {
			//add variable numeric item proposals
			proposals.addAll(new EGLDeclarationProposalHandler(
				viewer,
				documentOffset,
				prefix,
				boundNode).getDataItemProposals(EGLDeclarationProposalHandler.NUMERIC_DATAITEM));
			
			//add variable record proposals
			proposals.addAll(new EGLDeclarationProposalHandler(
				viewer,
				documentOffset,
				prefix,
				boundNode).getRecordProposals(EGLDeclarationProposalHandler.ALL_RECORDS, boundNode, false, false, false));
		}});

		return proposals;
	}
}
