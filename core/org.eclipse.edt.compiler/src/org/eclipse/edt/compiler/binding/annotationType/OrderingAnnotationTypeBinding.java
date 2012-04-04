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

import org.eclipse.edt.compiler.binding.DictionaryBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.OrderingKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class OrderingAnnotationTypeBinding extends EnumerationValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("ordering");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static OrderingAnnotationTypeBinding INSTANCE = new OrderingAnnotationTypeBinding();
	
	private OrderingAnnotationTypeBinding() {
		super(caseSensitiveName, OrderingKind.TYPE);
	}
	
	public static OrderingAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isDataBinding()) {
			IDataBinding dBinding = (IDataBinding) binding;
			return dBinding.getType() == DictionaryBinding.INSTANCE;
		}
		return false;
	}
	
	public Object getDefaultValue() {
		return OrderingKind.NONE;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
