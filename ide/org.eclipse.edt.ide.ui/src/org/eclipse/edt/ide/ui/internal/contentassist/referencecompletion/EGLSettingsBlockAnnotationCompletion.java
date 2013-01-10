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
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.EnumerationField;
import org.eclipse.edt.compiler.core.ast.FunctionDataDeclaration;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.core.internal.model.IRPartType;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchAnnotationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLVariableDotProposalHandler;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jface.text.ITextViewer;

public class EGLSettingsBlockAnnotationCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; handler a {"); //$NON-NLS-1$
		addContext("package a; handler a {@a{},"); //$NON-NLS-1$
		addContext("package a; handler a {a=a,"); //$NON-NLS-1$
		addContext("package a; handler a a atype{"); //$NON-NLS-1$
		addContext("package a; handler a function a(){"); //$NON-NLS-1$
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
				if (shouldShowAnnotations(boundNode)) {
					proposals.addAll(new EGLPartSearchAnnotationProposalHandler(viewer, documentOffset, prefix, editor, addPrefix(), true, getAnnotationsAlreadySpecified(boundNode), boundNode).getProposals(IEGLSearchConstants.RECORD, "", new String[]{"annotation", IRPartType.ANNOTATION}));
				}
			}
			
			private List<AnnotationType> getAnnotationsAlreadySpecified(Node node) {
				final List<AnnotationType> list = new ArrayList<AnnotationType>();
				if (node instanceof SettingsBlock) {
					node.accept(new DefaultASTVisitor() {
						
						public boolean visit(SettingsBlock settingsBlock) {
							return true;
						}
						
						
						public boolean visit(SetValuesExpression setValuesExpression) {
							
							if (setValuesExpression.getExpression() instanceof AnnotationExpression) {
								AnnotationExpression annExp = (AnnotationExpression) setValuesExpression.getExpression();
								if (annExp.resolveAnnotation() != null) {
									list.add((AnnotationType)annExp.resolveAnnotation().getEClass());
								}
							}
							return false;
						} 
					});
				}
				return list;
			}
			
			private boolean shouldShowAnnotations(Node node) {
				
				node = node.getParent();
				
				if (node == null) {
					return false;
				}
				
				if (node instanceof NewExpression) {
					return false;
				}
				
				if (node instanceof SetValuesExpression) {
					return false;
				}

				if (node instanceof StructureItem) {
					return true;
				}
				
				if (node instanceof ClassDataDeclaration) {
					return true;
				}
				
				if (node instanceof FunctionDataDeclaration) {
					return true;
				}
				
				if (node instanceof Part) {
					return true;
				}
				
				if (node instanceof EnumerationField) {
					return true;
				}
				
				if (node instanceof NestedFunction) {
					return true;
				}
				
				if (node instanceof Constructor) {
					return true;
				}

				return shouldShowAnnotations(node);
			}
			
		});

		
		return proposals;
	}

	protected boolean addPrefix() {
		return true;
	}

}
