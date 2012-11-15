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

public class EGLSQLFromStatementRefrerenceCompletion extends
		EGLAbstractReferenceCompletion {

	@Override
	protected void precompileContexts() {
		addContext("package a; handler a function a()	delete from "); //$NON-NLS-1$
		addContext("package a; handler a function a()	delete a from "); //$NON-NLS-1$
		addContext("package a; handler a function a() open a from "); //$NON-NLS-1$
		addContext("package a; handler a function a() get a from "); //$NON-NLS-1$
		addContext("package a; handler a function a() execute from "); //$NON-NLS-1$
		addContext("package a; handler a function a() prepare a from "); //$NON-NLS-1$
		addContext("package a; handler a function a() foreach(a from  "); //$NON-NLS-1$
	}

	@Override
	protected List returnCompletionProposals(ParseStack parseStack,
			final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		getBoundASTNodeForOffsetInStatement(viewer, documentOffset,
				new IBoundNodeProcessor() {
					public void processBoundNode(Node boundNode) {
						// SQL DataSource into proposals
						proposals.addAll(new EGLDeclarationProposalHandler(
								viewer, documentOffset, prefix, boundNode)
								.getDataSourceProposals());
					}
				});

		return proposals;
	}

}
