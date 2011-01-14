/*******************************************************************************
 * Copyright © 2008, 2010 IBM Corporation and others.
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
import org.eclipse.edt.compiler.internal.core.utils.InternUtil;
import org.eclipse.edt.compiler.internal.core.validation.annotation.MinimumInputAnnotationValueValidator;


class MinimumInputAnnotationTypeBinding extends IntegerValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("minimumInput");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static MinimumInputAnnotationTypeBinding INSTANCE = new MinimumInputAnnotationTypeBinding();
	
	private static final List valueAnnotations = new ArrayList();
	
	static{
		valueAnnotations.add(new UserDefinedValueValidationRule(MinimumInputAnnotationValueValidator.class));
	}
	
	private MinimumInputAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static MinimumInputAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return takesUIItemAnnotations(binding) || takesValidationAnnotations(binding);
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getValueAnnotations() {
		return valueAnnotations;
	}
}
