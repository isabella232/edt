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

import org.eclipse.edt.compiler.internal.core.validation.annotation.StructZonedDecimalValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AbstractStructParameterAnnotaionValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class StructZonedDecimalAnnotationTypeBinding extends StructParmeterAnnotationTypeBinding {

	private static StructZonedDecimalAnnotationTypeBinding INSTANCE = new StructZonedDecimalAnnotationTypeBinding();

	public static final String caseSensitiveName = InternUtil.internCaseSensitive("StructZonedDecimal");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
   	private static final List validationAnns = new ArrayList();
   	static{
   		validationAnns.add(new UserDefinedAnnotationValidationRule(StructZonedDecimalValidator.class));
   	}

   	public AbstractStructParameterAnnotaionValidator getValidator() {
   		return new StructZonedDecimalValidator();
   	}
   	
	public StructZonedDecimalAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static StructZonedDecimalAnnotationTypeBinding getInstance() {
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
