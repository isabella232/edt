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
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.OutlineKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class OutlineAnnotationTypeBinding extends AnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("outline");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static OutlineAnnotationTypeBinding INSTANCE = new OutlineAnnotationTypeBinding();
	
	private OutlineAnnotationTypeBinding() {
		super(caseSensitiveName, ArrayTypeBinding.getInstance(OutlineKind.TYPE));
	}
	
	public static OutlineAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return takesFormFieldAnnotations(binding);
	}
	
	public Object getDefaultValue() {
		return new OutlineKind[0];
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public EnumerationTypeBinding getEnumerationType() {
		return OutlineKind.TYPE;
	}
	
    public boolean supportsElementOverride() {
        return true;
    }
    
    public ITypeBinding getSingleValueType() {
    	return ArrayTypeBinding.getInstance(OutlineKind.TYPE);
    }
}
