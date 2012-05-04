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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.binding.annotationType.EGLSpecificValuesAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.annotationType.StereotypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.FunctionInvocation;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NewExpression;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.NullLiteral;
import org.eclipse.edt.compiler.core.ast.ParenthesizedExpression;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.DefaultCompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.core.lookup.SystemEnvironmentPackageNames;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.utils.TypeCompatibilityUtil;
import org.eclipse.edt.compiler.internal.core.validation.statement.StatementValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;



/**
 * @author winghong
 */
public class AnnotationBinding extends DataBinding implements IAnnotationBinding {
    
    private transient List fields = Collections.EMPTY_LIST;
	private boolean isCalculated;
    
    /**
     * @param simpleName
     */
    public AnnotationBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding) {
        this(caseSensitiveInternedName, declarer, typeBinding, false);
    }
    
    public AnnotationBinding(String caseSensitiveInternedName, IPartBinding declarer, ITypeBinding typeBinding, boolean suppressDefaultFields) {
        super(caseSensitiveInternedName, declarer, typeBinding);
        
        if(!suppressDefaultFields) {            
	        if(getType() != null && ITypeBinding.ANNOTATION_BINDING == getType().getKind()) {
		        IAnnotationTypeBinding aTypeBinding = (IAnnotationTypeBinding) getType();
		        
		        if(aTypeBinding != null) {
		        	if(aTypeBinding.hasSingleValue() && getAnnotationType().getAnnotation(StereotypeAnnotationTypeBinding.getInstance()) == null) {
		        		String singleFieldName = (String) aTypeBinding.getCaseSensitiveFieldNames().iterator().next();
		        		addField(new AnnotationFieldBinding(singleFieldName, declarer, aTypeBinding.getSingleValueType(), aTypeBinding));
		        	}
		        	
			        for(Iterator iter = aTypeBinding.getCaseSensitiveFieldNames().iterator(); iter.hasNext();) {
			        	String nextName = (String) iter.next();
			        	Object defaultValue = aTypeBinding.getDefaultValueForField(InternUtil.intern(nextName));
			        	if(defaultValue != null) {
			        		AnnotationFieldBinding aFieldBinding = new AnnotationFieldBinding(
			        			nextName,
			        			declarer,
			        			((IAnnotationTypeBinding) aTypeBinding.findData(InternUtil.intern(nextName)).getType()).getSingleValueType(),
			        			aTypeBinding);
			        		aFieldBinding.setValue(defaultValue, null, null, DefaultCompilerOptions.getInstance(), false);
			        		aFieldBinding.setCalculated(true);
			        		addAnnotation(aFieldBinding);
			        	}
			        }
		        }
	        }
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IAnnotationBinding#getValue()
     */
    public Object getValue() {
    	ITypeBinding type = getType();
    	if(ITypeBinding.ANNOTATION_BINDING == type.getKind()) {
    		IAnnotationTypeBinding aTypeBinding = (IAnnotationTypeBinding) type;
    		if(aTypeBinding.hasSingleValue()) {
    			String singleFieldName = (String) aTypeBinding.getFieldNames().iterator().next();
    			return ((IAnnotationBinding) findData(singleFieldName)).getValue();
    		}
    	}
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IAnnotationBinding#getAnnotationType()
     */
    public IAnnotationTypeBinding getAnnotationType() {
        return (IAnnotationTypeBinding) getType();
    }

    public int getKind() {
        return ANNOTATION_BINDING;
    }
    
    public boolean isAnnotationBinding() {
        return true;
    }
    
	public void setValue(Object value, IProblemRequestor problemRequestor, Expression expression, ICompilerOptions compilerOptions) {
		setValue(value, problemRequestor, expression, compilerOptions, true);
	}
    
    public void setValue(Object value, IProblemRequestor problemRequestor, Expression expression, ICompilerOptions compilerOptions, boolean performValidation) {
    	if(!performValidation) {
    		primSetValue(value);
    		return;
    	}
    	
    	
    	if (!isValidExpressionForAnnotationValue(expression)) {
    		problemRequestor.acceptProblem(
        			expression,
        			IProblemRequestor.EXPRESSION_NOT_VALID_FOR_PROPERTY,
        			new String[] {
        				caseSensitiveInternedName
        			});    		
    		return;
    	}
    	
    	
        boolean valid = true;
        ITypeBinding expressionType = expression.resolveTypeBinding();
        
        //allow an annotation to receive a value that is an annotation. This is already supported when the value is an array of annotations.
		if(expressionType instanceof AnnotationTypeBindingImpl) {
			expressionType = ((AnnotationTypeBindingImpl) expressionType).getAnnotationRecord();
		}

        
        IAnnotationTypeBinding myType = (IAnnotationTypeBinding) typeBinding;
        
    	if(myType.hasSingleValue()) {
    		ITypeBinding singleValueType = myType.getSingleValueType();
    		if(ITypeBinding.ARRAY_TYPE_BINDING == singleValueType.getKind() && typeIsResolvable(((ArrayTypeBinding) singleValueType).getElementType())) {
    			if(validateIsArrayOfNames(expression, problemRequestor, myType.getCaseSensitiveName())) {
    				expression.setTypeBinding(IBinding.NOT_FOUND_BINDING);
    				primSetValue(value);    				
    			}
    			return;
    		}
    		else if(typeIsResolvable(singleValueType)) {
    			if(validateIsName(expression, problemRequestor, myType.getCaseSensitiveName())) {
    				expression.setTypeBinding(IBinding.NOT_FOUND_BINDING);
    				primSetValue(value);    				
    			}
    			return;
    		}
    	}        	
    	else {
    		problemRequestor.acceptProblem(
        			expression,
        			IProblemRequestor.CANNOT_SET_VALUE_OF_MULTI_VALUE_PROPERTY,
        			new String[] {
        				caseSensitiveInternedName
        			});    		

    	}
   	
        if (problemRequestor != null && expression != null) {
        	if(value instanceof VariableBinding) {
        		VariableBinding vBinding = (VariableBinding) value;
        		if(vBinding.isConstant() && IDataBinding.SYSTEM_VARIABLE_BINDING != vBinding.getKind()) {
        			value = ((VariableBinding) value).getConstantValue();
        		}
        	}
        	
        	if(SystemPartManager.TYPEREF_BINDING == myType.getSingleValueType()) {
        		valid = value instanceof ITypeBinding;
        	}
        	else {            
        		valid = expressionType != null && valueMatchesType(value, expressionType, myType.getSingleValueType());
        		if(valid) {
        			if(NilBinding.INSTANCE == expressionType) {
        				valid = false;
        			}
        		}
        		else if(AbstractBinder.annotationIs(typeBinding, new String[] {"egl", "ui"}, "ValidValues")) {
        			valid = value instanceof Object[];
        		}
        	}
        }
        if (valid) {
        	if(myType.hasSingleValue()) {
        		primSetValue(value);
        	}
        }
        else if(value != null && expressionType != null) {
        	issueTypeMismatchError(myType.getSingleValueType(), expressionType, problemRequestor, expression);
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
    

	private boolean valueMatchesType(Object value, ITypeBinding valueType, ITypeBinding singleValueType) {
		if(singleValueType == null) {
			return true;
		}
		
		switch(singleValueType.getKind()) {
		case ITypeBinding.PRIMITIVE_TYPE_BINDING:
			switch(((PrimitiveTypeBinding) singleValueType).getPrimitive().getType()) {
			case Primitive.INT_PRIMITIVE:
			case Primitive.BIGINT_PRIMITIVE:
			case Primitive.SMALLINT_PRIMITIVE:
			case Primitive.SMALLFLOAT_PRIMITIVE:
			case Primitive.FLOAT_PRIMITIVE:
			case Primitive.DECIMAL_PRIMITIVE:
				return value instanceof Number;
			case Primitive.STRING_PRIMITIVE:
			case Primitive.CHAR_PRIMITIVE:
				return value instanceof String;
			case Primitive.BOOLEAN_PRIMITIVE:
				return value instanceof Boolean;
			case Primitive.ANY_PRIMITIVE:
				return true;
			}		
		case ITypeBinding.ARRAY_TYPE_BINDING:
			if(value instanceof Object[] && ITypeBinding.ARRAY_TYPE_BINDING == valueType.getKind()) {
				Object[] objAry = (Object[]) value;
				ITypeBinding elementType = ((ArrayTypeBinding) valueType).getElementType();
				ITypeBinding singleValueElementType = ((ArrayTypeBinding) singleValueType).getElementType();
				for(int i = 0; i < objAry.length; i++) {					
					if(!valueMatchesType(objAry[i], elementType, singleValueElementType)) {
						return false;
					}
				}
				return true;
			}
			return false;
		default:
			if (TypeCompatibilityUtil.compatibilityAnnotationMatches(valueType, singleValueType)) {
				return true;
			}
			
			if (Binding.isValidBinding(valueType) && Binding.isValidBinding(singleValueType)) {
				valueType = valueType.getNonNullableInstance();
				singleValueType = singleValueType.getNonNullableInstance();
			}
			return valueType == singleValueType;
		}
	}

	protected void primSetValue(Object value) {
    	ITypeBinding type = getType();
    	if(ITypeBinding.ANNOTATION_BINDING == type.getKind()) {
    		IAnnotationTypeBinding aTypeBinding = (IAnnotationTypeBinding) type;
    		if(aTypeBinding.hasSingleValue()) {
    			String singleFieldName = (String) aTypeBinding.getFieldNames().iterator().next();
    			IDataBinding findData = findData(singleFieldName);
    			if(Binding.isValidBinding(findData)) {
    				((AnnotationBinding) findData).primSetValue(value);
    			}
    			return;
    		}
    	}
    	throw new UnsupportedOperationException("primSetValue() called for annotation " + getName() + ", which does not have a single value");
	}

	private void issueTypeMismatchError(ITypeBinding annotationValueType, ITypeBinding suppliedType, IProblemRequestor problemRequestor, Expression expression) {
    	if(ITypeBinding.ENUMERATION_BINDING == annotationValueType.getKind()) {
    		problemRequestor.acceptProblem(
    			expression,
    			IProblemRequestor.PROPERTY_REQUIRES_SPECIFIC_VALUE,
    			new String[] {
    				caseSensitiveInternedName,
    				((EnumerationTypeBinding) annotationValueType).getEnumerationsAsCommaList()
    			});
    	}
    	else if(PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN) == annotationValueType) {
    		problemRequestor.acceptProblem(
    			expression,
    			IProblemRequestor.PROPERTY_REQUIRES_SPECIFIC_VALUE,
    			new String[] {
    				caseSensitiveInternedName,
    				IEGLConstants.KEYWORD_YES + ", " +
    				IEGLConstants.KEYWORD_NO
    			});
    	}
    	else if(SystemPartManager.SQLSTRING_BINDING == annotationValueType) {
    		problemRequestor.acceptProblem(
    			expression,
    			IProblemRequestor.PROPERTY_REQUIRES_SQL_STRING_VALUE,
    			new String[] {
    				caseSensitiveInternedName
    			});    		
    	}
    	else if(AbstractBinder.annotationIs(typeBinding, new String[] {"egl", "ui"}, "ValidValues")) {
    		problemRequestor.acceptProblem(
    			expression,
    			IProblemRequestor.PROPERTY_VALIDVALUES_INVALID_FORMAT);
    	}
    	else if(suppliedType != null) {
    		switch(annotationValueType.getKind()) {
	    		case ITypeBinding.PRIMITIVE_TYPE_BINDING:
	    			switch(((PrimitiveTypeBinding) annotationValueType).getPrimitive().getType()) {
		    			case Primitive.INT_PRIMITIVE:
		    			case Primitive.BIGINT_PRIMITIVE:
		    			case Primitive.SMALLINT_PRIMITIVE:
		    			case Primitive.SMALLFLOAT_PRIMITIVE:
		    			case Primitive.FLOAT_PRIMITIVE:
		    			case Primitive.DECIMAL_PRIMITIVE:
		    				problemRequestor.acceptProblem(expression, IProblemRequestor.PROPERTY_REQUIRES_NUMERIC_VALUE, new String[] {getCaseSensitiveName()});
		    				break;
		    			case Primitive.STRING_PRIMITIVE:
		    			case Primitive.CHAR_PRIMITIVE:
		    				problemRequestor.acceptProblem(expression, IProblemRequestor.PROPERTY_REQUIRES_STRING_VALUE, new String[] {getCaseSensitiveName()});
		    				break;
		    			default:
		    				issueDefaultTypeMismatchMessage(annotationValueType, suppliedType, problemRequestor, expression);
	    			}
	    			break;
	    		case ITypeBinding.ARRAY_TYPE_BINDING:
	    			ITypeBinding singleValueElementType = ((ArrayTypeBinding) annotationValueType).getElementType();
	    			switch(singleValueElementType.getKind()) {
		    			case ITypeBinding.PRIMITIVE_TYPE_BINDING:
		        			switch(((PrimitiveTypeBinding) singleValueElementType).getPrimitive().getType()) {
			        			case Primitive.INT_PRIMITIVE:
			        				problemRequestor.acceptProblem(expression, IProblemRequestor.PROPERTY_REQUIRES_NUMERIC_ARRAY_VALUE, new String[] {getCaseSensitiveName()});
			        				break;
			        			case Primitive.STRING_PRIMITIVE:
			        			case Primitive.CHAR_PRIMITIVE:
			        				problemRequestor.acceptProblem(expression, IProblemRequestor.PROPERTY_REQUIRES_STRING_ARRAY_VALUE, new String[] {getCaseSensitiveName()});
			        				break;
			        			default: issueDefaultTypeMismatchMessage(annotationValueType, suppliedType, problemRequestor, expression);
		        			}
		        			break;
		    			case ITypeBinding.ARRAY_TYPE_BINDING:
		    				singleValueElementType = ((ArrayTypeBinding) singleValueElementType).getElementType();
		        			switch(singleValueElementType.getKind()) {
			        			case ITypeBinding.PRIMITIVE_TYPE_BINDING:
			            			switch(((PrimitiveTypeBinding) singleValueElementType).getPrimitive().getType()) {
				            			case Primitive.INT_PRIMITIVE:
				            				problemRequestor.acceptProblem(expression, IProblemRequestor.PROPERTY_REQUIRES_NUMERIC_ARRAY_ARRAY_VALUE, new String[] {getCaseSensitiveName()});
				            				break;
				            			case Primitive.STRING_PRIMITIVE:
				            			case Primitive.CHAR_PRIMITIVE:
				            				problemRequestor.acceptProblem(expression, IProblemRequestor.PROPERTY_REQUIRES_STRING_ARRAY_ARRAY_VALUE, new String[] {getCaseSensitiveName()});
				            				break;
				            		}
			            			break;
			        			default: issueDefaultTypeMismatchMessage(annotationValueType, suppliedType, problemRequestor, expression);
		        			}
		        			break;
		    			default:
	        		    	issueDefaultTypeMismatchMessage(annotationValueType, suppliedType, problemRequestor, expression);
	    			}
	    			break;
	    		default:
	    			issueDefaultTypeMismatchMessage(annotationValueType, suppliedType, problemRequestor, expression);
	    	}
    	}
    }

	private void issueDefaultTypeMismatchMessage(ITypeBinding annotationValueType, ITypeBinding suppliedType, IProblemRequestor problemRequestor, Expression expression) {
		problemRequestor.acceptProblem(
			expression,
			IProblemRequestor.ASSIGNMENT_STATEMENT_TYPE_MISMATCH,
			new String[] {
				StatementValidator.getShortTypeString(annotationValueType),
				StatementValidator.getShortTypeString(suppliedType),
				getName() + "=" + expression.getCanonicalString()
			});
	}
    
    private boolean typeIsResolvable(ITypeBinding singleValueType) {
    	IPartBinding matchingSystemPart = (IPartBinding) SystemPartManager.findType(singleValueType.getName());
		if(matchingSystemPart == singleValueType) {
			return SystemEnvironmentPackageNames.EGL_CORE_REFLECT == matchingSystemPart.getPackageName() &&
		           matchingSystemPart != SystemPartManager.SQLSTRING_BINDING;
		}
		return false;
    }
    
    public IAnnotationBinding getAnnotationFor(IAnnotationTypeBinding annotationType, IDataBinding[] path) {
        return getAnnotation(annotationType);
    }

    public boolean isForElement() {
        return false;
    }
    
    public boolean isAnnotationField() {
        return false;
    }

    public IAnnotationBinding getAnnotationFor(IAnnotationTypeBinding annotationType, IDataBinding[] path, int index) {
        return getAnnotation(annotationType, index);
    }
    
    protected ITypeBinding getTypeBinding(ITypeBinding typeBinding, IDataBinding dataBinding) {
		return typeBinding;
	}
    
    private boolean validateIsArrayOfNames(final Expression rootExpression, final IProblemRequestor problemRequestor, final String annotationName) {
    	final boolean[] valid = new boolean[] {true};
    	rootExpression.accept(new AbstractASTExpressionVisitor() {
    		public boolean visitExpression(Expression expression) {
    			problemRequestor.acceptProblem(
    				rootExpression,
    				IProblemRequestor.ANNOTATION_VALUE_MUST_BE_NAME_ARRAY,
    				new String[] { expression.getCanonicalString(), annotationName });
    			valid[0] = false;
    			return false;
    		}
    		
            public boolean visit(ArrayLiteral arrayLiteral) {
            	for(Iterator iter = arrayLiteral.getExpressions().iterator(); iter.hasNext();) {
            		if(!validateIsName((Expression) iter.next(), problemRequestor, annotationName)) {
            			valid[0] = false;
            		}
            	}
            	return false;
            }
        });
    	
    	return valid[0];
    }
    
    private boolean validateIsName(final Expression expression, final IProblemRequestor problemRequestor, final String annotationName) {
    	final boolean[] valid = new boolean[] {true};
    	expression.accept(new AbstractASTExpressionVisitor() {
			public boolean visitExpression(Expression expression) {
    			problemRequestor.acceptProblem(
    				expression,
    				IProblemRequestor.ANNOTATION_VALUE_MUST_BE_NAME,
    				new String[] { expression.getCanonicalString(), annotationName });
    			valid[0] = false;
    			return false;
    		}
			
			public boolean visit(NullLiteral nilLiteral) {
				return false;
			}
			
			public boolean visitName(Name name) {
				return false;
			}
		});
    	return valid[0];
    }
    
    public void addAnnotation(IAnnotationBinding annotation) {
    	if(annotation.isAnnotationField()) {
    		if(fields == Collections.EMPTY_LIST) {
    			fields = new ArrayList();
    		}
    		fields.add(annotation);
    	}
    	else {
    		
    		//We may attempt to add an an annotation to itself for the following type of case:  Record myrecord {@sqlrecord{...}}
    		if (this != annotation) {
    			super.addAnnotation(annotation);
    		}
    	}
    }
    
    public IDataBinding findData(String simpleName) {
    	IDataBinding lastFound = IBinding.NOT_FOUND_BINDING;
        if(fields.size() > 0){
        	simpleName = InternUtil.intern(simpleName);
       
	    	for(Iterator iter = fields.iterator(); iter.hasNext();) {
	        	IDataBinding binding = (IDataBinding) iter.next();
	            if(binding.getName() == simpleName) {
	                lastFound = binding;
	            }
	        }
        }
        return lastFound;
    }
    
    public IAnnotationTypeBinding getEnclosingAnnotationType() {
    	return null;
    }
    
    public List getAnnotationFields() {
		return fields;
	}
    
    public void addField(IAnnotationBinding newField) {
    	if(fields == Collections.EMPTY_LIST) {
    		fields = new ArrayList();
    	}
    	fields.add(newField);
    }
    
    public boolean isCopiedAnnotationBinding() {
    	return false;
    }

	public void addFields(List list) {
		Iterator i = list.iterator();
		while (i.hasNext()) {
			addField((IAnnotationBinding)i.next());
		}
	}
	
	public ITypeBinding getType() {
		if (typeBinding instanceof AnnotationTypeBindingImpl) {
			AnnotationTypeBindingImpl temp = (AnnotationTypeBindingImpl) typeBinding;
			
			if (temp.getAnnotationRecord().isValid) {
				return super.getType();
			}
			
			IEnvironment env = getDeclaringPart() != null ? getDeclaringPart().getEnvironment() : null;
			if (env == null) {
				return null;
			}
	        temp.setAnnotationRecord((FlexibleRecordBinding)realizeTypeBinding(temp.getAnnotationRecord(), env));
		}
		return super.getType();
	}
	
	public boolean isStaticPartDataBinding() {
		return false;
	}
	
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        Map map = getFieldsHashMap();
        
        out.writeInt(map.size());
        Iterator i = map.keySet().iterator();
        while (i.hasNext()) {
        	String key = (String)i.next();
        	Object value = map.get(key);
        	out.writeUTF(key);

        	if (value instanceof ITypeBinding) {
                out.writeBoolean(true);
                writeTypeBindingReference(out, (ITypeBinding) value);
            }
            else {
                out.writeBoolean(false);
                if (value instanceof Node) {
                    out.writeUnshared(null);
                }
                else {
                    out.writeUnshared(value); 
                }
            }
        }
        
     }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        int len = in.readInt();
        
        fields = Collections.EMPTY_LIST;
        
        for (int i = 0; i < len; i++) {
			String key = in.readUTF();
			
	        boolean isType = in.readBoolean();
	        Object value;
	        if (isType) {
	            value = readTypeBindingReference(in);
	        }
	        else {
	            value = in.readUnshared();
	        }

        	AnnotationFieldBinding field = new AnnotationFieldBinding(key, null, PrimitiveTypeBinding.getInstance(Primitive.ANY), null);
        	field.setValue(value, null, null, null, false);
        	addField(field);
		}
    }
    
    private Map getFieldsHashMap() {
    	Map map = Collections.EMPTY_MAP;
  
    	if(getAnnotationFields().size() > 0){
			map = new HashMap();
			Iterator i = getAnnotationFields().iterator();
			while (i.hasNext()) {
				AnnotationFieldBinding field = (AnnotationFieldBinding) i.next();
				
				if (field.getValue() instanceof Expression) {
					map.put(field.getCaseSensitiveName().toUpperCase().toLowerCase(), field.getValue().toString());
				}
				else {
					map.put(field.getCaseSensitiveName().toUpperCase().toLowerCase(), field.getValue());
				}
			}
    	}

		return map;
	}

	public boolean isCalculatedValue() {
		return isCalculated ;
	}
	
	public void setCalculated(boolean b) {
		isCalculated = b;
	}
	
	//Creates a copy of this annotation binding...also creates copies of the fields
	public AnnotationBinding createCopy() {
		AnnotationBinding copy = new AnnotationBinding(getCaseSensitiveName(), getDeclaringPart(), getType(), true);
		populateCopy(copy);
		return copy;
	}
	protected void populateCopy(AnnotationBinding copy) {
		copy.setCalculated(isCalculatedValue());
		copy.addAnnotations(getAnnotations());
		Iterator i = fields.iterator();
		while (i.hasNext()) {
			copy.addField(((AnnotationFieldBinding)i.next()).createCopy());
		}
	}

}
