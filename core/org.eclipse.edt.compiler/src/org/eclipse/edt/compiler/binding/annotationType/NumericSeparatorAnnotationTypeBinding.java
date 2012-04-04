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
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.validation.annotation.BooleanPropertyApplicableForNumericTypeOnlyAnnotationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class NumericSeparatorAnnotationTypeBinding extends BooleanValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("numericSeparator");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static NumericSeparatorAnnotationTypeBinding INSTANCE = new NumericSeparatorAnnotationTypeBinding();
	
	private static final List annotations = new ArrayList();
	static{
		annotations.add(new BooleanPropertyApplicableForNumericTypeOnlyAnnotationValidator(INSTANCE, IEGLConstants.PROPERTY_NUMERICSEPARATOR));
	}
	
	private NumericSeparatorAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static NumericSeparatorAnnotationTypeBinding getInstance() {
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
}
