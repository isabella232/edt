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
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.AlignKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class AlignAnnotationTypeBinding extends EnumerationValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("align");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static AlignAnnotationTypeBinding INSTANCE = new AlignAnnotationTypeBinding();
	
	private AlignAnnotationTypeBinding() {
		super(caseSensitiveName, AlignKind.TYPE);
	}
	
	public static AlignAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return takesFormattingAnnotations(binding);
	}
	
	public Object getDefaultValue() {
		return AlignKind.LEFT;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
