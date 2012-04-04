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

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.internal.core.validation.annotation.RedefinesAnnotationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class RedefinesAnnotationTypeBinding extends ResolvableSimpleNameAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("redefines");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static RedefinesAnnotationTypeBinding INSTANCE = new RedefinesAnnotationTypeBinding();
	private static final List annotations = new ArrayList();
	static{
		annotations.add(new UserDefinedAnnotationValidationRule(RedefinesAnnotationValidator.class));
	}
	private RedefinesAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static RedefinesAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isDataBinding()) {
		    
			ITypeBinding tBinding = ((IDataBinding) binding).getType();
			if (tBinding == null || tBinding == IBinding.NOT_FOUND_BINDING) {
			    return false;
			}
			
			if(tBinding.getKind() == ITypeBinding.ARRAY_TYPE_BINDING) {
				tBinding = ((ArrayTypeBinding) tBinding).getBaseType();
			}
			return tBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING ||
			       tBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING;
		}
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getAnnotations(){
		return annotations;
	}
}
