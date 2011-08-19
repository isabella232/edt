/*******************************************************************************
 * Copyright Ã¦Â¼?2000, 2011 IBM Corporation and others.
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
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLGetStatementReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() get"); //$NON-NLS-1$
		//This is to support path calls (multi-segment) in DLI
		addContext("package a; function a() get a,"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();

		if (isState(parseStack, ((Integer) validStates.get(0)).intValue())
				&& !isState(parseStack, getState("package a; function a() get rcd into")) //$NON-NLS-1$
				&& !isState(parseStack, getState("package a; function a() get next rcd into")) //$NON-NLS-1$
				&& !isState(parseStack, getState("package a; function a() get rcd usingKeys"))) { //$NON-NLS-1$
			getBoundASTNodeForOffsetInStatement(viewer, documentOffset, new IBoundNodeProcessor() {public void processBoundNode(Node boundNode) {
				//add SQL variable record proposals allowing arrays
				proposals.addAll(new EGLDeclarationProposalHandler(
					viewer,
					documentOffset,
					prefix,
					boundNode).getRecordProposals(
						EGLDeclarationProposalHandler.SQL_RECORD, true));
				
				//add other variable record proposals
				proposals.addAll(
					new EGLDeclarationProposalHandler(
						viewer,
						documentOffset,
						prefix,
						boundNode).getRecordProposals(
							EGLDeclarationProposalHandler.INDEXED_RECORD |
							EGLDeclarationProposalHandler.RELATIVE_RECORD));
				
				//add DLI segment variable record proposals (including arrays)
				proposals.addAll(
					new EGLDeclarationProposalHandler(
						viewer,
						documentOffset,
						prefix,
						boundNode).getRecordProposals(
							EGLDeclarationProposalHandler.DLI_SEGMENT, true));
			}});
		}
		return proposals;
	}
}
