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
package org.eclipse.edt.compiler.binding.annotationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AbstractValidationProxy;
import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.binding.FieldAccessValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedFieldAccessAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.binding.ValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.validation.annotation.EGLPropertyFieldAccessValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.EGLPropertyValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.GetMethodAnnotationValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.SetMethodAnnotationValueValidator;
import org.eclipse.edt.mof.utils.NameUtile;


public class EGLPropertyAnnotationProxy extends AbstractValidationProxy {
	public static final String name = NameUtile.getAsName(IEGLConstants.PROPERTY_EGLPROPERTY);
	
	private static EGLPropertyAnnotationProxy INSTANCE = new EGLPropertyAnnotationProxy();
	
	
	private static final List<FieldAccessValidationRule> fieldAccessAnnotations = new ArrayList();
	static {
		fieldAccessAnnotations.add(new UserDefinedFieldAccessAnnotationValidationRule(EGLPropertyFieldAccessValidator.class));
	}
	
	private static final ArrayList getMethodAnnotations = new ArrayList();
	static{
		getMethodAnnotations.add(new UserDefinedValueValidationRule(GetMethodAnnotationValueValidator.class));
	}
	
	private static final List<AnnotationValidationRule> myAnnotations = new ArrayList();
	static{
		myAnnotations.add(new UserDefinedAnnotationValidationRule(EGLPropertyValidator.class));
	}

	private static final ArrayList setMethodAnnotations = new ArrayList();
	static{
		setMethodAnnotations.add(new UserDefinedValueValidationRule(SetMethodAnnotationValueValidator.class));
	}
   	
    private static final Map<String, List<ValueValidationRule>> fieldAnnotations = new HashMap();
	static{
		fieldAnnotations.put(NameUtile.getAsName(IEGLConstants.PROPERTY_GETMETHOD), getMethodAnnotations);
		fieldAnnotations.put(NameUtile.getAsName(IEGLConstants.PROPERTY_SETMETHOD), setMethodAnnotations);
	}
   	
	private EGLPropertyAnnotationProxy() {
	}
	
	public static EGLPropertyAnnotationProxy getInstance() {
		return INSTANCE;
	}
	
	@Override
	public List<ValueValidationRule> getFieldValidators(String field) {
		return fieldAnnotations.get(field);
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
