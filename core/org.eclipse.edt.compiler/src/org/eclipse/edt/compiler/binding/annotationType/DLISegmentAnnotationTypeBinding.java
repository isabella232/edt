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
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.validation.annotation.KeyItemForDLISegmentValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.MustBeDLINameAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.MustBeDLINameForDLISegmentAnnotationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
class DLISegmentAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String caseSensitiveName = InternUtil.internCaseSensitive("DLISegment");
    public static final String name = InternUtil.intern(caseSensitiveName);
    
    public DLISegmentAnnotationTypeBinding() {
        super(caseSensitiveName, new Object[] {
        	IEGLConstants.PROPERTY_HOSTVARQUALIFIER,	PrimitiveTypeBinding.getInstance(Primitive.STRING),
        	IEGLConstants.PROPERTY_KEYITEM,				SystemPartManager.INTERNALREF_BINDING,
        	IEGLConstants.PROPERTY_LENGTHITEM,			SystemPartManager.INTERNALREF_BINDING,
        	IEGLConstants.PROPERTY_SEGMENTNAME,			PrimitiveTypeBinding.getInstance(Primitive.STRING),
        });
    }

    private static DLISegmentAnnotationTypeBinding INSTANCE = new DLISegmentAnnotationTypeBinding();
    
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
   		subPartTypeAnnotations.add(new MustBeDLINameForDLISegmentAnnotationValidator(DLIFieldNameAnnotationTypeBinding.getInstance()));
   	}
   	
   	private static final ArrayList keyItemAnnotations = new ArrayList();
   	static{
   		keyItemAnnotations.add(new UserDefinedValueValidationRule(KeyItemForDLISegmentValidator.class));
   	}
   	
   	private static final ArrayList segmentNameAnnotations = new ArrayList();
   	static{
   		segmentNameAnnotations.add(MustBeDLINameAnnotationValidator.INSTANCE);
   	}
   	
   	private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_KEYITEM), keyItemAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_SEGMENTNAME), segmentNameAnnotations);
   	}
   	
    public static DLISegmentAnnotationTypeBinding getInstance() {
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
    
    public List getFieldAnnotations(String member){
		return (List)fieldAnnotations.get(member);
	}
    
	public static List getSubPartTypeAnnotations() {
		return subPartTypeAnnotations;
	}
}
