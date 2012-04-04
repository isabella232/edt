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
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.ElementTypeKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class ElementTypeAnnotationTypeBinding extends EnumerationValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("elementtype");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static ElementTypeAnnotationTypeBinding INSTANCE = new ElementTypeAnnotationTypeBinding();
	
	private ElementTypeAnnotationTypeBinding() {
		super(caseSensitiveName, ElementTypeKind.TYPE);
	}
	
	public static ElementTypeAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
//		if(binding.isTypeBinding()) {
//			ITypeBinding typeBinding = (ITypeBinding) binding;
//			if(typeBinding.getKind() == ITypeBinding.LIBRARY_BINDING ||
//			   typeBinding.getKind() == ITypeBinding.PROGRAM_BINDING ||
//			   typeBinding.getKind() == ITypeBinding.FORMGROUP_BINDING ||
//			   typeBinding.getKind() == ITypeBinding.FORM_BINDING ||
//			   typeBinding.getKind() == ITypeBinding.SERVICE_BINDING ||			   
//			   typeBinding.getKind() == ITypeBinding.DATATABLE_BINDING) {
//				return true;
//			}					
//		}
		if(binding.isFunctionBinding()) {
			IPartBinding declaringPart = ((IFunctionBinding) binding).getDeclarer();
			return declaringPart != null && declaringPart.getAnnotation(new String[] {"egl", "report","birt"}, "BirtHandler") != null;
		}
		return false;
	}
	
	
	private Object readResolve() {
		return INSTANCE;
	}
}
