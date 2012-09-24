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
package org.eclipse.edt.rui.validation.annotation.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AbstractValidationProxy;
import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.binding.ValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.edt.rui.validation.annotation.PropertiesFileAnnotationValueValidator;
import org.eclipse.edt.rui.validation.annotation.RUIPropertiesLibraryValidator;

public class RUIPropertiesLibraryAnnotationProxy extends AbstractValidationProxy {
	
	private static RUIPropertiesLibraryAnnotationProxy INSTANCE = new RUIPropertiesLibraryAnnotationProxy();
	
	private static final List<AnnotationValidationRule> annotationRules = new ArrayList();
   	static{
   		annotationRules.add(new UserDefinedAnnotationValidationRule(RUIPropertiesLibraryValidator.class));
   	}
   	
   	private static final List<ValueValidationRule> propertiesFileAnnotations = new ArrayList();
   	static{
   		propertiesFileAnnotations.add(new UserDefinedValueValidationRule(PropertiesFileAnnotationValueValidator.class));
   	}
   	
   	private static final Map<String, List<ValueValidationRule>> fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(NameUtile.getAsName(IEGLConstants.PROPERTY_PROPERTIESFILE), propertiesFileAnnotations);
   	}
	
	private RUIPropertiesLibraryAnnotationProxy() {
	}
	
	public static RUIPropertiesLibraryAnnotationProxy getInstance() {
		return INSTANCE;
	}
	
	@Override
	public List<AnnotationValidationRule> getAnnotationValidators() {
		return annotationRules;
	}
	
	@Override
	public List<ValueValidationRule> getFieldValidators(String field) {
		return fieldAnnotations.get(field);
	}
}
