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


class ThrowNrfEofExceptionsAnnotationTypeBinding extends BooleanValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("throwNrfEofExceptions");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static ThrowNrfEofExceptionsAnnotationTypeBinding INSTANCE = new ThrowNrfEofExceptionsAnnotationTypeBinding();
	
	private ThrowNrfEofExceptionsAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static ThrowNrfEofExceptionsAnnotationTypeBinding getInstance() {
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
	
	private Object readResolve() {
		return INSTANCE;
	}
}
