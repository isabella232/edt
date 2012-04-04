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
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.FillCharacterValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class FillCharacterAnnotationTypeBinding extends StringValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("fillCharacter");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static FillCharacterAnnotationTypeBinding INSTANCE = new FillCharacterAnnotationTypeBinding();
	
	private static final List annotations = new ArrayList();
	static{
		annotations.add(new UserDefinedAnnotationValidationRule(FillCharacterValidator.class));
	}
	
	private FillCharacterAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static FillCharacterAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return takesUIItemAnnotations(binding) || takesFormattingAnnotations(binding);
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getAnnotations() {
		return annotations;
	}
}
