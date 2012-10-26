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
import java.util.Collection;
import java.util.List;

import org.eclipse.edt.compiler.core.ast.AbstractASTPartVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFieldsFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionMemberSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchVariableDeclarationProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLNewLogicLineReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a()"); //$NON-NLS-1$
		addContext("package a; function a() var a;"); //$NON-NLS-1$
		addContext("package a; program a function a()"); //$NON-NLS-1$

		addContext("package a; function a() if ()"); //$NON-NLS-1$
		addContext("package a; function a() if () else"); //$NON-NLS-1$
		addContext("package a; function a() while ()"); //$NON-NLS-1$
		addContext("package a; function a() try"); //$NON-NLS-1$
		addContext("package a; function a() try onException(a b)"); //$NON-NLS-1$
		addContext("package a; function a() try onException"); //$NON-NLS-1$
		addContext("package a; function a() case(a) when (a)"); //$NON-NLS-1$
		addContext("package a; function a() case(a) otherwise"); //$NON-NLS-1$
		addContext("package a; function a() for(a to 2)"); //$NON-NLS-1$
		addContext("package a; function a() for(a int to 2)"); //$NON-NLS-1$
		addContext("package a; function a() foreach (a)"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLAbstractReferenceCompletion#returnCompletionProposals(org.eclipse.edt.ide.core.internal.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		
		getBoundASTNodeForOffsetInStatement(viewer, documentOffset, new IBoundNodeProcessor() {public void processBoundNode(Node boundNode) {
			//Get all variable proposals
			proposals.addAll(
				new EGLDeclarationProposalHandler(viewer,
					documentOffset,
					prefix,
					boundNode)
					.getProposals(boundNode));
			
			//Get user field proposals using library use statements
			proposals.addAll(
				new EGLFieldsFromLibraryUseStatementProposalHandler(viewer, documentOffset, prefix, editor, boundNode).getProposals());
			
			//Get user function proposals with no return value using library use statements
			proposals.addAll(
				new EGLFunctionFromLibraryUseStatementProposalHandler(viewer, documentOffset, prefix, editor, false, boundNode).getProposals());
			
			//Get user function proposals with no return value
			proposals.addAll(
				new EGLFunctionMemberSearchProposalHandler(viewer, documentOffset, prefix, editor, false, boundNode).
					getProposals());
		}});			

		//Get all library proposals
		proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(
			IEGLSearchConstants.LIBRARY));

		//Get types to declare proposals
		proposals.addAll(getTypesToDeclare(prefix, viewer, documentOffset));

		return proposals;
	}

	private Collection getTypesToDeclare(String prefix, ITextViewer viewer, int documentOffset) {
		List proposals = new ArrayList();
		//Get all data part names
		Node eglPart = getPart(viewer, documentOffset);
		final String[] newPartName = new String[] {null};
		eglPart.accept(new AbstractASTPartVisitor() {
			public void visitPart(Part part) {
				newPartName[0] = part.getName().getCanonicalName();
			}
		});
		if (eglPart != null)
			proposals.addAll(
				new EGLPartSearchVariableDeclarationProposalHandler(
					viewer,
					documentOffset,
					prefix,
					editor).getProposals(
						getSearchConstantsForDeclarableParts(),
						newPartName[0]));

		return proposals;
	}
}
