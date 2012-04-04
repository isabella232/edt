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


class PositionAnnotationTypeBinding extends IntegerArrayValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("position");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static PositionAnnotationTypeBinding INSTANCE = new PositionAnnotationTypeBinding();
	
	private PositionAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static PositionAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isTypeBinding()) {
			return ((ITypeBinding) binding).getKind() == ITypeBinding.FORM_BINDING;
		}
		return takesFormFieldAnnotations(binding) || takesConsoleFieldAnnotations(binding);
	}
	
	public Object getDefaultValue() {
		return new Integer[] {new Integer(1), new Integer(1)};
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
    public boolean supportsElementOverride() {
        return true;
    }

}
