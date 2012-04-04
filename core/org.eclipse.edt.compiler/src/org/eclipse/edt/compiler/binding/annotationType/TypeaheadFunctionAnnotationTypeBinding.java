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
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationNotValidForArrayAnnotationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class TypeaheadFunctionAnnotationTypeBinding extends ResolvableNameAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("typeaheadFunction");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static TypeaheadFunctionAnnotationTypeBinding INSTANCE = new TypeaheadFunctionAnnotationTypeBinding();
	
	private static final List annotations = new ArrayList();
	static{
//		annotations.add(new AnnotationNotValidForArrayAnnotationValidator(caseSensitiveName));
	}
	
	private TypeaheadFunctionAnnotationTypeBinding() {
		super(name);
	}
	
	public static TypeaheadFunctionAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return true;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getAnnotations() {
		return annotations;
	}
}
