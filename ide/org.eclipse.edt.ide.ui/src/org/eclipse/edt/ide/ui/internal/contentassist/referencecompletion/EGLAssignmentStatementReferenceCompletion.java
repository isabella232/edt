/*******************************************************************************
 * Copyright © 2000, 2013 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFieldsFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionMemberSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.jface.text.ITextViewer;

public class EGLAssignmentStatementReferenceCompletion extends EGLAbstractReferenceCompletion {
	
	/**
	 * ONLY USEFUL IN CODE RUNNING WITHIN SPAN OF PROCESSBOUNDNODE METHOD BELOW
	 */
	private Node boundNode = null;
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
//		addContext("package a; handler a a atype ="); //$NON-NLS-1$
//		addContext("package a; handler a const a atype ="); //$NON-NLS-1$
		addContext("package a; handler a function a() a="); //$NON-NLS-1$
		addContext("package a; handler a function a() a int="); //$NON-NLS-1$
		addContext("package a; handler a function a() a int=["); //$NON-NLS-1$
		addContext("package a; handler a type b{c=["); //$NON-NLS-1$
		addContext("package a; handler a type b{c="); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(final ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		final boolean[] proposal = new boolean[1];


		getBoundASTNode(viewer, documentOffset, new String[] {"x", "x}", "x;", "\";", "x", "x;end end", "x}; end"}, new CompletedNodeVerifier() {
			public boolean nodeIsValid(Node astNode) {
				return getAssignment(astNode) != null;
			}
		}, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {
				
				Assignment ass = getAssignment(boundNode);
				if (ass == null) {
					proposal[0] = false;
				}
				else {
					if (ass.resolveBinding() != null) {
						proposal[0] = false;
					}
					else {
						proposal[0] = true;
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
						
						//Get user function proposals with return value using library use statements
						proposals.addAll(
							new EGLFunctionFromLibraryUseStatementProposalHandler(viewer, documentOffset, prefix, editor, true, boundNode).getProposals());
						
						//Get user function proposals with return value
							proposals.addAll(
									new EGLFunctionMemberSearchProposalHandler(viewer, documentOffset, prefix, editor, true, boundNode).getProposals());
					}
					
				}
			}
		});
		
		if (proposal[0]) {
			//Get proposals for types that can contain static members
			proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(getSearchConstantsForPartsWithStaticMembers()));
		}

		return proposals;
	}

	private Assignment getAssignment(Node node) {
		if (node == null) {
			return null;
		}
		if(	node instanceof Assignment) {
			return (Assignment)node;
		}
		
		return getAssignment(node.getParent());
	}

}
