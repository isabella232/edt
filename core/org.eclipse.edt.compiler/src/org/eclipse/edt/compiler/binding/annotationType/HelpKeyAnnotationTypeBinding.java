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
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.PFKeyKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class HelpKeyAnnotationTypeBinding extends EnumerationValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("helpKey");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static HelpKeyAnnotationTypeBinding INSTANCE = new HelpKeyAnnotationTypeBinding();
	
	private HelpKeyAnnotationTypeBinding() {
		super(caseSensitiveName, PFKeyKind.TYPE);
	}
	
	public static HelpKeyAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isTypeBinding()) {
			ITypeBinding typeBinding = (ITypeBinding) binding; 
			return typeBinding.getKind() == ITypeBinding.FORM_BINDING ||
			       typeBinding.getKind() == ITypeBinding.FORMGROUP_BINDING;
		}
		if(binding.isUsedTypeBinding()) {
			return ((UsedTypeBinding) binding).getType().getKind() == ITypeBinding.FORMGROUP_BINDING;
		}
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
