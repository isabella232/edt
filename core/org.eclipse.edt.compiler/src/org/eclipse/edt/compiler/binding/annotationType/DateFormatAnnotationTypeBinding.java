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
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.DateFormatAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.DateFormatAnnotationValueValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class DateFormatAnnotationTypeBinding extends StringValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("dateFormat");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static DateFormatAnnotationTypeBinding INSTANCE = new DateFormatAnnotationTypeBinding();
	
	private static final List annotations = new ArrayList();
	static{
		annotations.add(new DateFormatAnnotationValidator());
	}
	
	private static final List valueAnnotations = new ArrayList();
	
	static{
		valueAnnotations.add(new UserDefinedValueValidationRule(DateFormatAnnotationValueValidator.class));
	}
	
	private DateFormatAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static DateFormatAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return takesUIItemAnnotations(binding) || takesFormattingAnnotations(binding);
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getAnnotations(){
		return annotations;
	}
	
	public List getValueAnnotations() {
		return valueAnnotations;
	}
}
