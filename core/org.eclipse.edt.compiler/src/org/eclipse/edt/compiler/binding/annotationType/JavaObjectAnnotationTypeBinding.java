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
import org.eclipse.edt.compiler.binding.PartSubTypeAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedFieldContentAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedInstantiationValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.JavaObjectFieldTypeValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.JavaObjectInstantiationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author Harmon
 */
public class JavaObjectAnnotationTypeBinding extends PartSubTypeAnnotationTypeBinding {
    public static final String name = InternUtil.intern("JavaObject");

    private static JavaObjectAnnotationTypeBinding INSTANCE = new JavaObjectAnnotationTypeBinding();
    
    private static final List subPartTypeAnnotations = new ArrayList();
    static {
    	subPartTypeAnnotations.add(new UserDefinedFieldContentAnnotationValidationRule(JavaObjectFieldTypeValidator.class));
    }
    
    private static final List instantiationValidators = new ArrayList();
    static {
    	instantiationValidators.add(new UserDefinedInstantiationValidationRule(JavaObjectInstantiationValidator.class));
    }
        
    public JavaObjectAnnotationTypeBinding() {
        super(name);
    }
    
    public static JavaObjectAnnotationTypeBinding getInstance() {
        return INSTANCE;
    }

    public boolean isApplicableFor(IBinding binding) {
        return binding.isTypeBinding() && (((ITypeBinding) binding).getKind() == ITypeBinding.INTERFACE_BINDING);
   }

    private Object readResolve() {
        return INSTANCE;
    }
    
    public List getPartSubTypeAnnotations() {
    	return subPartTypeAnnotations;
    }
    
    public List getInstantiationValidators() {
    	return instantiationValidators;
    }
}
