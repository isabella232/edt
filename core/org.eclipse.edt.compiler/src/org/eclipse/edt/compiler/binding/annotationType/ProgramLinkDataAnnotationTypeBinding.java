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

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class ProgramLinkDataAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("programLinkData");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static ProgramLinkDataAnnotationTypeBinding INSTANCE = new ProgramLinkDataAnnotationTypeBinding();
	
	private ProgramLinkDataAnnotationTypeBinding() {
		super(caseSensitiveName, new Object[] {
			IEGLConstants.PROPERTY_LINKPARMS,		ArrayTypeBinding.getInstance(LinkParameterAnnotationTypeBinding.getInstance()),
			IEGLConstants.PROPERTY_NEWWINDOW,		PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
			IEGLConstants.PROPERTY_PROGRAMNAME,		PrimitiveTypeBinding.getInstance(Primitive.STRING),
			IEGLConstants.PROPERTY_UIRECORDNAME,	PrimitiveTypeBinding.getInstance(Primitive.STRING)
		});
	}
	
	public static ProgramLinkDataAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isDataBinding()) {
			return true;
		}
		if(binding.isTypeBinding()) {
			return ((ITypeBinding) binding).getKind() == ITypeBinding.DATAITEM_BINDING;
		}
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
