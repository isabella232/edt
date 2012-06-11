/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.binding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.internal.core.validation.annotation.AS400Bin4Validator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AbstractAS400ParameterAnnotaionValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class AS400Bin4AnnotationTypeBinding extends AS400ParmeterAnnotationTypeBinding {

	private static AS400Bin4AnnotationTypeBinding INSTANCE = new AS400Bin4AnnotationTypeBinding();

	public static final String caseSensitiveName = InternUtil.internCaseSensitive("AS400Bin4");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
   	private static final List validationAnns = new ArrayList();
   	static{
   		validationAnns.add(new UserDefinedAnnotationValidationRule(AS400Bin4Validator.class));
   	}

   	public AbstractAS400ParameterAnnotaionValidator getValidator() {
   		return new AS400Bin4Validator();
   	}
   	
	public AS400Bin4AnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static AS400Bin4AnnotationTypeBinding getInstance() {
		return INSTANCE;
	}


	@Override
	public boolean isApplicableFor(IBinding binding) {
		return true;
	}
	
	public List getAnnotations(){
		return validationAnns;
	}

	

}
