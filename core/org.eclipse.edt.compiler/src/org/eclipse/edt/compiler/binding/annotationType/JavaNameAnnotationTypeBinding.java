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
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class JavaNameAnnotationTypeBinding extends StringValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("javaName");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static JavaNameAnnotationTypeBinding INSTANCE = new JavaNameAnnotationTypeBinding();
	
	private JavaNameAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static JavaNameAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isFunctionBinding()) {
			IFunctionBinding functionBinding = (IFunctionBinding) binding;
			IPartBinding declaringPart = functionBinding.getDeclarer();
			return functionBinding.isAbstract();
		}
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
