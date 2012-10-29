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
package org.eclipse.edt.rui.validation.annotation.proxy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.AbstractValidationProxy;
import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.binding.ValueValidationRule;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.edt.rui.validation.annotation.DateFormatAnnotationValidator;
import org.eclipse.edt.rui.validation.annotation.DateFormatAnnotationValueValidator;


public class DateFormatAnnotationProxy extends AbstractValidationProxy {
	public static final String caseSensitiveName = NameUtile.getAsCaseSensitiveName("dateFormat");
	public static final String name = NameUtile.getAsName(caseSensitiveName);
	
	private static DateFormatAnnotationProxy INSTANCE = new DateFormatAnnotationProxy();
	
	private static final List<AnnotationValidationRule> annotations = new ArrayList();
	static{
		annotations.add(new DateFormatAnnotationValidator());
	}
	
	private static final List<ValueValidationRule> valueAnnotations = new ArrayList();
	
	static{
		valueAnnotations.add(new UserDefinedValueValidationRule(DateFormatAnnotationValueValidator.class));
	}
	
	private DateFormatAnnotationProxy() {
	}
	
	public static DateFormatAnnotationProxy getInstance() {
		return INSTANCE;
	}
	
	@Override
	public List<AnnotationValidationRule> getAnnotationValidators(){
		return annotations;
	}
	
	@Override
	public List<ValueValidationRule> getFieldValidators(String field) {
		// There's only one field defined in the annotation type, no need to check the field name.
		return valueAnnotations;
	}
}
