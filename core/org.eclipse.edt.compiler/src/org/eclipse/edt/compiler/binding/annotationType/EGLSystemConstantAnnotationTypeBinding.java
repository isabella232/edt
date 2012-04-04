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


public class EGLSystemConstantAnnotationTypeBinding extends IntegerValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("eglSystemConstant");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static EGLSystemConstantAnnotationTypeBinding INSTANCE = new EGLSystemConstantAnnotationTypeBinding();
	
	private EGLSystemConstantAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static EGLSystemConstantAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return binding.isFunctionBinding() || binding.isDataBinding() || (binding.isTypeBinding() && ((ITypeBinding)binding).getKind() == ITypeBinding.ENUMERATION_BINDING);
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
