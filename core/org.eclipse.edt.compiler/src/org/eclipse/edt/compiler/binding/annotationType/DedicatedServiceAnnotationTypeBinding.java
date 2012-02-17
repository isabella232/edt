/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.DedicatedServiceValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class DedicatedServiceAnnotationTypeBinding extends IntegerValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("dedicatedService");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static DedicatedServiceAnnotationTypeBinding INSTANCE = new DedicatedServiceAnnotationTypeBinding();
	
	private static final List myAnnotations = new ArrayList();
   	static{
   		myAnnotations.add(new UserDefinedAnnotationValidationRule(DedicatedServiceValidator.class));
   	}
	
	private DedicatedServiceAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static DedicatedServiceAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if (binding.isDataBinding()) {
			ITypeBinding type = ((IDataBinding)binding).getType();
			return (Binding.isValidBinding(type) && type.getKind() == ITypeBinding.SERVICE_BINDING);
		}
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getAnnotations() {
		return myAnnotations;
	}
	
}
