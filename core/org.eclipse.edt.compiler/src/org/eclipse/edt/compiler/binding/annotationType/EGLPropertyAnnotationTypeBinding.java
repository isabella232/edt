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
package org.eclipse.edt.compiler.binding.annotationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedFieldAccessAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.validation.annotation.EGLPropertyFieldAccessValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.EGLPropertyValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.GetMethodAnnotationValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.SetMethodAnnotationValueValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class EGLPropertyAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String name = InternUtil.intern(IEGLConstants.PROPERTY_EGLPROPERTY);
	
	private static EGLPropertyAnnotationTypeBinding INSTANCE = new EGLPropertyAnnotationTypeBinding();
	
	
   private static final List fieldAccessAnnotations = new ArrayList();
   static {
    	fieldAccessAnnotations.add(new UserDefinedFieldAccessAnnotationValidationRule(EGLPropertyFieldAccessValidator.class));
    }    

	private static final ArrayList getMethodAnnotations = new ArrayList();
   	static{
   		getMethodAnnotations.add(new UserDefinedValueValidationRule(GetMethodAnnotationValueValidator.class));
   	}
	
	private static final List myAnnotations = new ArrayList();
   	static{
   		myAnnotations.add(new UserDefinedAnnotationValidationRule(EGLPropertyValidator.class));
   	}

	private static final ArrayList setMethodAnnotations = new ArrayList();
   	static{
   		setMethodAnnotations.add(new UserDefinedValueValidationRule(SetMethodAnnotationValueValidator.class));
   	}
   	
    private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_GETMETHOD), getMethodAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_SETMETHOD), setMethodAnnotations);
   	}
   	
	private EGLPropertyAnnotationTypeBinding() {
		super(name, new Object[0]);
	}
	
	public static EGLPropertyAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return true;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getFieldAnnotations(String field) {
		return (List) fieldAnnotations.get(field);
	}
	
	public List getAnnotations() {
		return myAnnotations;
	}
	
    public List getFieldAccessAnnotations() {
    	return fieldAccessAnnotations;
    }


}
