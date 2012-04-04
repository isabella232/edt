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

import org.eclipse.edt.compiler.binding.AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.UsedTypeBinding;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.PFKeyKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class ValidationBypassKeysAnnotationTypeBinding extends AnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("validationBypassKeys");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static ValidationBypassKeysAnnotationTypeBinding INSTANCE = new ValidationBypassKeysAnnotationTypeBinding();
	
	private ValidationBypassKeysAnnotationTypeBinding() {
		super(caseSensitiveName, ArrayTypeBinding.getInstance(PFKeyKind.TYPE));
	}
	
	public static ValidationBypassKeysAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isTypeBinding()) {
			int typeBindingKind = ((ITypeBinding) binding).getKind();
			return typeBindingKind == ITypeBinding.FORM_BINDING ||
			       typeBindingKind == ITypeBinding.FORMGROUP_BINDING;
		}
		if(binding.isUsedTypeBinding()) {
			return ((UsedTypeBinding) binding).getType().getKind() == ITypeBinding.FORMGROUP_BINDING;
		}
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public EnumerationTypeBinding getEnumerationType() {
		return PFKeyKind.TYPE;
	}
	
	public ITypeBinding getSingleValueType() {
		return ArrayTypeBinding.getInstance(PFKeyKind.TYPE);
	}
}
