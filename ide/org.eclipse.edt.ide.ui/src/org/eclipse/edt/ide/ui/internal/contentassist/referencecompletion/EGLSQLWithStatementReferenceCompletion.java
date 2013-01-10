/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFieldsFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionMemberSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLAbstractReferenceCompletion.IBoundNodeProcessor;
import org.eclipse.jface.text.ITextViewer;

public class EGLSQLWithStatementReferenceCompletion extends EGLAbstractReferenceCompletion {
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; handler a function a() get a with "); //$NON-NLS-1$
		addContext("package a; handler a function a() get a from a with "); //$NON-NLS-1$
		addContext("package a; handler a function a() execute with "); //$NON-NLS-1$
		addContext("package a; handler a function a() delete from a with "); //$NON-NLS-1$
		addContext("package a; handler a function a() delete a from a with "); //$NON-NLS-1$
		addContext("package a; handler a function a() open a with "); //$NON-NLS-1$
		addContext("package a; handler a function a() open a from a with "); //$NON-NLS-1$
		addContext("package a; handler a function a() replace a to a with "); //$NON-NLS-1$
		addContext("package a; handler a function a() prepare a from a with "); //$NON-NLS-1$
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(
		ParseStack parseStack,
		final String prefix,
		final ITextViewer viewer,
		final int documentOffset) {
		final ArrayList proposals = new ArrayList();
		String message;
		int prefixLength = prefix.length();
		if (("#" + IEGLConstants.SQLKEYWORD_SQL).toUpperCase().startsWith(prefix.toUpperCase())) { //$NON-NLS-1$
			message = "#" + IEGLConstants.SQLKEYWORD_SQL + "{}"; //$NON-NLS-1$ //$NON-NLS-2$
			proposals.add(new EGLCompletionProposal(viewer, message, message,
					UINlsStrings.CAProposal_EGLKeyword, documentOffset
							- prefixLength, prefixLength, message.length() - 1,
					EGLCompletionProposal.RELEVANCE_KEYWORD + 1,
					EGLCompletionProposal.NO_IMG_KEY));
		}

		getBoundASTNodeForOffsetInStatement(viewer, documentOffset, new IBoundNodeProcessor() {public void processBoundNode(Node boundNode) {
			//Get all variable proposals
			proposals.addAll(
				new EGLDeclarationProposalHandler(
					viewer,
					documentOffset,
					prefix,
					boundNode)
					.getProposals(boundNode));
			
			//Get user field proposals using library use statements
			proposals.addAll(
				new EGLFieldsFromLibraryUseStatementProposalHandler(viewer, documentOffset, prefix, editor, boundNode).getProposals());

			//Get user function proposals with return value using library use statements
			proposals.addAll(
				new EGLFunctionFromLibraryUseStatementProposalHandler(viewer, documentOffset, prefix, editor, true, boundNode).getProposals());
			
			//Get user function proposals with return value
			proposals.addAll(
				new EGLFunctionMemberSearchProposalHandler(viewer, documentOffset, prefix, editor, true, boundNode).getProposals());
		}});
		
		//Get all proposals for parts that contain static members
		proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(getSearchConstantsForPartsWithStaticMembers()));
		
		return proposals;
	}

}
