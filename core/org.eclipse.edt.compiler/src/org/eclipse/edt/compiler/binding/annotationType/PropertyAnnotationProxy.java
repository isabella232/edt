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

import org.eclipse.edt.compiler.binding.AbstractValidationProxy;
import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.binding.FieldAccessValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedFieldAccessAnnotationValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PropertyFieldAccessValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PropertyValidator;
import org.eclipse.edt.mof.utils.NameUtile;


public class PropertyAnnotationProxy extends AbstractValidationProxy {
	public static final String name = NameUtile.getAsName("Property");
	
	private static PropertyAnnotationProxy INSTANCE = new PropertyAnnotationProxy();
	
	private static final List<AnnotationValidationRule> myAnnotations = new ArrayList();
	static {
		myAnnotations.add(new UserDefinedAnnotationValidationRule(PropertyValidator.class));
	}
	
	private static final List<FieldAccessValidationRule> fieldAccessAnnotations = new ArrayList();
	static {
		fieldAccessAnnotations.add(new UserDefinedFieldAccessAnnotationValidationRule(PropertyFieldAccessValidator.class));
	}    
	
	private PropertyAnnotationProxy() {
	}
	
	public static PropertyAnnotationProxy getInstance() {
		return INSTANCE;
	}
	
	@Override
	public List<AnnotationValidationRule> getAnnotationValidators() {
		return myAnnotations;
	}
	
	@Override
	public List<FieldAccessValidationRule> getFieldAccessValidators() {
		return fieldAccessAnnotations;
	}
}
