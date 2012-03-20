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
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedInvocationValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ThrowsInvocationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ThrowsValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class ThrowsAnnotationTypeBinding extends BooleanValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("throws");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static ThrowsAnnotationTypeBinding INSTANCE = new ThrowsAnnotationTypeBinding();
	
	private ThrowsAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static ThrowsAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
    private static final List invocationValidators = new ArrayList();
    static {
    	invocationValidators.add(new UserDefinedInvocationValidationRule(ThrowsInvocationValidator.class));
    }
	
	private static final List myAnnotations = new ArrayList();
   	static{
   		myAnnotations.add(new UserDefinedAnnotationValidationRule(ThrowsValidator.class));
   	}

	
	public boolean isApplicableFor(IBinding binding) {
		return true;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	@Override
	public List getInvocationValidators() {
		return invocationValidators;
	}
	
	public List getAnnotations() {
		return myAnnotations;
	}

}
