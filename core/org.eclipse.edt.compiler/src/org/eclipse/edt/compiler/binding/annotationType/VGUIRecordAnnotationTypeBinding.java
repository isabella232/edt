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

import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.MutuallyExclusiveAnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedFieldContentAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AliasForVGUIRecordValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.NumElementsItemForVGUIRecordItemValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PrimitiveTypeNotAllowedInPartOfSubtypeValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.SelectedIndexItemForVGUIRecordItemValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.UITypeForVGUIRecordItemValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValidatorFunctionAnnotationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
class VGUIRecordAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String caseSensitiveName = InternUtil.internCaseSensitive("VGUIRecord");
    public static final String name = InternUtil.intern(caseSensitiveName);
    
    public VGUIRecordAnnotationTypeBinding() {
        super(caseSensitiveName, new Object[] {
        	IEGLConstants.PROPERTY_ALIAS,					PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	IEGLConstants.PROPERTY_COMMANDVALUEITEM,		SystemPartManager.INTERNALREF_BINDING,
        	IEGLConstants.PROPERTY_HELP,					PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	IEGLConstants.PROPERTY_RUNVALIDATORFROMPROGRAM,	PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
        	IEGLConstants.PROPERTY_TITLE,					PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	IEGLConstants.PROPERTY_VALIDATORFUNCTION,		SystemPartManager.INTERNALREF_BINDING,        	
        });
    }
    
    private static VGUIRecordAnnotationTypeBinding INSTANCE = new VGUIRecordAnnotationTypeBinding();
    
    private static final List subPartTypeAnnotations = new ArrayList();
   	static{
   		subPartTypeAnnotations.add(new MutuallyExclusiveAnnotationAnnotationTypeBinding(IsBooleanAnnotationTypeBinding.caseSensitiveName,
																								new String[]{
   																								NumericSeparatorAnnotationTypeBinding.caseSensitiveName,
																								CurrencyAnnotationTypeBinding.caseSensitiveName,
																								SignAnnotationTypeBinding.caseSensitiveName,
																								ZeroFormatAnnotationTypeBinding.caseSensitiveName,
																								ValidValuesAnnotationTypeBinding.caseSensitiveName}));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(NumElementsItemForVGUIRecordItemValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(SelectedIndexItemForVGUIRecordItemValidator.class));
   		subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(UITypeForVGUIRecordItemValidator.class));
   		subPartTypeAnnotations.add(new PrimitiveTypeNotAllowedInPartOfSubtypeValidator(Primitive.BOOLEAN, INSTANCE));
   	}
   	
   	private static final ArrayList aliasAnnotations = new ArrayList();
   	static{
   	    aliasAnnotations.add(new UserDefinedValueValidationRule(AliasForVGUIRecordValueValidator.class));
   	}
   	
   	private static final ArrayList validatorFunctionAnnotations = new ArrayList();
   	static{
   		validatorFunctionAnnotations.add(new UserDefinedValueValidationRule(ValidatorFunctionAnnotationValidator.class));
   	}
   	
   	private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_ALIAS), aliasAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_VALIDATORFUNCTION), validatorFunctionAnnotations);
   	}
	
   	public static IAnnotationTypeBinding getInstance() {
        return INSTANCE;
    }
    
    public boolean isApplicableFor(IBinding binding) {
        return binding.isTypeBinding() && ((ITypeBinding) binding).getKind() == ITypeBinding.FIXED_RECORD_BINDING;
   }

    private Object readResolve() {
        return INSTANCE;
    }

    public List getPartSubTypeAnnotations(){
		return subPartTypeAnnotations;
	}
    
    public List getFieldAnnotations(String field) {
		return (List)fieldAnnotations.get(field);
	}
}
