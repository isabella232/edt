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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.EglRestRpcValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class EglRestRpcAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String name = InternUtil.intern("EglRestRpc");
	
	private static EglRestRpcAnnotationTypeBinding INSTANCE = new EglRestRpcAnnotationTypeBinding();
		
	private static final List myAnnotations = new ArrayList();
   	static{
   		myAnnotations.add(new UserDefinedAnnotationValidationRule(EglRestRpcValidator.class));
   	}
   	
	private EglRestRpcAnnotationTypeBinding() {
		super(name, new Object[0]);
	}
	
	public static EglRestRpcAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return true;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getAnnotations() {
		return myAnnotations;
	}

}
