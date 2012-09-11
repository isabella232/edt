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

import java.util.List;

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
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.SubType;
import org.eclipse.edt.mof.utils.NameUtile;

public class SettingsBlockAnnotationBindingsCompletor extends DefaultBinder {

	private AnnotationLeftHandScope annotationLeftHandScope;
	private Binder lhsBinder;
	private Binder rhsBinder;

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

	private static class LHSBinder extends Binder {

		public LHSBinder(Scope currentScope, Part currentBinding,
				IDependencyRequestor dependencyRequestor,
				IProblemRequestor problemRequestor,
				ICompilerOptions compilerOptions) {
			super(currentScope, currentBinding, dependencyRequestor, problemRequestor,
					compilerOptions);
		}	
		
		public boolean visit(org.eclipse.edt.compiler.core.ast.ThisExpression thisExpression) {
			if (currentScope.getType() != null) {
				thisExpression.setType(currentScope.getType());
				return false;
			}
			return super.visit(thisExpression);
		}

		public boolean visit(org.eclipse.edt.compiler.core.ast.SuperExpression superExpression) {
			if (currentScope.getType() != null) {
				
				Classifier classifier = currentScope.getType().getClassifier();
				if (classifier instanceof SubType) {
					SubType sub = (SubType) classifier;
					if (sub.getSuperTypes().size() > 0) {
						superExpression.setType(sub.getSuperTypes().get(0));
						return false;
					}
				}
			}
			return super.visit(superExpression);
		}

		
	}

	public SettingsBlockAnnotationBindingsCompletor(Scope currentScope, Part partBinding,
			AnnotationLeftHandScope annotationLeftHandScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor,
			ICompilerOptions compilerOptions) {
		super(currentScope, partBinding, dependencyRequestor, problemRequestor, compilerOptions);
		this.partBinding = partBinding;
		this.annotationLeftHandScope = annotationLeftHandScope;
		rhsBinder = new Binder(currentScope, partBinding, dependencyRequestor, problemRequestor, compilerOptions);
		lhsBinder = new LHSBinder(annotationLeftHandScope.getTopLevelAnnotationLeftHandScope().getScopeToUseWhenResolving(), partBinding, dependencyRequestor, problemRequestor, compilerOptions);
	}

	public boolean visit(SettingsBlock settingsBlock) {
		settingsBlock.accept(new AbstractASTExpressionVisitor() {

			private EField getEField(AnnotationType annType, String name) {
				for (EField f : annType.getEFields()) {
					if (f.getName().equalsIgnoreCase(name))
						return f;
				}
				return null;
			}
			
			public boolean visit(Assignment assignment) {
				
				if (annotationLeftHandScope.getElementBeingAnnotated() instanceof Annotation && assignment.getLeftHandSide() instanceof SimpleName) {
					Annotation ann = (Annotation) annotationLeftHandScope.getElementBeingAnnotated();
					AnnotationType annType = (AnnotationType)ann.getEClass();
					String fieldName = ((SimpleName)assignment.getLeftHandSide()).getIdentifier();
					EField field = getEField(annType, fieldName);
					if (field != null) {
						Object obj = getValue(assignment.getRightHandSide(), ann, field);
						setValueIntoAnnotation(obj, assignment.getRightHandSide(), ann, field);
						assignment.getLeftHandSide().setElement(ann);
						assignment.getLeftHandSide().setType(annType);
						assignment.setBinding(ann);
						return false;
					}

				}
				assignment.getLeftHandSide().accept(lhsBinder);
				assignment.getRightHandSide().accept(rhsBinder);
				return false;
			}

			public boolean visit(AnnotationExpression annotationExpression) {
				Annotation ann = getAnnotation(annotationExpression, problemRequestor);
				setAnnotationOnElement(annotationLeftHandScope.getTopLevelAnnotationLeftHandScope().getElementBeingAnnotated(), ann, annotationExpression);
				
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
					setValuesExpression.getExpression().accept(lhsBinder);
					setValuesExpression.accept(rhsBinder);
					
				}
				return false;
			}

			public boolean visitExpression(Expression expression) {
				//If the current element is an annotation and the annotationType has a single field, set the value of the expression into the field
				if (annotationLeftHandScope.getElementBeingAnnotated() instanceof Annotation) {
					Annotation ann = (Annotation) annotationLeftHandScope.getElementBeingAnnotated();
					AnnotationType annType = (AnnotationType)ann.getEClass();
					if (annType.getEFields().size() == 1) {
						Object obj = getValue(expression, ann, annType.getEFields().get(0));
						setValueIntoAnnotation(obj, expression, ann, annType.getEFields().get(0));
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
		AnnotationRightHandScope rhScope = new AnnotationRightHandScope(currentScope, field);
		return new AnnotationValueGatherer(expr, rhScope, partBinding, dependencyRequestor, problemRequestor, compilerOptions).getValue();
	}
				
	private void setValueIntoAnnotation(Object value, Expression expr, Annotation ann, EField field) {
		AnnotationValueValidator validator =  new AnnotationValueValidator(problemRequestor);
		Object result = validator.validateValue(value, expr, field, field.getEType(), field.isNullable());
		if (result != null) {
			ann.eSet(field, result);			
		}
	}
		
	private void setAnnotationOnElement(Element elem, Annotation ann, AnnotationExpression annotationExpression) {
		if (ann == null) {
			return;
		}
		
		if (isApplicableFor(elem, ann)) {
			elem.addAnnotation(ann);
		}
		else {
			problemRequestor.acceptProblem(annotationExpression, IProblemRequestor.ANNOTATION_NOT_APPLICABLE,
					new String[] {annotationExpression.getName().getCanonicalString()});
		}
	}

	private boolean isApplicableFor(Element elem, Annotation ann) {
		AnnotationType annType = (AnnotationType)ann.getEClass();
		return BindingUtil.isApplicableFor(elem, annType.getTargets());
	}
	
}
