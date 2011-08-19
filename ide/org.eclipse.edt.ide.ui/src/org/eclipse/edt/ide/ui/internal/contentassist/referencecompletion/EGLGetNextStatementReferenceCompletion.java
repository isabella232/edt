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
import org.eclipse.edt.compiler.internal.IEGLConstants;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLGetNextStatementReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		//also handles get previous, first, last, current, relative, absolute
		addContext("package a; function a() get next"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		final int[] recordTypes = new int[1];
		String nodeText = getNodeText2(parseStack, 2);
		if (nodeText.equalsIgnoreCase(IEGLConstants.KEYWORD_NEXT))
			recordTypes[0] = 
				EGLDeclarationProposalHandler.SQL_RECORD |
				EGLDeclarationProposalHandler.MQ_RECORD |
				EGLDeclarationProposalHandler.SERIAL_RECORD |
				EGLDeclarationProposalHandler.INDEXED_RECORD |
				EGLDeclarationProposalHandler.DLI_SEGMENT |
				EGLDeclarationProposalHandler.RELATIVE_RECORD;
		else if (nodeText.equalsIgnoreCase(IEGLConstants.KEYWORD_PREVIOUS))
			recordTypes[0] = 
				EGLDeclarationProposalHandler.SQL_RECORD |
				EGLDeclarationProposalHandler.INDEXED_RECORD;
		else if (nodeText.equalsIgnoreCase(IEGLConstants.KEYWORD_INPARENT))
			recordTypes[0] = EGLDeclarationProposalHandler.DLI_SEGMENT;
		else
			recordTypes[0] = EGLDeclarationProposalHandler.SQL_RECORD;
			
		//add variable record proposals
		getBoundASTNodeForOffsetInStatement(viewer, documentOffset, new IBoundNodeProcessor() {public void processBoundNode(Node boundNode) {
			proposals.addAll(new EGLDeclarationProposalHandler(
				viewer,
				documentOffset,
				prefix,
				boundNode).getRecordProposals(recordTypes[0]));
		}});
		return proposals;
	}
}
