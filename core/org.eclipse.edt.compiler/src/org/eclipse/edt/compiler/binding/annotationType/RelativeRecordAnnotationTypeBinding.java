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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.MutuallyExclusiveAnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.RequiredFieldInPartTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedFieldContentAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.validation.annotation.RecordNumItemValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.TypeAheadAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.TypeAheadFunctionForRecordAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValidValuesForPageItemAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValidatorDataTableForPageItemFieldValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
class RelativeRecordAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String caseSensitiveName = InternUtil.internCaseSensitive("RelativeRecord");
    public static final String name = InternUtil.intern(caseSensitiveName);
    
    public RelativeRecordAnnotationTypeBinding() {
        super(caseSensitiveName, new Object[] {
        	IEGLConstants.PROPERTY_FILENAME,	PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	IEGLConstants.PROPERTY_KEYITEM,		SystemPartManager.INTERNALREF_BINDING
        });
    }

    private static final List subPartTypeAnnotations = new ArrayList();
   	static{
 		
  		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(TypeaheadFunctionAnnotationTypeBinding.caseSensitiveName,
   				new String[]{
   					TypeaheadAnnotationTypeBinding.caseSensitiveName,
   					ValidatorDataTableAnnotationTypeBinding.caseSensitiveName,
   					ValidValuesAnnotationTypeBinding.caseSensitiveName
   				}
   	   		));
  		
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(TypeAheadFunctionForRecordAnnotationValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(TypeAheadAnnotationValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(ValidValuesForPageItemAnnotationValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(ValidatorDataTableForPageItemFieldValidator.class));
   	}
   	
    private static RelativeRecordAnnotationTypeBinding INSTANCE = new RelativeRecordAnnotationTypeBinding();
    
    private static final List relativeRecordAnnotations = new ArrayList();
   	static{
   		relativeRecordAnnotations.add(new RequiredFieldInPartTypeAnnotationTypeBinding(
   			new String[]{
   				IEGLConstants.PROPERTY_FILENAME,
				IEGLConstants.PROPERTY_RECORDNUMITEM},
			caseSensitiveName));
   	}
    
    private static final ArrayList recordNumItemAnnotations = new ArrayList();
   	static{
   		recordNumItemAnnotations.add(new UserDefinedValueValidationRule(RecordNumItemValidator.class));
   	}
   	
    private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_RECORDNUMITEM), recordNumItemAnnotations);
   	}
    
    public static RelativeRecordAnnotationTypeBinding getInstance() {
        return INSTANCE;
    }
    
    public boolean isApplicableFor(IBinding binding) {
         return binding.isTypeBinding() && ((ITypeBinding) binding).getKind() == ITypeBinding.FIXED_RECORD_BINDING;
    }

    private Object readResolve() {
        return INSTANCE;
    }
    
    public List getFieldAnnotations(String member){
		return (List)fieldAnnotations.get(member);
	}
    
	public List getAnnotations() {
		return relativeRecordAnnotations;
	}
	
    public List getPartSubTypeAnnotations(){
		return subPartTypeAnnotations;
	}
}
