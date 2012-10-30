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
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.binding.ValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.utils.NameUtile;
import org.eclipse.edt.rui.validation.annotation.OnConstructionFunctionAnnotationValueValidator;

public class RUIHandlerAnnotationProxy extends AbstractValidationProxy {
	private static RUIHandlerAnnotationProxy INSTANCE = new RUIHandlerAnnotationProxy();
	
	private static final List<ValueValidationRule> onConstructionFunctionAnnotations = new ArrayList();
   	static{
   		onConstructionFunctionAnnotations.add(new UserDefinedValueValidationRule(OnConstructionFunctionAnnotationValueValidator.class));
   	}
	
	private static final Map<String, List<ValueValidationRule>> fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(NameUtile.getAsName(IEGLConstants.PROPERTY_ONCONSTRUCTIONFUNCTION), onConstructionFunctionAnnotations);
   	}
	
	private RUIHandlerAnnotationProxy() {
	}
	
	public static RUIHandlerAnnotationProxy getInstance() {
		return INSTANCE;
	}
	
	@Override
	public List<ValueValidationRule> getFieldValidators(String field) {
		return fieldAnnotations.get(field);
	}
}
