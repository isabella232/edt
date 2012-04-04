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
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class HandleHardIOErrorsAnnotationTypeBinding extends BooleanValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("handleHardIOErrors");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static HandleHardIOErrorsAnnotationTypeBinding INSTANCE = new HandleHardIOErrorsAnnotationTypeBinding();
	
	private HandleHardIOErrorsAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static HandleHardIOErrorsAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if (binding.isTypeBinding()) {
            ITypeBinding typeBinding = (ITypeBinding) binding;
            return typeBinding.getKind() == ITypeBinding.PROGRAM_BINDING ||
				   typeBinding.getKind() == ITypeBinding.SERVICE_BINDING ||
				   typeBinding.getKind() == ITypeBinding.HANDLER_BINDING ||
				   typeBinding.getKind() == ITypeBinding.LIBRARY_BINDING; 
        }
		return false;
	}
	
	public Object getDefaultValue() {
		return Boolean.YES;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
