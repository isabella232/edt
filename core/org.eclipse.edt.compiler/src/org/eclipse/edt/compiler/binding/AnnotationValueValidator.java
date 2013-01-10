/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
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

import java.math.BigDecimal;

import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.EClass;
import org.eclipse.edt.mof.EClassifier;
import org.eclipse.edt.mof.EDataType;
import org.eclipse.edt.mof.EField;
import org.eclipse.edt.mof.EGenericType;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.EType;
import org.eclipse.edt.mof.ETypedElement;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Element;
import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.serialization.Environment;
import org.eclipse.edt.mof.utils.EList;

public class AnnotationValueValidator {
	
	private static EType elistType;
	private static EType etypeType;
	IProblemRequestor problemRequestor;
	
	
	public AnnotationValueValidator(IProblemRequestor problemRequestor) {
		super();
		this.problemRequestor = problemRequestor;
	}

	public Object validateValue(Object value, Expression expr, EField field, EType eType, boolean nullable) {	
		
		if (value instanceof Element) {
			EType etype = BindingUtil.getETypeFromProxy((Element)value);
			if (etype != null) {
				value = etype;
			}
		}
		
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
		
		boolean valueIsList = value instanceof EList;
		boolean typeIsList = isGenericElistType(eType);

		//if value is a list and the type is not, error
		if (valueIsList && !typeIsList) {
    		problemRequestor.acceptProblem(
        			expr,
        			IProblemRequestor.ANNOTATION_CANNOT_BE_ARRAY,
        			new String[] {
        				field.getName()
        			});   
			return null;
		}
		
		//if the type is a list and the value is not, error
		if (typeIsList && !valueIsList) {  
    		problemRequestor.acceptProblem(
        			expr,
        			IProblemRequestor.ANNOTATION_MUST_BE_ARRAY,
        			new String[] {
        				field.getName()
        			});   
			return null;
		}
		
		//if the value is nullLiteral, the field must be nullable
		if (value instanceof NullLiteral) {
			if (nullable) {
				return value;
			}
			else {
	    		problemRequestor.acceptProblem(
	        			expr,
	        			IProblemRequestor.ANNOTATION_CANNOT_BE_NULL,
	        			new String[] {
	        				field.getName()
	        			});   
				return null;
			}
		}

		//if both value and type is a list, recursively check the elements of the array
		if (valueIsList && typeIsList) {
			@SuppressWarnings("unchecked")
			EList<Object> listValue = (EList<Object>) value;
			ArrayLiteral arrayLit = (ArrayLiteral) expr;
			boolean foundError = false;
			int index = 0;
			EType currType = ((EGenericType)eType).getETypeArguments().get(0);
			for (Object curValue : listValue) {
				Expression currExpr = arrayLit.getExpressions().get(index);
				curValue = validateValue(curValue, currExpr, field, currType, true);
				if (curValue == null) {
					foundError = true;
				}
				index = index + 1;
			}
			if (foundError) {
				return null;
			}
			return value;
		}
		
		//not dealing with arrays, check the EData types: javaobject, estring, eboolean, eint32, efloat, edecimal
		if (eType instanceof EDataType) {
			String className = ((EDataType)eType).getJavaClassName();
			if (className.equals(EDataType.EDataType_JavaObject)) {
				return value;
			}

			if (className.equals(EDataType.EDataType_String)) {
				if (value instanceof String) {
					return value;
				}
	    		problemRequestor.acceptProblem(
	        			expr,
	        			IProblemRequestor.ANNOTATION_MUST_BE_STRING,
	        			new String[] {
	        				field.getName()
	        			});   
				return null;
			}

			if (className.equals(EDataType.EDataType_Boolean)) {
				if (value instanceof Boolean) {
					return value;
				}
	    		problemRequestor.acceptProblem(
	        			expr,
	        			IProblemRequestor.ANNOTATION_MUST_BE_BOOL,
	        			new String[] {
	        				field.getName()
	        			});   
				return null;
			}

			if (className.equals(EDataType.EDataType_Int32)) {
				if (value instanceof Integer) {
					return value;
				}
	    		problemRequestor.acceptProblem(
	        			expr,
	        			IProblemRequestor.ANNOTATION_MUST_BE_INT,
	        			new String[] {
	        				field.getName()
	        			});   
				return null;
			}

			if (className.equals(EDataType.EDataType_Float)) {
				if (value instanceof Double) {
					return value;
				}
	    		problemRequestor.acceptProblem(
	        			expr,
	        			IProblemRequestor.ANNOTATION_MUST_BE_FLOAT,
	        			new String[] {
	        				field.getName()
	        			});   
				return null;
			}
			
			if (className.equals(EDataType.EDataType_Decimal)) {
				if (value instanceof BigDecimal) {
					return value;
				}
	    		problemRequestor.acceptProblem(
	        			expr,
	        			IProblemRequestor.ANNOTATION_MUST_BE_DECIMAL,
	        			new String[] {
	        				field.getName()
	        			});   
				return null;
			}
		}
		
		//finally check if the type of the value is compatible with the etype of the field....if the object is a ETypedElement, just get
		// the type, otherwise, if it is an EObject, get the eclass
		
		//value could be a enumerationEntry, it is valid if the field is typed with that enumeration
		if (value instanceof ETypedElement) {
			if (((ETypedElement)value).getEType().equals(eType)) {
				return value;
			}
		}
		
		//Field could be typed as an AnnotationType
		if (expr.resolveType() != null && expr.resolveType().equals(eType)) {
			return value;
		}

		//Field could be typed as something like SequenceType
		if (expr.resolveType() != null && expr.resolveType().getEClass().equals(eType)) {
			return value;
		}
		
		if (value instanceof EObject && eType instanceof EClass) { 
			EClass valClass = ((EObject)value).getEClass();
			EClass typeClass = (EClass) eType;
			if (isEqualOrSubclassOf(valClass, typeClass)) {
				return value;
			}
		}
		
		//special check for fields that have etype of EType, such as defaultSuperType for stereotypes
		if (eType != null && eType.equals(getETypeType())) {
			if (value instanceof Type || value instanceof EType) {
				return value;
			}
		}
		
		//Check for mofclass proxy. This will only happen if we are compiling the system parts
		EObject eobject = null;
		if (value instanceof Classifier) {
			eobject = BindingUtil.getMofClassProxyFor((Classifier)value);
		}
		if (eobject == null && value instanceof EObject) {
			eobject = (EObject)value;
		}
			
		if (eobject instanceof EClass && eType instanceof EClass) {
			if (isEqualOrSubclassOf((EClass) eobject, (EClass) eType)) {
				return value;
			}
		}
		
		
		
		//If we get here, there is some problem, throw a error and return null
		problemRequestor.acceptProblem(
    			expr,
    			IProblemRequestor.ANNOTATION_VALUE_NOT_COMPAT,
    			new String[] {
    				field.getName()
    			});   
		return null;

	}
	
	private boolean isEqualOrSubclassOf(EClass child, EClass parent) {
		if (child == parent) {
			return true;
		}
		if (child == null || parent == null) {
			return false;
		}
		
		if (child.getETypeSignature().equals(parent.getETypeSignature())) {
			return true;
		}
		
		return BindingUtil.isSubClassOf(child, parent);
		
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

	private static EType getElistType() {
		if (elistType == null) {
			try {
				elistType = (EType)Environment.getCurrentEnv().findType(MofConversion.Type_EList);
			} catch (Exception e) {
			} 
		}
		return elistType;
	}

	private static EType getETypeType() {
		if (etypeType == null) {
			try {
				etypeType = (EType)Environment.getCurrentEnv().findType(MofConversion.Type_EType);
			} catch (Exception e) {
			} 
		}
		return etypeType;
	}

	//Type_EType
}
