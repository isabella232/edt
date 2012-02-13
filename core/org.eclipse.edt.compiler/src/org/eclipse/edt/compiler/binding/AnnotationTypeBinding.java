/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.core.ast.ArrayLiteral;
import org.eclipse.edt.compiler.core.ast.BinaryExpression;
import org.eclipse.edt.compiler.core.ast.BooleanLiteral;
import org.eclipse.edt.compiler.core.ast.CharLiteral;
import org.eclipse.edt.compiler.core.ast.DBCharLiteral;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.HexLiteral;
import org.eclipse.edt.compiler.core.ast.IntegerLiteral;
import org.eclipse.edt.compiler.core.ast.MBCharLiteral;
import org.eclipse.edt.compiler.core.ast.SQLLiteral;
import org.eclipse.edt.compiler.core.ast.StringLiteral;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public abstract class AnnotationTypeBinding extends TypeBinding implements IAnnotationTypeBinding {
    
	protected Map fields = Collections.EMPTY_MAP;
	protected Map defaultValues = Collections.EMPTY_MAP;
	
	protected Map caseSensitiveFieldNames = Collections.EMPTY_MAP;
	
	/**
     * @param simpleName
     */

    public AnnotationTypeBinding(String caseSensitiveInternedName) {
        this(caseSensitiveInternedName, new Object[] {});
    }
	
	public AnnotationTypeBinding(String caseSensitiveInternedName, ITypeBinding singleValueType) {
        this(caseSensitiveInternedName, new Object[] {
        	"value",	singleValueType
        });
    }
    
    public AnnotationTypeBinding(String caseSensitiveInternedName, Object[] fieldNamesAndTypes) {
    	this(caseSensitiveInternedName, fieldNamesAndTypes, new Object[0]);
    }
    
    public AnnotationTypeBinding(String caseSensitiveInternedName, Object[] fieldNamesAndTypes, Object[] defaultValues) {
    	super(caseSensitiveInternedName);
    	
    	if(fieldNamesAndTypes.length != 0) {
    		fields = new HashMap();
    		caseSensitiveFieldNames = new HashMap();
    		for(int i = 0; i < fieldNamesAndTypes.length; i+=2) {
    			fields.put(InternUtil.intern((String) fieldNamesAndTypes[i]), fieldNamesAndTypes[i+1]);
    			caseSensitiveFieldNames.put(InternUtil.intern((String) fieldNamesAndTypes[i]), InternUtil.internCaseSensitive((String) fieldNamesAndTypes[i]));
    		}
    	}
    	
    	if(defaultValues.length != 0) {
    		this.defaultValues = new HashMap();
    		for(int i = 0; i < defaultValues.length; i+=2) {
    			this.defaultValues.put(InternUtil.intern((String) defaultValues[i]), defaultValues[i+1]);
    		}
    	}
    }

    public boolean isPartSubType() {
        return false;
    }

    public int getKind() {
        return ANNOTATION_BINDING;
    }
    
    public boolean isComplex() {
        return false;
    }
    
    public boolean isValueless() {
    	return false;
    }
    
    public EnumerationTypeBinding getEnumerationType() {
        return null;
    }
    
    public ITypeBinding getBaseType() {
        return this;
    }
    
    public Object getDefaultValue() {
    	return null;
    }
    
    public Object getDefaultValueForField(String fieldName) {
    	return defaultValues.get(fieldName);
    }
    
    public List getFieldNames() {
    	return new ArrayList(fields.keySet());
    }
    
    public List getCaseSensitiveFieldNames() {
    	return new ArrayList(caseSensitiveFieldNames.values());
    }
    
    private static boolean hasAnnotation(String[] packageName, String annotationName, IPartBinding partBinding) {
    	return partBinding == null ? false : partBinding.getAnnotation(packageName, annotationName) != null;
    }
    
    private static boolean isFixedRecord(IPartBinding partBinding) {
    	return partBinding == null ? false : partBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING;
    }
    
    private static boolean isForm(IPartBinding partBinding) {
    	return partBinding == null ? false : partBinding.getKind() == ITypeBinding.FORM_BINDING;
    }
    
    private static boolean isVariableFormField(IDataBinding dBinding) {
    	return dBinding.getKind() == IDataBinding.FORM_FIELD && !((FormFieldBinding) dBinding).isConstant();
    }
    
    private static boolean isConstantFormField(IDataBinding dBinding) {
    	return dBinding.getKind() == IDataBinding.FORM_FIELD && ((FormFieldBinding) dBinding).isConstant();
    }
    
    protected static boolean takesConsoleArrayFieldAnnotations(IBinding binding) {
    	if(binding.isTypeBinding()) {
    		return ((ITypeBinding) binding).getKind() == ITypeBinding.DATAITEM_BINDING;
    	}
    	if(binding.isDataBinding()) {
    		IDataBinding dBinding = (IDataBinding) binding;
    		IPartBinding declaringPart = dBinding.getDeclaringPart();
    		return hasAnnotation(new String[] {"egl", "ui", "console"}, "ConsoleForm", declaringPart);
    	}
    	return false;
    }
    
    protected static boolean takesConsoleFieldAnnotations(IBinding binding) {
    	if(binding.isTypeBinding()) {
    		return ((ITypeBinding) binding).getKind() == ITypeBinding.DATAITEM_BINDING;
    	}
    	if(binding.isDataBinding()) {
    		IDataBinding dBinding = (IDataBinding) binding;
    		IPartBinding declaringPart = dBinding.getDeclaringPart();
    		if(!hasAnnotation(new String[] {"egl", "ui", "console"}, "ConsoleForm", declaringPart)) {
    			return false;
    		}
    		return true;			
    	}
    	return false;
    }
    
    protected static boolean takesDLIItemAnnotations(IBinding binding) {
    	if(binding.isTypeBinding()) {
    		return ((ITypeBinding) binding).getKind() == ITypeBinding.DATAITEM_BINDING;
    	}
    	if(binding.isDataBinding()) {
    		IDataBinding dBinding = (IDataBinding) binding;
    		IPartBinding declaringPart = dBinding.getDeclaringPart();
    		return isFixedRecord(declaringPart);			
    	}
    	return false;
    }
    
    protected static boolean takesFieldPresentationAnnotations(IBinding binding) {
    	return takesTextFormFieldAnnotations(binding);
    }
    
    protected static boolean takesFillerStructureItemAnnotations(IBinding binding) {
    	if(binding.isTypeBinding()) {
    		return ((ITypeBinding) binding).getKind() == ITypeBinding.DATAITEM_BINDING;
    	}
    	if(binding.isDataBinding()) {
    		return ((IDataBinding) binding).getKind() == IDataBinding.STRUCTURE_ITEM_BINDING &&
			       binding.getName() == InternUtil.intern("*");
    	}
    	return false;
    }
    
    protected static boolean takesFormattingAnnotations(IBinding binding) {
    	if(binding.isTypeBinding()) {
    		return ((ITypeBinding) binding).getKind() == ITypeBinding.DATAITEM_BINDING;
    	}
    	if(binding.isDataBinding()) {
    		IDataBinding dBinding = (IDataBinding) binding;
    		if(dBinding.getKind() == IDataBinding.FLEXIBLE_RECORD_FIELD &&
    		   dBinding.getType() != null &&
			   dBinding.getType().getKind() != ITypeBinding.ARRAY_TYPE_BINDING) {
    			return true;
    		}
    		if(dBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING &&
    		   dBinding.getName() != InternUtil.intern("*")) {
    		   	return true;
    		}
    		if(isVariableFormField(dBinding)) {
    			return true;
    		}
    	}
    	return takesConsoleArrayFieldAnnotations(binding) || takesPageItemAnnotations(binding);
    }
    
    protected static boolean takesFormFieldAnnotations(IBinding binding) {
    	if(binding.isTypeBinding()) {
    		return ((ITypeBinding) binding).getKind() == ITypeBinding.DATAITEM_BINDING;
    	}
    	if(binding.isDataBinding()) {
    		IDataBinding dBinding = (IDataBinding) binding;
    		IPartBinding declaringPart = dBinding.getDeclaringPart();
    		return isForm(declaringPart);
    	}
    	return false;
    }
    
    protected static boolean takesPageItemAnnotations(IBinding binding) {
    	if(binding.isTypeBinding()) {
    		return ((ITypeBinding) binding).getKind() == ITypeBinding.DATAITEM_BINDING;
    	}
    	if(binding.isDataBinding()) {
    		IDataBinding dBinding = (IDataBinding) binding;
    		
    		if(dBinding.getKind() == IDataBinding.CLASS_FIELD_BINDING ||
    		   dBinding.getKind() == IDataBinding.LOCAL_VARIABLE_BINDING) {
    			return true;
    		}
    		
    		if(dBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING) {
    			return dBinding.getName() != InternUtil.intern("*");
    		}    				
    				
    		if(dBinding.getKind() == IDataBinding.FLEXIBLE_RECORD_FIELD) {
    			return !takesConsoleFieldAnnotations(binding) &&
				       !takesConsoleArrayFieldAnnotations(binding);
    		}
    	}
    	return false;
    }
    
    protected static boolean takesSQLItemAnnotations(IBinding binding) {
    	return takesPageItemAnnotations(binding);
    }
    
    protected static boolean takesTextConstantFormFieldAnnotations(IBinding binding) {
    	if(binding.isTypeBinding()) {
    		return ((ITypeBinding) binding).getKind() == ITypeBinding.DATAITEM_BINDING;
    	}
    	if(binding.isDataBinding()) {
    		IDataBinding dBinding = (IDataBinding) binding;
    		IPartBinding declaringPart = dBinding.getDeclaringPart();
    		return isConstantFormField(dBinding) && hasAnnotation(new String[] {"egl", "ui", "text"}, "TextForm", declaringPart);
    	}
    	return false;
    }
    
    protected static boolean takesDataItemBinding(IBinding binding){
    	if(binding.isTypeBinding()) {
    		return ((ITypeBinding) binding).getKind() == ITypeBinding.DATAITEM_BINDING;
    	}
    	
    	return false;
    }
    
    protected static boolean takesTextFormFieldAnnotations(IBinding binding) {
    	return takesTextVariableFormFieldAnnotations(binding) ||
		       takesTextConstantFormFieldAnnotations(binding);
    }
    
    protected static boolean takesTextVariableFormFieldAnnotations(IBinding binding) {
    	if(binding.isTypeBinding()) {
    		return ((ITypeBinding) binding).getKind() == ITypeBinding.DATAITEM_BINDING;
    	}
    	if(binding.isDataBinding()) {
    		IDataBinding dBinding = (IDataBinding) binding;
    		IPartBinding declaringPart = dBinding.getDeclaringPart();
    		return isVariableFormField(dBinding) && hasAnnotation(new String[] {"egl", "ui", "text"}, "TextForm", declaringPart);
    	}
    	return false;
    }
    
    protected static boolean takesUIItemAnnotations(IBinding binding) {
    	if(binding.isTypeBinding()) {
    		return ((ITypeBinding) binding).getKind() == ITypeBinding.DATAITEM_BINDING;
    	}
    	if(binding.isDataBinding()) {
    		IDataBinding dBinding = (IDataBinding) binding;
    		if(dBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING &&
    		   dBinding.getName() != InternUtil.intern("*")) {
    		   	return true;
    		}
    	}
    	return false;
    }
    
    protected static boolean takesValidationAnnotations(IBinding binding) {
    	if(binding.isTypeBinding()) {
    		return ((ITypeBinding) binding).getKind() == ITypeBinding.DATAITEM_BINDING;
    	}
    	if(binding.isDataBinding()) {
    		IDataBinding dBinding = (IDataBinding) binding;
    		if(dBinding.getKind() == IDataBinding.FLEXIBLE_RECORD_FIELD) {
    			if(dBinding.getType().getKind() != ITypeBinding.ARRAY_TYPE_BINDING &&
    			   !takesConsoleFieldAnnotations(binding)) {
    			   	return true;
    			}  
    		}
    		if(dBinding.getKind() == IDataBinding.STRUCTURE_ITEM_BINDING &&
    		   dBinding.getName() != InternUtil.intern("*")) {
    		   	return true;
    		}
    	}
    	return takesTextVariableFormFieldAnnotations(binding) ||
    	       takesPageItemAnnotations(binding);
    }
    
    protected static boolean takesVariableFormFieldAnnotations(IBinding binding) {
    	if(binding.isDataBinding()) {
    		return isVariableFormField((IDataBinding) binding);
    	}
    	return false;
    }
        
    public static class IsStringLiteralChecker extends DefaultASTVisitor {
		boolean isStringLiteral = false;
		
		public boolean isStringLiteral(Expression expr) {
			expr.accept(this);
			return isStringLiteral;
		}
		
		public boolean visit(StringLiteral stringLiteral) {
			isStringLiteral = true;
			return false;
		}
		
		public boolean visit(CharLiteral stringLiteral) {
			isStringLiteral = true;
			return false;
		}
		
		public boolean visit(DBCharLiteral stringLiteral) {
			isStringLiteral = true;
			return false;
		}
		
		public boolean visit(MBCharLiteral stringLiteral) {
			isStringLiteral = true;
			return false;
		}
		
		public boolean visit(HexLiteral stringLiteral) {
			isStringLiteral = true;
			return false;
		}
		
		public boolean visit(BinaryExpression binaryExpression) {
			if(binaryExpression.getOperator() == BinaryExpression.Operator.PLUS) {
				IsStringLiteralChecker checker1 = new IsStringLiteralChecker();
				IsStringLiteralChecker checker2 = new IsStringLiteralChecker();
				isStringLiteral = checker1.isStringLiteral(binaryExpression.getFirstExpression()) &&
				                  checker2.isStringLiteral(binaryExpression.getSecondExpression());
				
			}
			return false;
		}
	}
    
    public static class IsBooleanLiteralChecker extends DefaultASTVisitor {
		boolean isBooleanLiteral = false;
		
		public boolean isBooleanLiteral(Expression expr) {
			expr.accept(this);
			return isBooleanLiteral;
		}
		
		public boolean visit(BooleanLiteral booleanLiteral) {
			isBooleanLiteral = true;
			return false;
		}
	}
    
    public static class IsStringArrayLiteralChecker extends DefaultASTVisitor {
		boolean isStringArrayLiteral = false;
		
		public boolean isStringArrayLiteral(Expression expr) {
			expr.accept(this);
			return isStringArrayLiteral;
		}
		
		public boolean visit(ArrayLiteral arrayLiteral) {
			for(Iterator iter = arrayLiteral.getExpressions().iterator(); iter.hasNext();) {
				if(!new IsStringLiteralChecker().isStringLiteral((Expression) iter.next())) {
					return false;
				}
			}
			isStringArrayLiteral = true;
			return false;
		}
	}
    
    public static class IsArrayLiteralChecker extends DefaultASTVisitor {
		boolean isArrayLiteral = false;
		
		public boolean isArrayLiteral(Expression expr) {
			expr.accept(this);
			return isArrayLiteral;
		}
		
		public boolean visit(ArrayLiteral arrayLiteral) {
			isArrayLiteral = true;
			return false;
		}
	}
    
    public static class IsIntegerLiteralChecker extends DefaultASTVisitor {
		boolean isIntegerLiteral = false;
		
		public boolean isIntegerLiteral(Expression expr) {
			expr.accept(this);
			return isIntegerLiteral;
		}
		
		public boolean visit(IntegerLiteral integerLiteral) {
			isIntegerLiteral = true;
			return false;
		}
	}
    
    public static class IsIntegerArrayLiteralChecker extends DefaultASTVisitor {
		boolean isIntegerArrayLiteral = false;
		
		public boolean isIntegerArrayLiteral(Expression expr) {
			expr.accept(this);
			return isIntegerArrayLiteral;
		}
		
		public boolean visit(ArrayLiteral arrayLiteral) {
			for(Iterator iter = arrayLiteral.getExpressions().iterator(); iter.hasNext();) {
				if(!new IsIntegerLiteralChecker().isIntegerLiteral((Expression) iter.next())) {
					return false;
				}
			}
			isIntegerArrayLiteral = true;
			return false;
		}
	}
    
    public static class IsInlineSQLLiteralLiteralChecker extends DefaultASTVisitor {
		boolean isSQLLiteral = false;
		
		public boolean isInlineSQLLiteralLiteralChecker(Expression expr) {
			expr.accept(this);
			return isSQLLiteral;
		}
		
		public boolean visit(SQLLiteral sQLLiteral) {
			isSQLLiteral = true;
			return false;
		}
    }
    
    public boolean supportsElementOverride() {
        return false;
    }
    
	public boolean takesExpressionInOpenUIStatement() {
		return false;
	}
	
	public ITypeBinding requiredTypeForOpenUIStatement() {
		return null;
	}	
    
    public List getValueAnnotations(){
    	return Collections.EMPTY_LIST;
    }
    
    public List getAnnotations(){
    	return Collections.EMPTY_LIST;
    }
    
    public boolean hasSingleValue() {
    	return true;
    }
    
    public ITypeBinding getSingleValueType() {
    	return null;
    }
    
    public IDataBinding findData(String simpleName) {
    	ITypeBinding tBinding = (ITypeBinding) fields.get(InternUtil.intern(simpleName));
    	if(tBinding != null) {
    		final AnnotationTypeBinding aTypeBinding = this;
			final ITypeBinding fieldBindingType = tBinding;
			return new AnnotationFieldBinding((String)caseSensitiveFieldNames.get(InternUtil.intern(simpleName)), null, new AnnotationTypeBinding((String)caseSensitiveFieldNames.get(InternUtil.intern(simpleName)), new Object[0]) {
				public boolean isApplicableFor(IBinding binding) {
					return binding == aTypeBinding;
				}
				
				public boolean hasSingleValue() {
					return true;
				}
				
				public ITypeBinding getSingleValueType() {
					return fieldBindingType; 
				}
				
				public EnumerationTypeBinding getEnumerationType() {
					return ITypeBinding.ENUMERATION_BINDING == fieldBindingType.getBaseType().getKind() ?
						(EnumerationTypeBinding) fieldBindingType.getBaseType() : null;
				}

				public boolean isValueless() {
					return false;
				}

			}, this);
    	}
    	return IBinding.NOT_FOUND_BINDING;
    }

	public List getFieldAnnotations(String field) {
		return Collections.EMPTY_LIST;
	}
	
	public List getPartSubTypeAnnotations(){
		return Collections.EMPTY_LIST;
	}
	
	public List getPartTypeAnnotations(){
		return Collections.EMPTY_LIST;
	}
	
	public List getFieldAccessAnnotations(){
		return Collections.EMPTY_LIST;
	}
	
	public IAnnotationTypeBinding getValidationProxy() {
		return this;
	}
	
	public boolean isSystemAnnotation() {
		return false;
	}
	
	public boolean isBIDIEnabled() {
		return false;
	}
	
	@Override
	public ITypeBinding primGetNullableInstance() {
		return this;
	}
}
