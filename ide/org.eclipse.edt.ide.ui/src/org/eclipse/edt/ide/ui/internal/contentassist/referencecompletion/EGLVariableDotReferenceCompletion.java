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
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.AsExpression;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FieldAccess;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.QualifiedName;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLVariableDotProposalHandler;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jface.text.ITextViewer;

public class EGLVariableDotReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; function a() var."); //$NON-NLS-1$
		addContext("package a; function a() (var)."); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List result = new ArrayList();
		//"x);" added for a qualified parameter to a functions call.  Ex: makeReservation(reservation.<ca>
		getBoundASTNode(viewer, documentOffset, new String[] {"x", "x;", "x=x;", "x);", "x to x;"}, new CompletedNodeVerifier() {
			public boolean nodeIsValid(Node astNode) {
				final boolean[] isValid = new boolean[] {false};
				if (astNode != null) {
					astNode.accept(new DefaultASTVisitor() {
						public boolean visit(QualifiedName qualifiedName) {
							isValid[0] = true;
							return false;
						}
						
						public boolean visit(FieldAccess fieldAccess) {
							isValid[0] = true;
							return false;
						}
					});
				}
				return isValid[0];
			}			
		}, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {
				//Node should either be a QualifiedName or FieldAccess
				boundNode.accept(new DefaultASTVisitor() {
					public boolean visit(QualifiedName qualifiedName) {
						handleQualifier(qualifiedName.getQualifier());				
						return false;
					}
					
					public boolean visit(FieldAccess fieldAccess) {
						handleQualifier(fieldAccess.getPrimary());
						return false;
					}
					
					private void handleQualifier(Expression qualifier) {
						Type tBinding = qualifier.resolveType();
						result.addAll(new EGLVariableDotProposalHandler(viewer, documentOffset, prefix, editor, tBinding, qualifier.resolveMember() != null, qualifier).getProposals(null));				
					}
				});
			}
		});

		return result;
	}

	protected boolean wantFieldsForType(Expression expr) {
		final boolean[] result = new boolean[] {false};
		expr.accept(new DefaultASTVisitor() {
			public boolean visit(ParenthesizedExpression parenthesizedExpression) {
				return true;
			}
			
			public boolean visit(AsExpression asExpression) {
				result[0] = true;
				return false;
			}
			
			public boolean visit(FunctionInvocation fInvocation) {
				result[0] = true;
				return false;
			}
		});
		return result[0];
	}
}
