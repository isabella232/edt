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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFieldsFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionMemberSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionSignatureProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLSystemLibraryProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLSystemWordProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLFunctionArgumentsReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() fct("); //$NON-NLS-1$
		addContext("package a; function a() fct(a,"); //$NON-NLS-1$		
		addContext("package a; function a() a refType = new refType("); //$NON-NLS-1$
		addContext("package a; function a() fct[2]("); //$NON-NLS-1$
		addContext("package a; function a() fct[2](a,"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		if (isState(parseStack, ((Integer) validStates.get(0)).intValue()) ||
			isState(parseStack, ((Integer) validStates.get(2)).intValue()) ||
			isState(parseStack, ((Integer) validStates.get(3)).intValue())) {
			getBoundASTNode(viewer, documentOffset, new String[] {");", ")", ";", "", ";end end"},
				new CompletedNodeVerifier() { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					public boolean nodeIsValid(Node astNode) {
						return astNode instanceof FunctionInvocation || astNode instanceof NewExpression;
					}
				},
				new IBoundNodeProcessor() {public void processBoundNode(Node boundNode) {
					Node nodeThatMightBeAssignment = getNodeThatMightBeAssignment(boundNode);
					if(nodeThatMightBeAssignment instanceof Assignment) {
						IBinding lhBinding = ((Assignment) nodeThatMightBeAssignment).getLeftHandSide().resolveDataBinding();
						if(lhBinding != null && IBinding.NOT_FOUND_BINDING != lhBinding && lhBinding.isAnnotationBinding()) {
							//We are completing the rhs of a property value
							return;
						}
					}
					
					
					if(prefix.length() == 0 &&
						(
							boundNode instanceof FunctionInvocation ||
							(
								boundNode instanceof NewExpression &&
								((NewExpression) boundNode).getType().isNameType()
							)
						)
					) {
						proposals.addAll(
							new EGLFunctionSignatureProposalHandler(viewer,
								documentOffset,
								boundNode instanceof FunctionInvocation ?
									((FunctionInvocation) boundNode).getTarget() :
									((NameType) ((NewExpression) boundNode).getType()).getName())
								.getProposals());
					}
					else {				
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
						
						//Get all library and external type proposals
						proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(
							IEGLSearchConstants.LIBRARY|IEGLSearchConstants.EXTERNALTYPE));
						
					}
				}
			});
		}

		return proposals;
	}
	
	private Node getNodeThatMightBeAssignment(Node boundNode) {
		return boundNode instanceof Assignment ?
			boundNode :
			boundNode.getParent();
	}
}
