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
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFieldsFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionMemberSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLAbstractReferenceCompletion.IBoundNodeProcessor;
import org.eclipse.jface.text.ITextViewer;

public class EGLForToStatementReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; handler a function a() for(a from "); //$NON-NLS-1$
		addContext("package a; handler a function a() for(a int from "); //$NON-NLS-1$
		addContext("package a; handler a function a() for(a from 1 to"); //$NON-NLS-1$
		addContext("package a; handler a function a() for(a int from 1 to"); //$NON-NLS-1$
		addContext("package a; handler a function a() for(a from 1 to 2 by"); //$NON-NLS-1$
		addContext("package a; handler a function a() for(a int from 1 to 2 by"); //$NON-NLS-1$
		addContext("package a; handler a function a() for(a from 1 to 2 decrement by"); //$NON-NLS-1$
		addContext("package a; handler a function a() for(a int from 1 to 2 decrement by"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
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
