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
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.validation.annotation.LengthItemForSerialMessageOrIndexedRecordValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.MinimumInputForStructureItemValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.NumElementsItemForSerialMessageOrIndexedRecordValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.TypeAheadAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.TypeAheadFunctionForRecordAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValidValuesForPageItemAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValidatorDataTableForPageItemFieldValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValueForNonFormFieldValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
class MQRecordAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String caseSensitiveName = InternUtil.internCaseSensitive("MQRecord");
    public static final String name = InternUtil.intern(caseSensitiveName);
    
    public MQRecordAnnotationTypeBinding() {
        super(caseSensitiveName, new Object[] {
        	IEGLConstants.PROPERTY_GETOPTIONSRECORD,		SystemPartManager.INTERNALREF_BINDING,
        	IEGLConstants.PROPERTY_INCLUDEMSGINTRANSACTION,	PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
        	IEGLConstants.PROPERTY_LENGTHITEM,				SystemPartManager.INTERNALREF_BINDING,
        	IEGLConstants.PROPERTY_MSGDESCRIPTORRECORD,		SystemPartManager.INTERNALREF_BINDING,
        	IEGLConstants.PROPERTY_NUMELEMENTSITEM,			SystemPartManager.INTERNALREF_BINDING,        	
        	IEGLConstants.PROPERTY_OPENOPTIONSRECORD,		SystemPartManager.INTERNALREF_BINDING,
        	IEGLConstants.PROPERTY_OPENQUEUEEXCLUSIVE,		PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
        	IEGLConstants.PROPERTY_PUTOPTIONSRECORD,		SystemPartManager.INTERNALREF_BINDING,
        	IEGLConstants.PROPERTY_QUEUEDESCRIPTORRECORD,	SystemPartManager.INTERNALREF_BINDING,
        	IEGLConstants.PROPERTY_QUEUENAME,				PrimitiveTypeBinding.getInstance(Primitive.STRING)
        },
        new Object[] {
        	IEGLConstants.PROPERTY_INCLUDEMSGINTRANSACTION, Boolean.YES,	
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
   	
    private static MQRecordAnnotationTypeBinding INSTANCE = new MQRecordAnnotationTypeBinding();
    
    private static final List mqRecordAnnotations = new ArrayList();
   	static{
   		mqRecordAnnotations.add(new RequiredFieldInPartTypeAnnotationTypeBinding(
   			new String[]{IEGLConstants.PROPERTY_QUEUENAME},
   			caseSensitiveName));
   	}
    
    private static final ArrayList lengthItemAnnotations = new ArrayList();
   	static{
   		lengthItemAnnotations.add(new UserDefinedValueValidationRule(LengthItemForSerialMessageOrIndexedRecordValidator.class));
   	}
    
    private static final ArrayList numElementsItemAnnotations = new ArrayList();
   	static{
   		numElementsItemAnnotations.add(new UserDefinedValueValidationRule(NumElementsItemForSerialMessageOrIndexedRecordValidator.class));
   	}
   	
   	private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_LENGTHITEM), lengthItemAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_NUMELEMENTSITEM), numElementsItemAnnotations);
   	}
    
    public static MQRecordAnnotationTypeBinding getInstance() {
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
		return mqRecordAnnotations;
	}
	
    public List getPartSubTypeAnnotations(){
		return subPartTypeAnnotations;
	}
    
}
