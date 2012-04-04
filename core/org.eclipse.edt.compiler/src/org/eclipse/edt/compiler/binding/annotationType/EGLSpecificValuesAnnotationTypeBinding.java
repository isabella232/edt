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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class EGLSpecificValuesAnnotationTypeBinding extends StringArrayValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("eglSpecificValues");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static EGLSpecificValuesAnnotationTypeBinding INSTANCE = new EGLSpecificValuesAnnotationTypeBinding();
	
	private EGLSpecificValuesAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static EGLSpecificValuesAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return binding.isTypeBinding() && ITypeBinding.FLEXIBLE_RECORD_BINDING == ((ITypeBinding) binding).getKind(); 
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
