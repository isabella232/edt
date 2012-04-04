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
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLForToStatementReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() for(a from "); //$NON-NLS-1$
		addContext("package a; function a() for(a int from "); //$NON-NLS-1$
		addContext("package a; function a() for(a from 1 to"); //$NON-NLS-1$
		addContext("package a; function a() for(a int from 1 to"); //$NON-NLS-1$
		addContext("package a; function a() for(a from 1 to 2 by"); //$NON-NLS-1$
		addContext("package a; function a() for(a int from 1 to 2 by"); //$NON-NLS-1$
		addContext("package a; function a() for(a from 1 to 2 decrement by"); //$NON-NLS-1$
		addContext("package a; function a() for(a int from 1 to 2 decrement by"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		getBoundASTNodeForOffsetInStatement(viewer, documentOffset, new IBoundNodeProcessor() {public void processBoundNode(Node boundNode) {
			//Get all integer data item variable proposals
			proposals.addAll(
				new EGLDeclarationProposalHandler(viewer,
					documentOffset,
					prefix,
					boundNode)
						.getDataItemProposals(EGLDeclarationProposalHandler.ALL_DATAITEMS));
			
			//Get all record variable proposals
			proposals.addAll(
				new EGLDeclarationProposalHandler(viewer,
					documentOffset,
					prefix,
					boundNode)
						.getRecordProposals(EGLDeclarationProposalHandler.ALL_RECORDS));
		}});

		return proposals;
	}

}
