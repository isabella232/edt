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
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class XMLAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("xml");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static XMLAnnotationTypeBinding INSTANCE = new XMLAnnotationTypeBinding();
	
	private XMLAnnotationTypeBinding() {
		super(caseSensitiveName, new Object[] {
			IEGLConstants.PROPERTY_NAME,					PrimitiveTypeBinding.getInstance(Primitive.STRING),
			IEGLConstants.PROPERTY_NAMESPACE,				PrimitiveTypeBinding.getInstance(Primitive.STRING)
		});
	}
	
	public static XMLAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isFunctionBinding()) {
			return true;
		}

		if(binding.isDataBinding()) {
			return true;
		}
		if(binding.isTypeBinding()) {
			ITypeBinding tBinding = (ITypeBinding) binding;
			if(tBinding.getKind() == ITypeBinding.FIXED_RECORD_BINDING ||
			   tBinding.getKind() == ITypeBinding.FLEXIBLE_RECORD_BINDING ||
			   tBinding.getKind() == ITypeBinding.DATAITEM_BINDING ||
			   tBinding.getKind() == ITypeBinding.SERVICE_BINDING ||
			   tBinding.getKind() == ITypeBinding.INTERFACE_BINDING) {
				return true;
			}
		}
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
