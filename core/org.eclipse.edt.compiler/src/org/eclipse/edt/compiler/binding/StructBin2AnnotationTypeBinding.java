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

import org.eclipse.edt.compiler.internal.core.validation.annotation.StructBin2Validator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AbstractStructParameterAnnotaionValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class StructBin2AnnotationTypeBinding extends StructParmeterAnnotationTypeBinding {

	private static StructBin2AnnotationTypeBinding INSTANCE = new StructBin2AnnotationTypeBinding();

	public static final String caseSensitiveName = InternUtil.internCaseSensitive("StructBin2");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
   	private static final List validationAnns = new ArrayList();
   	static{
   		validationAnns.add(new UserDefinedAnnotationValidationRule(StructBin2Validator.class));
   	}

   	public AbstractStructParameterAnnotaionValidator getValidator() {
   		return new StructBin2Validator();
   	}
   	
	public StructBin2AnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static StructBin2AnnotationTypeBinding getInstance() {
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
