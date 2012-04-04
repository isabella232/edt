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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.SignKind;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PropertyApplicableForNumericTypeOnlyAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.SignAnnotationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class SignAnnotationTypeBinding extends EnumerationValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("sign");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static SignAnnotationTypeBinding INSTANCE = new SignAnnotationTypeBinding();
	
	private static final List annotations = new ArrayList();
	static{
		annotations.add(new PropertyApplicableForNumericTypeOnlyAnnotationValidator(INSTANCE,IEGLConstants.PROPERTY_SIGN));
		annotations.add(new SignAnnotationValidator());
	}
	
	
	private SignAnnotationTypeBinding() {
		super(caseSensitiveName, SignKind.TYPE);
	}
	
	public static SignAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return takesUIItemAnnotations(binding) || takesFormattingAnnotations(binding);
	}
	
	public Object getDefaultValue() {
		return SignKind.NONE;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getAnnotations(){
		return annotations;
	}
}
