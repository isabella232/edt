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
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.UITypeKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class UITypeAnnotationTypeBinding extends EnumerationValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("uiType");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static UITypeAnnotationTypeBinding INSTANCE = new UITypeAnnotationTypeBinding();
	
	private UITypeAnnotationTypeBinding() {
		super(caseSensitiveName, UITypeKind.TYPE);
	}
	
	public static UITypeAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return takesFillerStructureItemAnnotations(binding) ||
		       takesUIItemAnnotations(binding);
	}
	
	public Object getDefaultValue() {
		return UITypeKind.OUTPUT;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
