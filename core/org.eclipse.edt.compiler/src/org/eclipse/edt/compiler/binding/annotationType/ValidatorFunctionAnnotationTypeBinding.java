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
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValidatorFunctionAnnotationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class ValidatorFunctionAnnotationTypeBinding extends ResolvableNameAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("validatorFunction");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static ValidatorFunctionAnnotationTypeBinding INSTANCE = new ValidatorFunctionAnnotationTypeBinding();
	
	private static final List valueAnnotations = new ArrayList();
	static{
		valueAnnotations.add(new UserDefinedValueValidationRule(ValidatorFunctionAnnotationValidator.class));
	}
	
	private ValidatorFunctionAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static ValidatorFunctionAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isTypeBinding()) {
			ITypeBinding typeBinding = (ITypeBinding) binding;
			return typeBinding.getKind() == ITypeBinding.DATAITEM_BINDING ||
			       typeBinding.getKind() == ITypeBinding.FORM_BINDING;
		}
		return takesUIItemAnnotations(binding) ||
		       takesValidationAnnotations(binding) ||
			   takesPageItemAnnotations(binding);
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getValueAnnotations() {
		return valueAnnotations;
	}
}
