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
import org.eclipse.edt.compiler.binding.IntegerGreaterThanOneValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PropertyApplicableForSpecificPrimitiveOnlyAnnotationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class MaxLenAnnotationTypeBinding extends IntegerValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("maxLen");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static MaxLenAnnotationTypeBinding INSTANCE = new MaxLenAnnotationTypeBinding();
	
	private static final List valueAnnotations = new ArrayList();
	
	static{
		valueAnnotations.add(IntegerGreaterThanOneValueValidationRule.INSTANCE);		
	}
	
	private static final List annotations = new ArrayList();
	static {
		annotations.add(new PropertyApplicableForSpecificPrimitiveOnlyAnnotationValidator(
			INSTANCE, IEGLConstants.PROPERTY_MAXLEN, Primitive.STRING
		));
	}
	
	private MaxLenAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static MaxLenAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return takesSQLItemAnnotations(binding);
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getValueAnnotations() {
		return valueAnnotations;
	}
	
	public List getAnnotations() {
		return annotations;
	}
}
