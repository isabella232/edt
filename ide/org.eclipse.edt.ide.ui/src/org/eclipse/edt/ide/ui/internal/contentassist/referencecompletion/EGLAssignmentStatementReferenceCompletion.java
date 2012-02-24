/*******************************************************************************
 * Copyright ©2000, 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLEnumerationNameProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFieldsFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPropertyValueProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLSystemLibraryProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLSystemWordProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLTableUseStatementProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLAssignmentStatementReferenceCompletion extends EGLAbstractReferenceCompletion {
	
	/**
	 * ONLY USEFUL IN CODE RUNNING WITHIN SPAN OF PROCESSBOUNDNODE METHOD BELOW
	 */
	private Node boundNode = null;
	private static int invokeInArray = 66;
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() a="); //$NON-NLS-1$
		addContext("package a; function a() a int="); //$NON-NLS-1$
		addContext("package a; function a() a int=["); //$NON-NLS-1$
		addContext("package a; program a type b{c=["); //$NON-NLS-1$
		addContext("package a; program a type b{c="); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(final ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		final EGLAssignmentStatementReferenceCompletion thisCompletion = this;
		final boolean[] isDone = new boolean[] {false};
		final boolean[] isPropertySetting = new boolean[] {false};
		final boolean[] isStringLiteral = new boolean[]{false};

		if (isState(parseStack, ((Integer) validStates.get(0)).intValue())
				|| isState(parseStack,((Integer) validStates.get(1)).intValue())
				|| isState(parseStack,((Integer) validStates.get(2)).intValue())) {

			getBoundASTNode(viewer, documentOffset, new String[] {"x", "x}", "x;", "\";", "x", "x;end end", "x}; end"}, new CompletedNodeVerifier() {
				public boolean nodeIsValid(Node astNode) {
					if(astNode instanceof StringLiteral){
						isStringLiteral[0] = true;
					}
					return astNode != null && getNodeThatMightBeAssignment(astNode) instanceof Assignment;
				}
			}, new IBoundNodeProcessor() {
				public void processBoundNode(Node boundNode) {
					if(!isStringLiteral[0]){
						thisCompletion.boundNode = boundNode;
						Node nodeThatMightBeAssignment = getNodeThatMightBeAssignment(boundNode);
						if(nodeThatMightBeAssignment instanceof Assignment) {
							Assignment assignmentNode = ((Assignment) nodeThatMightBeAssignment);
							IBinding lhBinding = assignmentNode.getLeftHandSide().resolveDataBinding();
							if(lhBinding != null && IBinding.NOT_FOUND_BINDING != lhBinding) {
								if(lhBinding.isAnnotationBinding()) {
									//We are completing the rhs of a property value
									proposals.addAll(new EGLPropertyValueProposalHandler(viewer, documentOffset, prefix, editor, thisCompletion, parseStack, boundNode).getProposals((IAnnotationBinding) lhBinding));
									isPropertySetting[0] = true;
									isDone[0] = true;
//									return;
								}
								else if(lhBinding instanceof ClassFieldBinding && 
										assignmentNode.getParent() != null &&								
										assignmentNode.getParent() instanceof SettingsBlock &&
										parseStack.getCurrentState() != invokeInArray) {  //added for RUI widget class fields
									proposals.addAll(new EGLPropertyValueProposalHandler(viewer, documentOffset, prefix, editor, thisCompletion, parseStack, boundNode).getProposals((ClassFieldBinding) lhBinding));
									isPropertySetting[0] = true;
									isDone[0] = true;
//									return;
								}
							}
						}

						//Get all variable proposals
						proposals.addAll(
							new EGLDeclarationProposalHandler(viewer,
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
						
						//Get user field proposals using library use statements
						proposals.addAll(
							new EGLFieldsFromLibraryUseStatementProposalHandler(viewer, documentOffset, prefix, editor, boundNode).getProposals());
						
						//Get user function proposals with return value using library use statements
						proposals.addAll(
							new EGLFunctionFromLibraryUseStatementProposalHandler(viewer, documentOffset, prefix, editor, true, boundNode).getProposals());
						
						//Get system function proposals with no return value
						proposals.addAll(
								new EGLSystemWordProposalHandler(viewer,
									documentOffset,
									prefix,
									editor,
									boundNode).getProposals(EGLSystemWordProposalHandler.RETURNS, true));

						//Get user function proposals with return value
						if(!isPropertySetting[0]){
							proposals.addAll(
									new EGLFunctionPartSearchProposalHandler(viewer, documentOffset, prefix, editor, true, boundNode).getProposals());
						}else{
							proposals.addAll(
									new EGLFunctionPartSearchProposalHandler(viewer, documentOffset, prefix, editor, false, boundNode).getProposals());
						}
						
						thisCompletion.boundNode = null;
					}
				}
			});
			
			if(!isDone[0] && !isStringLiteral[0]) {
				//Get all library and external type proposals
				proposals.addAll(new EGLPartSearchProposalHandler(viewer, documentOffset, prefix, editor).getProposals(
					IEGLSearchConstants.LIBRARY|IEGLSearchConstants.EXTERNALTYPE));
		
				//Get all system library proposals
				proposals.addAll(
					new EGLSystemLibraryProposalHandler(viewer, documentOffset, prefix, editor).getProposals());
				
				//Get all enumeration name proposals
				proposals.addAll(
					new EGLEnumerationNameProposalHandler(viewer, documentOffset, prefix).getProposals());
			}
		}

		return proposals;
	}

	private Node getNodeThatMightBeAssignment(Node boundNode) {
		if(boundNode instanceof Assignment) {
			return boundNode;
		}
		
		Node parent = boundNode.getParent();
		
		if(parent instanceof Assignment) {
			return parent;
		}
		
		if(parent != null) {
			return parent.getParent();
		}
		
		return null;
	}

	public List getStructureItems() {
		return getStructureItems(boundNode);
	}
}
