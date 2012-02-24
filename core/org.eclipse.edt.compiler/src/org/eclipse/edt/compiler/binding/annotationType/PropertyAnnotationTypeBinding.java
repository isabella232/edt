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
import java.util.List;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.UserDefinedFieldAccessAnnotationValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PropertyFieldAccessValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class PropertyAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String name = InternUtil.intern("Property");
	
	private static PropertyAnnotationTypeBinding INSTANCE = new PropertyAnnotationTypeBinding();
	
	
   private static final List fieldAccessAnnotations = new ArrayList();
   static {
    	fieldAccessAnnotations.add(new UserDefinedFieldAccessAnnotationValidationRule(PropertyFieldAccessValidator.class));
    }    
   	   	
	private PropertyAnnotationTypeBinding() {
		super(name, new Object[0]);
	}
	
	public static PropertyAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return true;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}

	public List getFieldAccessAnnotations() {
    	return fieldAccessAnnotations;
    }


}
