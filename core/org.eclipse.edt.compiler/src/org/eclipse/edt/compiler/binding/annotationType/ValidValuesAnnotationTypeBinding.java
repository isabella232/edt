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

import org.eclipse.edt.compiler.binding.AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.IValidValuesElement;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValidValuesAnnotationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class ValidValuesAnnotationTypeBinding extends AnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("validValues");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static ValidValuesAnnotationTypeBinding INSTANCE = new ValidValuesAnnotationTypeBinding();
	
	private static final List annotations = new ArrayList();
	static{
		annotations.add(new UserDefinedAnnotationValidationRule(ValidValuesAnnotationValidator.class));
	}
	
	private ValidValuesAnnotationTypeBinding() {
		super(caseSensitiveName, ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.ANY)));
	}
	
	public static ValidValuesAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return takesUIItemAnnotations(binding) || takesValidationAnnotations(binding) ||
		       takesConsoleFieldAnnotations(binding) || takesPageItemAnnotations(binding);
	}
	
	public Object getDefaultValue() {
		return new IValidValuesElement[0];
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getAnnotations(){
		return annotations;
	}
	
	public ITypeBinding getSingleValueType() {
		return ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.ANY));
	}
}
