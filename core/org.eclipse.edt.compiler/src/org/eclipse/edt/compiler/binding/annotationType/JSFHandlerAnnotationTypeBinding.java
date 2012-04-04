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
package org.eclipse.edt.compiler.binding.annotationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.MutuallyExclusiveAnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedFieldContentAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedPartContentAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.ScopeKind;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.validation.annotation.BypassValidationForPageItemFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.DateAndTimeFormatForJSFHandlerValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.JSFHandlerOnPrerenderFunctionAndOnConstructionFunctionValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.JSFHandlerValidationOrderValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.NewWindowForPageItemFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.NumElementsItemForPageItemFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.OnConstructionFunctionAnnotationValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.OnPrerenderFunctionAnnotationValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.OnValueChangeFunctionForPageItemFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.SelectTypeForPageItemFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.SelectedValueItemAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.TypeAheadAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.TypeAheadFunctionAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValidValuesForPageItemAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValidatorDataTableForPageItemFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValidatorFunctionAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValueForNonFormFieldValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Svihovec
 */
class JSFHandlerAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String caseSensitiveName = InternUtil.internCaseSensitive("BasicPageHandler");
    public static final String name = InternUtil.intern(caseSensitiveName);

    private static JSFHandlerAnnotationTypeBinding INSTANCE = new JSFHandlerAnnotationTypeBinding();
    
    private static final List subPartTypeAnnotations = new ArrayList();
   	static{
   		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(
   			DateFormatAnnotationTypeBinding.caseSensitiveName,
   			new String[]{
   				NumericSeparatorAnnotationTypeBinding.caseSensitiveName,
   				CurrencyAnnotationTypeBinding.caseSensitiveName,
   				SignAnnotationTypeBinding.caseSensitiveName,
   				ZeroFormatAnnotationTypeBinding.caseSensitiveName,
   				ValidValuesAnnotationTypeBinding.caseSensitiveName,
   				TimeFormatAnnotationTypeBinding.caseSensitiveName,
   				IsBooleanAnnotationTypeBinding.caseSensitiveName
   			}
   		));
   		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(TimeFormatAnnotationTypeBinding.caseSensitiveName,
			new String[]{
				IsBooleanAnnotationTypeBinding.caseSensitiveName
			}
   		));
   		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(SelectedRowItemAnnotationTypeBinding.caseSensitiveName,
			new String[]{
				SelectedValueItemAnnotationTypeBinding.caseSensitiveName,
				ValidatorDataTableAnnotationTypeBinding.caseSensitiveName,
				ValidValuesAnnotationTypeBinding.caseSensitiveName
			}
   		));
   		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(SelectedValueItemAnnotationTypeBinding.caseSensitiveName,
			new String[]{
				ValidatorDataTableAnnotationTypeBinding.caseSensitiveName,
				ValidValuesAnnotationTypeBinding.caseSensitiveName
			}
   		));
   		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(ValidatorDataTableAnnotationTypeBinding.caseSensitiveName,
			new String[]{
				ValidValuesAnnotationTypeBinding.caseSensitiveName
			}
   		));
   		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(SelectTypeAnnotationTypeBinding.caseSensitiveName,
			new String[]{
				SelectedRowItemAnnotationTypeBinding.caseSensitiveName,
				SelectedValueItemAnnotationTypeBinding.caseSensitiveName
			}
   		));
   		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(IsBooleanAnnotationTypeBinding.caseSensitiveName,
			new String[]{
				ValidatorDataTableAnnotationTypeBinding.caseSensitiveName
			}
   		));
   		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(SelectFromListItemAnnotationTypeBinding.caseSensitiveName,
   				new String[]{
   					SelectedRowItemAnnotationTypeBinding.caseSensitiveName,
   					SelectedValueItemAnnotationTypeBinding.caseSensitiveName,
   					ValidatorDataTableAnnotationTypeBinding.caseSensitiveName,
   					ValidValuesAnnotationTypeBinding.caseSensitiveName
   				}
   	   		));
   		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(TypeaheadFunctionAnnotationTypeBinding.caseSensitiveName,
   				new String[]{
   					TypeaheadAnnotationTypeBinding.caseSensitiveName,
   					ValidatorDataTableAnnotationTypeBinding.caseSensitiveName,
   					ValidValuesAnnotationTypeBinding.caseSensitiveName
   				}
   	   		));
   		
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(BypassValidationForPageItemFieldValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(DateAndTimeFormatForJSFHandlerValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(NewWindowForPageItemFieldValidator.class));   		
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(NumElementsItemForPageItemFieldValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(OnValueChangeFunctionForPageItemFieldValidator.class));   		
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(SelectedValueItemAnnotationValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(SelectTypeForPageItemFieldValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(ValidatorDataTableForPageItemFieldValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(ValueForNonFormFieldValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(TypeAheadFunctionAnnotationValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(TypeAheadAnnotationValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(ValidValuesForPageItemAnnotationValidator.class));
   		
   		
   		
   	}
   	
   	private static final List partTypeAnnotations = new ArrayList();
   	static{
   		partTypeAnnotations.add(new UserDefinedPartContentAnnotationValidationRule(JSFHandlerValidationOrderValidator.class));
   	}
   	
   	private static final List basicPageHandlerAnnotations = new ArrayList();
   	static{
   		basicPageHandlerAnnotations.add(new UserDefinedAnnotationValidationRule(JSFHandlerOnPrerenderFunctionAndOnConstructionFunctionValidator.class));
   	}
   	
    private static final ArrayList onConstructionFunctionAnnotations = new ArrayList();
   	static{
   		onConstructionFunctionAnnotations.add(new UserDefinedValueValidationRule(OnConstructionFunctionAnnotationValueValidator.class));
   	}
   	
   	private static final ArrayList onPrerenderFunctionAnnotations = new ArrayList();
   	static{
   		onPrerenderFunctionAnnotations.add(new UserDefinedValueValidationRule(OnPrerenderFunctionAnnotationValueValidator.class));
   	}
   	
   	private static final ArrayList validatorFunctionAnnotations = new ArrayList();
   	static{
   		validatorFunctionAnnotations.add(new UserDefinedValueValidationRule(ValidatorFunctionAnnotationValidator.class));
   	}
   	
  	
    private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_ONCONSTRUCTIONFUNCTION), onConstructionFunctionAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_ONPRERENDERFUNCTION), onPrerenderFunctionAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORFUNCTION), validatorFunctionAnnotations);
	
   		
   	}
   	
    public JSFHandlerAnnotationTypeBinding() {
        super(caseSensitiveName, new Object[] {
        	IEGLConstants.PROPERTY_EVENTVALUEITEM,				PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	IEGLConstants.PROPERTY_LABELANDHELPRESOURCE,		PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	IEGLConstants.PROPERTY_MSGRESOURCE,					PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	IEGLConstants.PROPERTY_ONCONSTRUCTIONFUNCTION,		SystemPartManager.INTERNALREF_BINDING,
        	IEGLConstants.PROPERTY_ONPRERENDERFUNCTION,			SystemPartManager.INTERNALREF_BINDING,
        	IEGLConstants.PROPERTY_SCOPE,						ScopeKind.TYPE,
        	IEGLConstants.PROPERTY_TITLE,						PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	IEGLConstants.PROPERTY_VALIDATIONBYPASSFUNCTIONS,	ArrayTypeBinding.getInstance(SystemPartManager.INTERNALREF_BINDING),
        	IEGLConstants.PROPERTY_VALIDATORFUNCTION,			SystemPartManager.INTERNALREF_BINDING,
        	IEGLConstants.PROPERTY_VIEW,						PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	IEGLConstants.PROPERTY_VIEWROOTVAR,					SystemPartManager.INTERNALREF_BINDING,        	
        },
        new Object[] {
        	IEGLConstants.PROPERTY_SCOPE,	ScopeKind.SESSION	
        });
    }
    
    public static JSFHandlerAnnotationTypeBinding getInstance() {
        return INSTANCE;
    }
    
    public boolean isApplicableFor(IBinding binding) {
        return binding.isTypeBinding() && (((ITypeBinding) binding).getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING || ((ITypeBinding) binding).getKind() == ITypeBinding.FIXED_RECORD_BINDING);
   }

    private Object readResolve() {
        return INSTANCE;
    }

    public List getPartSubTypeAnnotations(){
		return subPartTypeAnnotations;
	}

	public List getAnnotations(){
		return basicPageHandlerAnnotations;
	}
	
	public List getPartTypeAnnotations(){
		return partTypeAnnotations;
	}
	
	public List getFieldAnnotations(String field) {
		return (List) fieldAnnotations.get(field);
	}
}
