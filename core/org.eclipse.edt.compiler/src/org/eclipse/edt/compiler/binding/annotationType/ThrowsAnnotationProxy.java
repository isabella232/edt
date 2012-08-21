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

import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.binding.InvocationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedInvocationValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ThrowsInvocationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ThrowsValidator;
import org.eclipse.edt.mof.utils.NameUtile;


public class ThrowsAnnotationProxy extends BooleanValueAnnotationProxy {
	public static final String caseSensitiveName = NameUtile.getAsName("throws");
	public static final String name = NameUtile.getAsName(caseSensitiveName);
	
	private static ThrowsAnnotationProxy INSTANCE = new ThrowsAnnotationProxy();
	
	private ThrowsAnnotationProxy() {
		super(caseSensitiveName);
	}
	
	public static ThrowsAnnotationProxy getInstance() {
		return INSTANCE;
	}
	
    private static final List<InvocationValidationRule> invocationValidators = new ArrayList();
    static {
    	invocationValidators.add(new UserDefinedInvocationValidationRule(ThrowsInvocationValidator.class));
    }
	
	private static final List<AnnotationValidationRule> myAnnotations = new ArrayList();
   	static{
   		myAnnotations.add(new UserDefinedAnnotationValidationRule(ThrowsValidator.class));
   	}

	@Override
	public List<InvocationValidationRule> getInvocationValidators() {
		return invocationValidators;
	}
	
	@Override
	public List<AnnotationValidationRule> getAnnotationValidators() {
		return myAnnotations;
	}

}
