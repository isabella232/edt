/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.rui.validation.annotation.proxy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.AbstractValidationProxy;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.binding.ValueValidationRule;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.edt.rui.validation.annotation.ValidatorFunctionAnnotationValidator;


public class ValidatorFunctionAnnotationProxy extends AbstractValidationProxy {
	public static final String caseSensitiveName = NameUtile.getAsCaseSensitiveName("validatorFunction");
	public static final String name = NameUtile.getAsName(caseSensitiveName);
	
	private static ValidatorFunctionAnnotationProxy INSTANCE = new ValidatorFunctionAnnotationProxy();
	
	private static final List<ValueValidationRule> valueAnnotations = new ArrayList();
	static{
		valueAnnotations.add(new UserDefinedValueValidationRule(ValidatorFunctionAnnotationValidator.class));
	}
	
	private ValidatorFunctionAnnotationProxy() {
	}
	
	public static ValidatorFunctionAnnotationProxy getInstance() {
		return INSTANCE;
	}
	
	@Override
	public List<ValueValidationRule> getFieldValidators(String field) {
		// There's only one field defined in the annotation type, no need to check the field name.
		return valueAnnotations;
	}
}
