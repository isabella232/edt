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
package org.eclipse.edt.mof.eglx.rui.validation.annotation.proxy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.AbstractValidationProxy;
import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PropertyApplicableForNumericTypeOnlyAnnotationValidator;
import org.eclipse.edt.mof.eglx.rui.validation.annotation.SignAnnotationValidator;
import org.eclipse.edt.mof.utils.NameUtile;


public class SignAnnotationProxy extends AbstractValidationProxy {
	public static final String caseSensitiveName = NameUtile.getAsCaseSensitiveName("sign");
	public static final String name = NameUtile.getAsName(caseSensitiveName);
	
	private static SignAnnotationProxy INSTANCE = new SignAnnotationProxy();
	
	private static final List<AnnotationValidationRule> annotations = new ArrayList();
	static{
		annotations.add(new PropertyApplicableForNumericTypeOnlyAnnotationValidator(IEGLConstants.PROPERTY_SIGN));
		annotations.add(new SignAnnotationValidator());
	}
	
	private SignAnnotationProxy() {
	}
	
	public static SignAnnotationProxy getInstance() {
		return INSTANCE;
	}
	
	@Override
	public List<AnnotationValidationRule> getAnnotationValidators(){
		return annotations;
	}
}