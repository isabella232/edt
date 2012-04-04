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
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class AliasAnnotationTypeBinding extends StringValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("alias");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static AliasAnnotationTypeBinding INSTANCE = new AliasAnnotationTypeBinding();
	
	private AliasAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static AliasAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isTypeBinding()) {
			ITypeBinding typeBinding = (ITypeBinding) binding;
			if(typeBinding.getKind() == ITypeBinding.LIBRARY_BINDING ||
			   typeBinding.getKind() == ITypeBinding.PROGRAM_BINDING ||
			   typeBinding.getKind() == ITypeBinding.FORMGROUP_BINDING ||
			   typeBinding.getKind() == ITypeBinding.FORM_BINDING ||
			   typeBinding.getKind() == ITypeBinding.SERVICE_BINDING ||			   
			   typeBinding.getKind() == ITypeBinding.DATATABLE_BINDING) {
				return true;
			}					
		}
		if(binding.isFunctionBinding()) {
			IPartBinding declaringPart = ((IFunctionBinding) binding).getDeclarer();
			return declaringPart != null && declaringPart.getAnnotation(new String[] {"egl", "core"}, "NativeLibrary") != null;
		}
		return takesUIItemAnnotations(binding);
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
