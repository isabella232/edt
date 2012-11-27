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

import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.EnumerationField;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLVariableDotProposalHandler;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jface.text.ITextViewer;

public class EGLSettingsBlockFieldReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; handler a {"); //$NON-NLS-1$
		addContext("package a; handler a a atype{"); //$NON-NLS-1$
		addContext("package a; handler a function a() new a{"); //$NON-NLS-1$
		addContext("package a; handler a function a() new a{a=a,"); //$NON-NLS-1$
		addContext("package a; handler a function a() a{"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		
		getBoundASTNode(viewer, documentOffset, new String[] {"", "a};", "a", "};", "}};"}, new CompletedNodeVerifier() { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			public boolean nodeIsValid(Node astNode) {
				return (astNode instanceof SettingsBlock);
			}
		}, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {
				Type type = getTypeFromNode(boundNode);
				if (type != null) {
					proposals.addAll(new EGLVariableDotProposalHandler(viewer, documentOffset, prefix, editor, type, true).getProposals(false, getFieldsAlreadySpecified(boundNode)));				
				}
				
			}
			
			private List<Field> getFieldsAlreadySpecified(Node node) {
				final List<Field> list = new ArrayList<Field>();
				if (node instanceof SettingsBlock) {
					node.accept(new DefaultASTVisitor() {
						
						public boolean visit(SettingsBlock settingsBlock) {
							return true;
						}
						
						public boolean visit(Assignment assignment) {
							Member mbr = assignment.getLeftHandSide().resolveMember();
							if (mbr instanceof Field) {
								list.add((Field)mbr);
							}
							return false;
						}
						
						public boolean visit(SetValuesExpression setValuesExpression) {
							Member mbr = setValuesExpression.getExpression().resolveMember();
							if (mbr instanceof Field) {
								list.add((Field)mbr);
							}
								return false;
						} 
					});
				}
				return list;
			}
			
			private Type getTypeFromNode(Node node) {
				
				if (node == null) {
					return null;
				}
				
				if (node instanceof NewExpression) {
					return ((NewExpression)node).getType().resolveType();
				}
				
				if (node instanceof SetValuesExpression) {
					return ((SetValuesExpression)node).getExpression().resolveType();
				}

				if (node instanceof StructureItem) {
					return ((StructureItem)node).getType().resolveType();
				}
				
				if (node instanceof ClassDataDeclaration) {
					return ((ClassDataDeclaration)node).getType().resolveType();
				}
				
				if (node instanceof Part) {
					return ((Part)node).getName().resolveType();
				}
				
				if (node instanceof EnumerationField) {
					return null;
				}
				
				if (node instanceof NestedFunction) {
					return null;
				}
				
				if (node instanceof Constructor) {
					return null;
				}

				return getTypeFromNode(node.getParent());
			}
			
		});

		
		return proposals;
	}
	
}
