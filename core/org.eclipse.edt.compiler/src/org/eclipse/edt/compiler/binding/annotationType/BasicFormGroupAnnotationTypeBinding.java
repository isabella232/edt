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
import org.eclipse.edt.compiler.binding.PartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AliasForProgramValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class BasicFormGroupAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String name = InternUtil.intern("BasicFormGroup");

    private static BasicFormGroupAnnotationTypeBinding INSTANCE = new BasicFormGroupAnnotationTypeBinding();
    
    private static final ArrayList aliasAnnotations = new ArrayList();
   	static{
   		aliasAnnotations.add(new UserDefinedValueValidationRule(AliasForProgramValidator.class));
   	}
   	
    private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_ALIAS), aliasAnnotations);
   	}
    
    public BasicFormGroupAnnotationTypeBinding() {
        super(name);
    }
    
    public static BasicFormGroupAnnotationTypeBinding getInstance() {
        return INSTANCE;
    }
    
    public boolean isApplicableFor(IBinding binding) {
        return binding.isTypeBinding() && (((ITypeBinding) binding).getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING || ((ITypeBinding) binding).getKind() == ITypeBinding.FIXED_RECORD_BINDING);
   }

    private Object readResolve() {
        return INSTANCE;
    }
    
	public List getFieldAnnotations(String field) {
		return (List)fieldAnnotations.get(field);
	}
}
