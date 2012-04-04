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
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLConditionSysVarProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFormUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLSystemLibraryProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLSystemWordProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLTableUseStatementProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLBooleanExpressionReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() if ("); //$NON-NLS-1$
		addContext("package a; function a() if (a=="); //$NON-NLS-1$
		addContext("package a; function a() if (a!="); //$NON-NLS-1$
		addContext("package a; function a() if (a>"); //$NON-NLS-1$
		addContext("package a; function a() if (a>="); //$NON-NLS-1$
		addContext("package a; function a() if (a<"); //$NON-NLS-1$
		addContext("package a; function a() if (a<="); //$NON-NLS-1$
		addContext("package a; function a() if (a==b &&"); //$NON-NLS-1$
		addContext("package a; function a() if (a==b ||"); //$NON-NLS-1$
		
		addContext("package a; function a() while ("); //$NON-NLS-1$
		addContext("package a; function a() if (!"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();

		getBoundASTNodeForOffsetInStatement(viewer, documentOffset, new IBoundNodeProcessor() {public void processBoundNode(Node boundNode) {
			//Get all variable proposals
			proposals.addAll(
				new EGLDeclarationProposalHandler(
					viewer,
					documentOffset,
					prefix,
					boundNode)
					.getProposals(boundNode));
			
			//Get all table use statement proposals
			proposals.addAll(
				new EGLTableUseStatementProposalHandler(viewer,
					documentOffset,
					prefix,
					editor,
					boundNode).getProposals());

			//Get all forms from formGroup use statement proposals
			proposals.addAll(
				new EGLFormUseStatementProposalHandler(viewer,
					documentOffset,
					prefix,
					editor,
					boundNode).getProposals());
			
			//Get user function proposals with return value using library use statements
			proposals.addAll(
				new EGLFunctionFromLibraryUseStatementProposalHandler(viewer, documentOffset, prefix, editor, true, boundNode).getProposals());
			
			//Get system function proposals with return value
			proposals.addAll(
				new EGLSystemWordProposalHandler(viewer,
					documentOffset,
					prefix,
					editor,
					boundNode).getProposals(EGLSystemWordProposalHandler.RETURNS, true));

			//Get user function proposals with return value
			proposals.addAll(
				new EGLFunctionPartSearchProposalHandler(viewer, documentOffset, prefix, editor, true, boundNode).getProposals());
		}});
		
		//Get all library proposals
		proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(
				IEGLSearchConstants.LIBRARY));
		
		//Get all system library proposals
		proposals.addAll(
			new EGLSystemLibraryProposalHandler(viewer, documentOffset, prefix, editor).getProposals());
				
		//JON - not sure this should be here for all the cases above???
		proposals.addAll(
			new EGLConditionSysVarProposalHandler(viewer, documentOffset, prefix).getProposals());

		return proposals;
	}
}
