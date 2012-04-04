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
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class EGLLocationAnnotationTypeBinding extends IntegerArrayValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("eglLocation");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static EGLLocationAnnotationTypeBinding INSTANCE = new EGLLocationAnnotationTypeBinding();
	
	private EGLLocationAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static EGLLocationAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return false; 
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
}
