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

import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
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
import org.eclipse.edt.ide.core.internal.model.IRPartType;
import org.eclipse.edt.ide.core.search.IEGLSearchConstants;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFieldsFromAnnotationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchAnnotationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLVariableDotProposalHandler;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.jface.text.ITextViewer;

public class EGLAnnotationFieldReferenceCompletion extends EGLAbstractReferenceCompletion {

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; handler a {@a{"); //$NON-NLS-1$
		addContext("package a; handler a {@a{a=a,"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		
		getBoundASTNode(viewer, documentOffset, new String[] {"", "a};", "=a}", "=a};", ";", "};"}, new CompletedNodeVerifier() { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			public boolean nodeIsValid(Node astNode) {
				return getAnnotation(astNode) != null;
			}
		}, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {
				Annotation ann = getAnnotation(boundNode);
				if (ann != null) {
					
					AnnotationType annotationType = (AnnotationType)ann.getEClass();
					
					if ((annotationType instanceof StereotypeType) || annotationType.getEFields().size() > 1) {	
						proposals.addAll(new EGLFieldsFromAnnotationProposalHandler(viewer, documentOffset, prefix, editor, getAnnotationFieldsAlreadySpecified(boundNode)).getProposals((AnnotationType)ann.getEClass()));
					}
				}
			}
			
			private List<String> getAnnotationFieldsAlreadySpecified(Node node) {

				final List<String> list = new ArrayList<String>();
				
				while (node != null && !(node instanceof SettingsBlock)) {
					node = node.getParent();
				}
				
				if (node != null) {
					node.accept(new DefaultASTVisitor() {
					
					public boolean visit(SettingsBlock settingsBlock) {
						return true;
					}
					
					
					public boolean visit(Assignment assignment) {
						
						if (assignment.resolveBinding() != null) {
							list.add(assignment.getLeftHandSide().toString().toUpperCase().toLowerCase());
						}
						return false;
					} 
				});
				}
				
				
				return list;
			}
		});

		
		return proposals;
	}

	public Annotation getAnnotation(Node node) {
		
		if (node == null) {
			return null;
		}
		
		if (node instanceof SetValuesExpression) {
			SetValuesExpression sve = (SetValuesExpression) node;
			if (sve.getExpression() instanceof AnnotationExpression) {
				return (((AnnotationExpression)sve.getExpression()).resolveAnnotation());
			}
			return null;
		}
		
		if (node instanceof SettingsBlock) {
			if (node.getParent() instanceof Part) {
				Part part = (Part) node.getParent();
				if (part.hasSubType()) {
					return (Annotation)part.getSubType().resolveElement();
				}
			}
		}

		return getAnnotation(node.getParent());
	}


}
