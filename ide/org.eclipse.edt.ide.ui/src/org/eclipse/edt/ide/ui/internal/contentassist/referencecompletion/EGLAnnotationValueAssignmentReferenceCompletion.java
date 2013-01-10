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
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.ide.core.internal.errors.ParseStack;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLAnnotationValueProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLDeclarationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFieldsFromAnnotationProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFieldsFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionFromLibraryUseStatementProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLFunctionMemberSearchProposalHandler;
import org.eclipse.edt.ide.ui.internal.contentassist.proposalhandlers.EGLPartSearchProposalHandler;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.jface.text.ITextViewer;

public class EGLAnnotationValueAssignmentReferenceCompletion extends EGLAbstractReferenceCompletion {
	
	/**
	 * ONLY USEFUL IN CODE RUNNING WITHIN SPAN OF PROCESSBOUNDNODE METHOD BELOW
	 */
	private Node boundNode = null;
	
	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#precompileContexts()
	 */
	protected void precompileContexts() {
		addContext("package a; handler a type a {abc =" ); //$NON-NLS-1$
		addContext("package a; handler a type a {abc = abc, abc =" ); //$NON-NLS-1$
		addContext("package a; handler a {@abc{abc=" ); //$NON-NLS-1$
		addContext("package a; handler a {@abc{abc=abc, abc=" ); //$NON-NLS-1$
		addContext("package a; handler a {@abc{abc=[" ); //$NON-NLS-1$
		addContext("package a; handler a {@abc{abc=[a," ); //$NON-NLS-1$
		addContext("package a; handler a {@abc{" ); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.edt.ide.ui.internal.contentassist.EGLAbstractReferenceCompletion#returnCompletionProposals(com.ibm.etools.egl.pgm.errors.ParseStack, java.util.List, org.eclipse.jface.text.ITextViewer, int)
	 */
	protected List returnCompletionProposals(final ParseStack parseStack, final String prefix, final ITextViewer viewer, final int documentOffset) {
		final List proposals = new ArrayList();
		final boolean[] proposal = new boolean[1];


		getBoundASTNode(viewer, documentOffset, new String[] {"x", "x}", "x}}", "x]", "x]}", "x}; end"}, new CompletedNodeVerifier() {
			public boolean nodeIsValid(Node astNode) {
				return getAnnotationField(astNode) != null;
			}
		}, new IBoundNodeProcessor() {
			public void processBoundNode(Node boundNode) {
				
				EField field = getAnnotationField(boundNode);
				proposals.addAll(new EGLAnnotationValueProposalHandler(viewer, documentOffset, prefix, editor).getProposals(field, boundNode));
			}
		});
		
		return proposals;
	}

	public EField getAnnotationField(Node node) {
		EField field = getAnnotationFieldFromAssignment(node);
		if (field != null) {
			return field;
		}
		
		return getAnnotationFieldFromAnnotation(node);
	}

	public EField getAnnotationFieldFromAssignment(Node node) {
		if (node == null) {
			return null;
		}
		if (node instanceof SetValuesExpression && ((SetValuesExpression)node).getExpression() instanceof AnnotationExpression) {
			return null;
		}
		
		if (node instanceof Assignment) {
			Assignment ass = (Assignment) node;
			if (ass.resolveBinding() != null && ass.getLeftHandSide() instanceof SimpleName) {
				return getEField((AnnotationType)ass.resolveBinding().getEClass(), ass.getLeftHandSide().toString());
			}
			return null;
		}
		return getAnnotationFieldFromAssignment(node.getParent());
	}
	
	public EField getAnnotationFieldFromAnnotation(Node node) {
		Annotation ann = getAnnotation(node);
		if (ann != null && !(ann.getEClass() instanceof StereotypeType) && ann.getEClass().getEFields().size() == 1) {
			return (ann.getEClass().getEFields().get(0));
		}
		return null;
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
	
	private EField getEField(AnnotationType annType, String name) {
		for (EField f : annType.getEFields()) {
			if (f.getName().equalsIgnoreCase(name))
				return f;
		}
		return null;
	}


}
