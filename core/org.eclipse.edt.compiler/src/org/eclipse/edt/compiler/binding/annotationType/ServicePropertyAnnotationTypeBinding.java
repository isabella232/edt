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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class ServicePropertyAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("serviceProperty");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static ServicePropertyAnnotationTypeBinding INSTANCE = new ServicePropertyAnnotationTypeBinding();
	
	private ServicePropertyAnnotationTypeBinding() {
		super(caseSensitiveName, new Object[] {
			IEGLConstants.PROPERTY_NAME,		PrimitiveTypeBinding.getInstance(Primitive.STRING),
			IEGLConstants.PROPERTY_REQUIRED,	PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
		});
	}
	
	public static ServicePropertyAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isDataBinding()) {
			return true;
		}
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
