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
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFieldsFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionMemberSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLAbstractReferenceCompletion.IBoundNodeProcessor;
import org.eclipse.jface.text.ITextViewer;

public class EGLUsingStatementReferenceCompletion extends
		EGLAbstractReferenceCompletion {

	@Override
	protected void precompileContexts() {
		addContext("package a; handler a function a() get a using"); //$NON-NLS-1$
		addContext("package a; handler a function a() get a from b using "); //$NON-NLS-1$
		addContext("package a; handler a function a() open a using"); //$NON-NLS-1$
		addContext("package a; handler a function a() open a from b using "); //$NON-NLS-1$
		addContext("package a; handler a function a() delete from b using "); //$NON-NLS-1$
		addContext("package a; handler a function a() delete a from b using  "); //$NON-NLS-1$
		addContext("package a; handler a function a() replace a to b using  "); //$NON-NLS-1$
		addContext("package a; handler a function a() execute using "); //$NON-NLS-1$
		addContext("package a; handler a function a() call a using "); //$NON-NLS-1$
	}

	@Override
	protected List returnCompletionProposals(ParseStack parseStack,
			final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		getBoundASTNodeForOffsetInStatement(viewer, documentOffset, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {
				proposals.addAll(
					new EGLDeclarationProposalHandler(viewer,
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
			}
		});
		
		//Get all proposals for parts that have static memebers
		proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(getSearchConstantsForPartsWithStaticMembers()));
		
		return proposals;
	}

}
