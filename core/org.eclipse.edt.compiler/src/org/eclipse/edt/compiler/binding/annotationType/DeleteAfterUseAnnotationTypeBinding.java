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


class DeleteAfterUseAnnotationTypeBinding extends BooleanValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("deleteAfterUse");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static DeleteAfterUseAnnotationTypeBinding INSTANCE = new DeleteAfterUseAnnotationTypeBinding();
	
	private DeleteAfterUseAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static DeleteAfterUseAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return (binding.isUsedTypeBinding() && ((UsedTypeBinding) binding).getType().getKind() == ITypeBinding.DATATABLE_BINDING);
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
