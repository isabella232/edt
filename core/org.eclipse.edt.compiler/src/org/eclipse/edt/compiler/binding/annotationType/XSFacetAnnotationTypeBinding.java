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
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.WhitespaceKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class XSFacetAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("xsFacet");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static XSFacetAnnotationTypeBinding INSTANCE = new XSFacetAnnotationTypeBinding();
	
	private XSFacetAnnotationTypeBinding() {
		super(caseSensitiveName, new Object[] {
				IEGLConstants.PROPERTY_MINLENGTH,		PrimitiveTypeBinding.getInstance(Primitive.STRING),
				IEGLConstants.PROPERTY_MAXLENGTH,		PrimitiveTypeBinding.getInstance(Primitive.STRING),
				IEGLConstants.PROPERTY_PATTERN,			ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.STRING)),
				IEGLConstants.PROPERTY_ENUM,			ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.STRING)),
				IEGLConstants.PROPERTY_WHITESPACE,		WhitespaceKind.TYPE,
				IEGLConstants.PROPERTY_MAXINCLUSIVE,	PrimitiveTypeBinding.getInstance(Primitive.STRING),		
				IEGLConstants.PROPERTY_MININCLUSIVE,	PrimitiveTypeBinding.getInstance(Primitive.STRING),
				IEGLConstants.PROPERTY_MAXEXCLUSIVE,	PrimitiveTypeBinding.getInstance(Primitive.STRING),
				IEGLConstants.PROPERTY_MINEXCLUSIVE,	PrimitiveTypeBinding.getInstance(Primitive.STRING),
		});
	}
	
	public static XSFacetAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isDataBinding()) {
			return true;
		}
		if(binding.isTypeBinding()) {
			ITypeBinding tBinding = (ITypeBinding) binding;
			if(tBinding.getKind() == ITypeBinding.DATAITEM_BINDING) {
				return true;
			}
		}
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
