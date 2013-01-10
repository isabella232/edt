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
package org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.ForStatement;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Statement;
import org.eclipse.edt.compiler.core.ast.WhileStatement;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.EGLCompletionProposal;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLLabelPrecedingControlStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLLabelPrecedingControlStatementProposalHandler.IncludeLabelForTester;
import org.eclipse.jface.text.ITextViewer;

public class EGLContinueStatementReferenceCompletion extends EGLAbstractReferenceCompletion implements IncludeLabelForTester {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; handler a function a() continue"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		final IncludeLabelForTester thisReferenceCompletion = this;
		
		getBoundASTNodeForOffsetInStatement(viewer, documentOffset, new IBoundNodeProcessor() {public void processBoundNode(Node boundNode) {
			proposals.addAll(new EGLLabelPrecedingControlStatementProposalHandler(viewer, documentOffset, prefix).getProposals(boundNode, thisReferenceCompletion, EGLCompletionProposal.RELEVANCE_KEYWORD, EGLCompletionProposal.STR_IMG_KEYWORD));
		}});

		return proposals;
	}

	public boolean includeLabelFor(Statement statement) {
		return statement instanceof ForStatement ||
		       statement instanceof ForEachStatement ||
		       statement instanceof WhileStatement;
		
	}
}
