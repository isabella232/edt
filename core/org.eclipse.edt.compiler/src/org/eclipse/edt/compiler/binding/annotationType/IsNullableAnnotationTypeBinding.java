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
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class IsNullableAnnotationTypeBinding extends BooleanValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("isNullable");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static IsNullableAnnotationTypeBinding INSTANCE = new IsNullableAnnotationTypeBinding();
	
	private IsNullableAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static IsNullableAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isDataBinding()) {
			IDataBinding dBinding = (IDataBinding) binding;
			if(IDataBinding.LOCAL_VARIABLE_BINDING == dBinding.getKind() ||
			   IDataBinding.CLASS_FIELD_BINDING == dBinding.getKind()) {
				return true;
			}
		}
		return takesSQLItemAnnotations(binding);
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
