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
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class PersistentAnnotationTypeBinding extends BooleanValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("persistent");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static PersistentAnnotationTypeBinding INSTANCE = new PersistentAnnotationTypeBinding();
	
	private PersistentAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static PersistentAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return takesSQLItemAnnotations(binding);
	}
	
	public Object getDefaultValue() {
		return Boolean.YES;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
