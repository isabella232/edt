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
package org.eclipse.edt.mof.eglx.rui.validation.annotation.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.edt.compiler.binding.AbstractValidationProxy;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.binding.ValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.mof.eglx.rui.validation.annotation.PublishHelperValidator;
import org.eclipse.edt.mof.eglx.rui.validation.annotation.PublishMessageHelperValidator;
import org.eclipse.edt.mof.eglx.rui.validation.annotation.RetrieveValidStateHelperValidator;
import org.eclipse.edt.mof.eglx.rui.validation.annotation.RetrieveViewHelperValidator;
import org.eclipse.edt.mof.utils.NameUtile;

public class MVCViewAnnotationProxy extends AbstractValidationProxy {
	public static final String caseSensitiveName = NameUtile.getAsCaseSensitiveName("mvcView");
	public static final String name = NameUtile.getAsName(caseSensitiveName);
	
	private static MVCViewAnnotationProxy INSTANCE = new MVCViewAnnotationProxy();
		
	private MVCViewAnnotationProxy() {
	}
	
	public static MVCViewAnnotationProxy getInstance() {
		return INSTANCE;
	}
		
    private static final List<ValueValidationRule> publishHelperAnnotations = new ArrayList();
   	static{
   		publishHelperAnnotations.add(new UserDefinedValueValidationRule(PublishHelperValidator.class));
   	}

    private static final List<ValueValidationRule> retrieveViewHelperAnnotations = new ArrayList();
   	static{
   		retrieveViewHelperAnnotations.add(new UserDefinedValueValidationRule(RetrieveViewHelperValidator.class));
   	}

    private static final List<ValueValidationRule> retrieveValidStateHelperAnnotations = new ArrayList();
   	static{
   		retrieveValidStateHelperAnnotations.add(new UserDefinedValueValidationRule(RetrieveValidStateHelperValidator.class));
   	}

   	private static final List<ValueValidationRule> publishMessageHelperAnnotations = new ArrayList();
   	static{
   		publishMessageHelperAnnotations.add(new UserDefinedValueValidationRule(PublishMessageHelperValidator.class));
   	}
   	
   	
   	private static final Map<String, List<ValueValidationRule>> fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(NameUtile.getAsName(IEGLConstants.PROPERTY_PUBLISHHELPER), publishHelperAnnotations);
   		fieldAnnotations.put(NameUtile.getAsName(IEGLConstants.PROPERTY_RETRIEVEVIEWHELPER), retrieveViewHelperAnnotations);
   		fieldAnnotations.put(NameUtile.getAsName(IEGLConstants.PROPERTY_RETRIEVEVALIDSTATEHELPER), retrieveValidStateHelperAnnotations);
   		fieldAnnotations.put(NameUtile.getAsName(IEGLConstants.PROPERTY_PUBLISHMESSAGEHELPER), publishMessageHelperAnnotations);
   	}
   	
   	@Override
    public List<ValueValidationRule> getFieldValidators(String member){
		return fieldAnnotations.get(member);
	}
}
