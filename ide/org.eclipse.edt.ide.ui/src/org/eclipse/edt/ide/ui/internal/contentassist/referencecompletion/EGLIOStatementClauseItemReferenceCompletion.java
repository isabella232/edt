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

public class EGLIOStatementClauseItemReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() get rcd usingKeys"); //$NON-NLS-1$
		addContext("package a; function a() get rcd usingKeys item1,"); //$NON-NLS-1$
		addContext("package a; function a() delete rcd usingKeys"); //$NON-NLS-1$
		addContext("package a; function a() replace rcd usingKeys"); //$NON-NLS-1$
		addContext("package a; function a() open resultSet for sqlRec usingKeys"); //$NON-NLS-1$
		addContext("package a; function a() get rcd into"); //$NON-NLS-1$
		addContext("package a; function a() open resultSet for sqlRec into"); //$NON-NLS-1$
		addContext("package a; function a() get rcd using"); //$NON-NLS-1$
		addContext("package a; function a() open resultSet for sqlRec using"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		boolean isState = false;
		for(int i  = 0; i < validStates.size() && !isState; i+=2) {
			isState = isState(parseStack, ((Integer) validStates.get(i)).intValue());
		}
		if(isState) {
			//add all variable proposals
			getBoundASTNodeForOffsetInStatement(viewer, documentOffset, new IBoundNodeProcessor() {public void processBoundNode(Node boundNode) {
				proposals.addAll(new EGLDeclarationProposalHandler(
					viewer,
					documentOffset,
					prefix,
					boundNode).getProposals(false, false));
			}});			
		}
		return proposals;
	}
}
