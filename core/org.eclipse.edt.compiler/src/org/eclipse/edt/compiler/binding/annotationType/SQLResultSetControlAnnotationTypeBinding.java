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
package org.eclipse.edt.compiler.binding.annotationType;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.internal.egl2mof.eglx.persistence.sql.validation.SQLResultSetControlValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class SQLResultSetControlAnnotationTypeBinding extends ComplexAnnotationTypeBinding{
	public static final String name = InternUtil.intern("SQLResultSetControl");
	
	private static SQLResultSetControlAnnotationTypeBinding INSTANCE = new SQLResultSetControlAnnotationTypeBinding();
		
	private static final List myAnnotations = new ArrayList();
   	static{
   		myAnnotations.add(new UserDefinedAnnotationValidationRule(SQLResultSetControlValidator.class));
   	}
   	
	private SQLResultSetControlAnnotationTypeBinding() {
		super(name, new Object[0]);
	}
	
	public static SQLResultSetControlAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
        return binding.isTypeBinding() && ((ITypeBinding) binding).getKind() == ITypeBinding.EXTERNALTYPE_BINDING;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getAnnotations() {
		return myAnnotations;
	}
}
