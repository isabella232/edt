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

import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFieldsFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionMemberSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLAbstractReferenceCompletion.CompletedNodeVerifier;
import org.eclipse.edt.ide.ui.internal.contentassist.referencecompletion.EGLAbstractReferenceCompletion.IBoundNodeProcessor;
import org.eclipse.jface.text.ITextViewer;

public class EGLExpressionPlusReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; handler a function a() call pgm (a,"); //$NON-NLS-1$		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(final ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		final boolean[] done = new boolean[1];
		getBoundASTNode(viewer, documentOffset, new String[] {"", "a", "a)", "a);"},
			new CompletedNodeVerifier() { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				public boolean nodeIsValid(Node astNode) {
					return (astNode instanceof Expression);
				}
			},
			new IBoundNodeProcessor() {public void processBoundNode(Node boundNode) {
			
				//EGLFunctionArgumentsReferenceCompletion handles this for functions and new expressions
				if (getInvocOrNew(boundNode) != null || isAnnoationValue(boundNode)) {
					done[0] = true;
					return;
				}
				
				proposals.addAll(
					new EGLDeclarationProposalHandler(viewer,
						documentOffset,
						prefix,
						boundNode)
							.getProposals(boundNode, false, true));
				
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
		
		if (!done[0]) {
			//Get all proposals for parts that have static memebers
			proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(getSearchConstantsForPartsWithStaticMembers()));
		}
		
		return proposals;
	}
	
	private Node getInvocOrNew(Node node) {
		
		if (node == null) {
			return null;
		}
		
		if (node instanceof FunctionInvocation) {
			return node;
		}
		
		if (node instanceof NewExpression) {
			return node;
		}
		
		return getInvocOrNew(node.getParent());
	}
	
	private boolean isAnnoationValue(Node node) {
		if (node == null) {
			return false;
		}
		
		if (node instanceof Assignment) {
			return ((Assignment)node).resolveBinding() != null;
		}
		
		if (node instanceof SetValuesExpression && ((SetValuesExpression)node).getExpression() instanceof AnnotationExpression) {
			return true;
		}
		return isAnnoationValue(node.getParent());
	}

}
