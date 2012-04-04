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

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.ClassFieldBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.EGLNewPropertiesHandler;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLVariableDotProposalHandler;
import org.eclipse.jface.text.ITextViewer;

public class EGLNewSettingsBlockReferenceCompletion extends EGLAbstractPropertyReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() new a{"); //$NON-NLS-1$
		addContext("package a; function a() new a{a=a,"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		this.viewer = viewer;
		this.documentOffset = documentOffset;
		final List proposals = new ArrayList();
		if (isState(parseStack, ((Integer) validStates.get(0)).intValue())
			&& isState(parseStack, getState("package a; function a() new"))) { //$NON-NLS-1$

			int location = getLocation();
			if (location == EGLNewPropertiesHandler.locationNewExpression) {
				getBoundASTNode(viewer, documentOffset, new String[] {"", "a};", "a", "}};"}, new CompletedNodeVerifier() { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					public boolean nodeIsValid(Node astNode) {
						boolean result = false;
						while(astNode != null && !result) {
							if(astNode instanceof SettingsBlock) {
								result = true;
							}
							astNode = astNode.getParent();
						}
						return result;
					}
				}, new IBoundNodeProcessor() {
					public void processBoundNode(Node boundNode) {
						Node node = boundNode.getParent();
						if(node instanceof Assignment) {
							node = node.getParent();
						}
						if(node instanceof SettingsBlock) {
							node = node.getParent();
						}
						final NewExpression newExpression[] = new NewExpression[] {null};
						node.accept(new DefaultASTVisitor() {
							public boolean visit(NewExpression newExpr) {
								newExpression[0] = newExpr;
								return false;
							}
						});
						if (newExpression[0] != null) {
							Type type = newExpression[0].getType();							
							Type baseType = type.getBaseType();
							if(baseType instanceof NameType) {								
								IBinding binding = ((NameType) baseType).getName().resolveBinding();
								if(Binding.isValidBinding(binding) && binding.isTypeBinding()) {
									IDataBinding dBinding = new ClassFieldBinding(binding.getName(), null, (ITypeBinding) binding);
									proposals.addAll(
										new EGLVariableDotProposalHandler(
											viewer,
											documentOffset,
											prefix,
											editor,
											dBinding,
											(ITypeBinding) binding,
											null).getProposals(false, true, false, settingsBlockList));
								}
							}
						}
					}
				});
			}
		}
		return proposals;
	}
}
