/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

public class EGLSQLAddStatementReferenceCompletion extends EGLAbstractReferenceCompletion {

	protected void precompileContexts() {
		addContext("package a; function a() add"); //$NON-NLS-1$
		addContext("package a; function a() add a,"); //$NON-NLS-1$
	}

	protected List returnCompletionProposals(final ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		if (isState(parseStack, ((Integer) validStates.get(0)).intValue())) {
			getBoundASTNodeForOffsetInStatement(viewer, documentOffset, new IBoundNodeProcessor() {
				public void processBoundNode(Node boundNode) {
					proposals.addAll(new EGLDeclarationProposalHandler(viewer,
							documentOffset,
							prefix,
							boundNode).getSQLActioniTargets());
				}
			});
		}
		return proposals;
	}
	
}
