/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.jtopen.validation.annotation.proxy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.AbstractValidationProxy;
import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.mof.eglx.jtopen.validation.annotation.StructPackedDecimalValidator;


public class StructPackedDecimalProxy extends AbstractValidationProxy {
	private static StructPackedDecimalProxy INSTANCE = new StructPackedDecimalProxy();
	
	private static final List<AnnotationValidationRule> myAnnotations = new ArrayList<AnnotationValidationRule>();
	static{
		myAnnotations.add(new UserDefinedAnnotationValidationRule(StructPackedDecimalValidator.class));
	}

	@Override
	public List<AnnotationValidationRule> getAnnotationValidators() {
		return myAnnotations;
	}

	private StructPackedDecimalProxy() {
	}
	
	public static StructPackedDecimalProxy getInstance() {
		return INSTANCE;
	}
}
