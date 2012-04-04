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
import org.eclipse.edt.compiler.binding.UsedTypeBinding;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class PFKeyEquateAnnotationTypeBinding extends BooleanValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("pfKeyEquate");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static PFKeyEquateAnnotationTypeBinding INSTANCE = new PFKeyEquateAnnotationTypeBinding();
	
	private PFKeyEquateAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static PFKeyEquateAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isTypeBinding()) {
			int typeBindingKind = ((ITypeBinding) binding).getKind();
			return typeBindingKind == ITypeBinding.FORM_BINDING ||
			       typeBindingKind == ITypeBinding.FORMGROUP_BINDING;
		}
		if((binding.isUsedTypeBinding() && ((UsedTypeBinding) binding).getType().getKind() == ITypeBinding.FORMGROUP_BINDING)){
			return true;
		}
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
