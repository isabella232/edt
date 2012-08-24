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
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
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
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.AnnotationType;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.ElementKind;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Handler;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.SubType;
import org.eclipse.edt.mof.serialization.DeserializationException;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.serialization.TypeNotFoundException;
import org.eclipse.edt.mof.utils.EList;


public class SettingsBlockAnnotationBindingsCompletor extends DefaultBinder {

	private static EType elistType;
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
		AnnotationRightHandScope rhScope = new AnnotationRightHandScope(currentScope, ann, field);
		return new AnnotationValueGatherer(expr, rhScope, partBinding, dependencyRequestor, problemRequestor, compilerOptions).getValue();
	}
	
	private Object validateValue(Object value, Expression expr, Annotation ann, EField field) {		
	
		//check for invalid expression type
		if (!isValidExpressionForAnnotationValue(expr)) {
    		problemRequestor.acceptProblem(
        			expr,
        			IProblemRequestor.EXPRESSION_NOT_VALID_FOR_PROPERTY,
        			new String[] {
        				field.getName()
        			});   
    		return null;
		}
		
		//replace constant values
		if (value instanceof ConstantField) {
			value = ((ConstantField)value).getValue().getObjectValue();
		}


		boolean valueIsList = value instanceof EList;
		boolean typeIsList = isGenericElistType(field.getEType());

		//if value is a list and the type is not, error
		if (value instanceof EList) {
			if (!(field.getEType() instanceof EGenericType)) {
				//array is invalid value for annotation field
			}
		}
		
		//if the type is a list and the value is not, error
		if ((field.getEType() instanceof EGenericType)) {
			
		}
		
		//if the value is nullLiteral, the field must be nullable
		
		//if both value and type is a list, recursively check the elements of the array
		
		//not dealing with arrays, check the EData types: javaobject, estring, eboolean, eint32, efloat, edecimal, elist
		
		//check the resolveable types like typeref and fieldref
		
		//finally check if the type of the value is compatible with the etype of the field....if the object is a ETypedElement, just get
		// the type, otherwise, if it is an EObject, get the eclass
		
		return value;

	}
	
	private static EType getElistType() {
		if (elistType == null) {
			try {
				elistType = (EType)Environment.getCurrentEnv().findType(MofConversion.Type_EList);
			} catch (Exception e) {
			} 
		}
		return elistType;
	}
	
	private boolean isGenericElistType (EType type) {
		if (type instanceof EGenericType) {
			try {
				EType elistType = getElistType();
				EClassifier eclassifier = ((EGenericType) type).getEClassifier();
				return elistType.equals(eclassifier);
			} catch (Exception e) {
			} 
		}
		return false;
	}
	
	private void setValueIntoAnnotation(Object value, Expression expr, Annotation ann, EField field) {
		
		Object result = validateValue(value, expr, ann, field);
		if (result != null) {
			ann.eSet(field, value);			
		}
	}
		
    private boolean isValidExpressionForAnnotationValue(Expression expr) {
    	
    	final boolean[] valid = new boolean[] {true};
    	DefaultASTVisitor visitor = new DefaultASTVisitor() {
   		
    		public boolean visit(NewExpression newExpression) {
    			valid[0] = false;
    			return false;
    		};
    		
    		public boolean visit(FunctionInvocation functionInvocation) {
    			valid[0] = false;
    			return false;
    		};
    		
    		public boolean visit(ParenthesizedExpression parenthesizedExpression) {
    			return true;
    		};
    		
    	};
    	
    	expr.accept(visitor);
		return valid[0];
    	
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
		return isApplicableFor(elem, annType.getTargets());
	}
	
	
	
	private boolean isApplicableFor(Element targetBinding, List<ElementKind> targets) {
		for(ElementKind nextTarget : targets) {
			boolean result = false;
			switch(nextTarget) {
				case DelegatePart:
					result = targetBinding instanceof Delegate;
					break;
				case ExternalTypePart:
					result = targetBinding instanceof ExternalType;
					break;
				case HandlerPart:
					result = targetBinding instanceof Handler;
					break;
				case ClassPart:
					result = targetBinding instanceof EGLClass;
					break;
				case InterfacePart:
					result = targetBinding instanceof Interface;
					break;
				case Part:
					result = targetBinding instanceof Part;
					break;
				case ProgramPart:
					result = targetBinding instanceof Program;
					break;
				case RecordPart:
					result = targetBinding instanceof Record;
					break;
				case LibraryPart:
					result = targetBinding instanceof Library;
					break;
				case ServicePart:
					result = targetBinding instanceof Service;
					break;
				case FieldMbr:
					result = targetBinding instanceof Field;
					break;
				case FunctionMbr:
					result = targetBinding instanceof Function;
					break;
				case ConstructorMbr:
					result = targetBinding instanceof Constructor;
					break;
				case EnumerationPart:
					result = targetBinding instanceof Enumeration;
					break;
				case EnumerationEntry:
					result = targetBinding instanceof EnumerationEntry;
					break;
			}
			if(result){
				return result;
			}
		}
		return false;
	}

		
	


}
