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

public class EGLUsingStatementReferenceCompletion extends
		EGLAbstractReferenceCompletion {

	@Override
	protected void precompileContexts() {
		addContext("package a; function a() open a using( "); //$NON-NLS-1$
		addContext("package a; function a() open a using(b, "); //$NON-NLS-1$
		addContext("package a; function a() open a from b using( "); //$NON-NLS-1$
		addContext("package a; function a() open a from b using(c, "); //$NON-NLS-1$
		addContext("package a; function a() get a using( "); //$NON-NLS-1$
		addContext("package a; function a() get a using(b, "); //$NON-NLS-1$
		addContext("package a; function a() get a from b using( "); //$NON-NLS-1$
		addContext("package a; function a() get a from b using(c, "); //$NON-NLS-1$
		addContext("package a; function a() delete from b using( "); //$NON-NLS-1$
		addContext("package a; function a() delete from b using(c, "); //$NON-NLS-1$
		addContext("package a; function a() delete a from b using( "); //$NON-NLS-1$
		addContext("package a; function a() delete a from b using(c, "); //$NON-NLS-1$
		addContext("package a; function a() replace a to b using( "); //$NON-NLS-1$
		addContext("package a; function a() replace a to b using(c, "); //$NON-NLS-1$
		addContext("package a; function a() execute using("); //$NON-NLS-1$
		addContext("package a; function a() execute using(c, "); //$NON-NLS-1$
		addContext("package a; function a() call a using("); //$NON-NLS-1$
		addContext("package a; function a() call a using "); //$NON-NLS-1$
	}

	@Override
	protected List returnCompletionProposals(ParseStack parseStack,
			final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		if(isState(parseStack, ((Integer) validStates.get(0)).intValue()) 		||
				isState(parseStack, ((Integer) validStates.get(2)).intValue())	||
				isState(parseStack, ((Integer) validStates.get(4)).intValue())	||
				isState(parseStack, ((Integer) validStates.get(6)).intValue()) 	||
				isState(parseStack, ((Integer) validStates.get(8)).intValue()) 	||
				isState(parseStack, ((Integer) validStates.get(10)).intValue())	||
				isState(parseStack, ((Integer) validStates.get(12)).intValue())	||
				isState(parseStack, ((Integer) validStates.get(14)).intValue())	||
				isState(parseStack, ((Integer) validStates.get(16)).intValue())){
					getBoundASTNodeForOffsetInStatement(viewer, documentOffset,
					new IBoundNodeProcessor() {
						public void processBoundNode(Node boundNode) {
							proposals.addAll(new EGLDeclarationProposalHandler(
									viewer, documentOffset, prefix, boundNode).getProposals(null, false, true));
						}
					});
			
		}

		return proposals;
	}

}
