/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.AnnotationExpression;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.SetValuesExpression;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.SimpleName;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationRightHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.NullScope;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.Part;


public class SettingsBlockAnnotationBindingsCompletor extends DefaultBinder {

	private AnnotationLeftHandScope annotationLeftHandScope;
	private Binder binder;

	private Part partBinding;
	
	private static class Binder extends DefaultBinder {

		public Binder(Scope currentScope, Part currentBinding,
				IDependencyRequestor dependencyRequestor,
				IProblemRequestor problemRequestor,
				ICompilerOptions compilerOptions) {
			super(currentScope, currentBinding, dependencyRequestor, problemRequestor,
					compilerOptions);
		}		
	}

	public SettingsBlockAnnotationBindingsCompletor(Scope currentScope, Part partBinding,
			AnnotationLeftHandScope annotationLeftHandScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor,
			ICompilerOptions compilerOptions) {
		super(currentScope, partBinding, dependencyRequestor, problemRequestor, compilerOptions);
		this.partBinding = partBinding;
		this.annotationLeftHandScope = annotationLeftHandScope;
		binder = new Binder(currentScope, partBinding, dependencyRequestor, problemRequestor, compilerOptions);
	}

	public boolean visit(SettingsBlock settingsBlock) {
		settingsBlock.accept(new AbstractASTExpressionVisitor() {

			public boolean visit(Assignment assignment) {
				
				if (annotationLeftHandScope.getElementBeingAnnotated() instanceof Annotation && assignment.getLeftHandSide() instanceof SimpleName) {
					Annotation ann = (Annotation) annotationLeftHandScope.getElementBeingAnnotated();
					AnnotationType annType = (AnnotationType)ann.getEClass();
					String fieldName = ((SimpleName)assignment.getLeftHandSide()).getIdentifier();
					EField field = annType.getEField(fieldName);
					if (field != null) {
						Object obj = getValue(assignment.getRightHandSide(), ann, field);
						setValueIntoAnnotation(obj, assignment.getRightHandSide(), ann, field);
						assignment.getLeftHandSide().setElement(ann);
						assignment.getLeftHandSide().setType(annType);
						assignment.setBinding(ann);
						return false;
					}

				}
				assignment.accept(binder);
				return false;
			}

			public boolean visit(AnnotationExpression annotationExpression) {
				Annotation ann = getAnnotation(annotationExpression);
				setAnnotationOnElement(annotationLeftHandScope.getTopLevelAnnotationLeftHandScope().getElementBeingAnnotated(), ann);
				
				return false;
			}

			public boolean visit(SetValuesExpression setValuesExpression) {
				if (setValuesExpression.getExpression() instanceof AnnotationExpression) {
					setValuesExpression.getExpression().accept(this);
					Annotation ann = ((AnnotationExpression)setValuesExpression.getExpression()).resolveAnnotation();
					if (ann == null) {
						setBindAttemptedForNames(setValuesExpression.getSettingsBlock());
					}
					else {
						AnnotationLeftHandScope newScope = new AnnotationLeftHandScope(annotationLeftHandScope, ann, null, ann);
						SettingsBlockAnnotationBindingsCompletor completor = new SettingsBlockAnnotationBindingsCompletor(currentScope, partBinding, newScope, dependencyRequestor, problemRequestor, compilerOptions);
						setValuesExpression.getSettingsBlock().accept(completor);
					}
					
				}
				else {
					super.visit(setValuesExpression);
				}
				return false;
			}

			public boolean visitExpression(Expression expression) {
				//If the current element is an annotation and the annotationType has a single field, set the value of the expression into the field
				if (annotationLeftHandScope.getElementBeingAnnotated() instanceof Annotation) {
					Annotation ann = (Annotation) annotationLeftHandScope.getElementBeingAnnotated();
					AnnotationType annType = (AnnotationType)ann.getEClass();
					if (annType.getAllEFields().size() == 1) {
						Object obj = getValue(expression, ann, annType.getAllEFields().get(0));
						setValueIntoAnnotation(obj, expression, ann, annType.getAllEFields().get(0));
					}
					else {
						problemRequestor.acceptProblem(expression, IProblemRequestor.POSITIONAL_PROPERTY_NOT_VALID_FOR,
								new String[] { annType.getCaseSensitiveName() });
					}
				}
				return false;
			}
			
			
		});

		return false;
	}
	
	private Object getValue(Expression expr, Annotation ann, EField field) {
		AnnotationRightHandScope rhScope = new AnnotationRightHandScope(currentScope, ann, field);
		return new AnnotationValueGatherer(expr, rhScope, partBinding, dependencyRequestor, problemRequestor, compilerOptions).getValue();
	}
	
	private void setValueIntoAnnotation(Object value, Expression expr, Annotation ann, EField field) {
		if (isValidValueForAnnotation(value, expr, ann, field)) {
			ann.eSet(field, value);			
		}
		else {
    		problemRequestor.acceptProblem(
        			expr,
        			IProblemRequestor.EXPRESSION_NOT_VALID_FOR_PROPERTY,
        			new String[] {
        				field.getName()
        			});    		

		}
	}
	
	private boolean isValidValueForAnnotation(Object value, Expression expr, Annotation ann, EField field) {
		//TODO
		return true;
	}
	 
	private Annotation getAnnotation(AnnotationExpression annotationExpression) {
		
		org.eclipse.edt.mof.egl.Type type = null;
		
		try {
			type = bindTypeName(annotationExpression.getName());
		} catch (ResolutionException e) {
		}
		
		if (type == null || !(type instanceof AnnotationType)) {
			problemRequestor.acceptProblem(annotationExpression, IProblemRequestor.NOT_AN_ANNOTATION,
					new String[] { annotationExpression.getCanonicalString() });
			annotationExpression.getName().setType(null);
			return null;
		}
		
		Annotation ann = (Annotation)((AnnotationType)type).newInstance();
		annotationExpression.getName().setElement(ann);
		annotationExpression.setType(type);
		annotationExpression.setAnnotation(ann);
		annotationExpression.setType(type);
		return ann;
		
	}
	
	private void setAnnotationOnElement(Element elem, Annotation ann) {
		if (ann == null) {
			return;
		}
		
		if (isApplicableFor(elem, ann)) {
			elem.addAnnotation(ann);
		}
	}

	private boolean isApplicableFor(Element elem, Annotation ann) {
		//TODO
		return true;
	}
			


}
