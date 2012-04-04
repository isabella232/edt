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
import java.util.List;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.MutuallyExclusiveAnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedFieldContentAnnotationValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.MinimumInputForStructureItemValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.TypeAheadAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.TypeAheadFunctionForRecordAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValidValuesForPageItemAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValidatorDataTableForPageItemFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValueForNonFormFieldValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
class BasicRecordAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String caseSensitiveName = InternUtil.internCaseSensitive("BasicRecord");
    public static final String name = InternUtil.intern(caseSensitiveName);

    private static BasicRecordAnnotationTypeBinding INSTANCE = new BasicRecordAnnotationTypeBinding();
    
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
   		
  		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(TypeaheadFunctionAnnotationTypeBinding.caseSensitiveName,
   				new String[]{
   					TypeaheadAnnotationTypeBinding.caseSensitiveName,
   					ValidatorDataTableAnnotationTypeBinding.caseSensitiveName,
   					ValidValuesAnnotationTypeBinding.caseSensitiveName
   				}
   	   		));
  		
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(MinimumInputForStructureItemValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(ValueForNonFormFieldValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(TypeAheadFunctionForRecordAnnotationValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(TypeAheadAnnotationValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(ValidValuesForPageItemAnnotationValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(ValidatorDataTableForPageItemFieldValidator.class));
   	}
   	
   	private static final List basicRecordAnnotations = new ArrayList();
   	static{
   	}
   	
    public BasicRecordAnnotationTypeBinding() {
        super(caseSensitiveName, new Object[0]);
    }
    
    public static BasicRecordAnnotationTypeBinding getInstance() {
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
		return basicRecordAnnotations;
	}
}
