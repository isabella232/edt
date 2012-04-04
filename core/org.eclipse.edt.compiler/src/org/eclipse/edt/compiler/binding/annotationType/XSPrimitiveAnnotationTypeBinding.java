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


class XSPrimitiveAnnotationTypeBinding extends StringValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("xsPrimitive");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static XSPrimitiveAnnotationTypeBinding INSTANCE = new XSPrimitiveAnnotationTypeBinding();
	
	private XSPrimitiveAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static XSPrimitiveAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isDataBinding()) {
			return true;
		}
		if(binding.isTypeBinding()) {
			ITypeBinding tBinding = (ITypeBinding) binding;
			if(tBinding.getKind() == ITypeBinding.DATAITEM_BINDING) {
				return true;
			}
		}
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
