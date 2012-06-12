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

import org.eclipse.edt.compiler.internal.core.validation.annotation.StructUnsignedBin8Validator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AbstractStructParameterAnnotaionValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class StructUnsignedBin8AnnotationTypeBinding extends StructParmeterAnnotationTypeBinding {

	private static StructUnsignedBin8AnnotationTypeBinding INSTANCE = new StructUnsignedBin8AnnotationTypeBinding();

	public static final String caseSensitiveName = InternUtil.internCaseSensitive("StructUnsignedBin8");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
   	private static final List validationAnns = new ArrayList();
   	static{
   		validationAnns.add(new UserDefinedAnnotationValidationRule(StructUnsignedBin8Validator.class));
   	}

   	public AbstractStructParameterAnnotaionValidator getValidator() {
   		return new StructUnsignedBin8Validator();
   	}
   	
	public StructUnsignedBin8AnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static StructUnsignedBin8AnnotationTypeBinding getInstance() {
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
