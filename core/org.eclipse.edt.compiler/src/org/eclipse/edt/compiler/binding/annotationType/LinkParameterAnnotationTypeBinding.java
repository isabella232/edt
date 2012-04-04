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
import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValueForLinkParameterValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class LinkParameterAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("linkParameter");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static LinkParameterAnnotationTypeBinding INSTANCE = new LinkParameterAnnotationTypeBinding();
	
   	private static final List valueAnnotations = new ArrayList();
   	static{
   		valueAnnotations.add(new UserDefinedValueValidationRule(ValueForLinkParameterValidator.class));
   	}
   	
   	private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_VALUE), valueAnnotations);
   	}
	
	private LinkParameterAnnotationTypeBinding() {
		super(caseSensitiveName, new Object[] {
			IEGLConstants.PROPERTY_NAME,		PrimitiveTypeBinding.getInstance(Primitive.STRING),
			IEGLConstants.PROPERTY_VALUE,		PrimitiveTypeBinding.getInstance(Primitive.ANY),
			IEGLConstants.PROPERTY_VALUEREF,	SystemPartManager.INTERNALREF_BINDING,
		});
	}
	
	public static LinkParameterAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return binding.isAnnotationBinding() &&
	       ProgramLinkDataAnnotationTypeBinding.getInstance() == ((IAnnotationBinding) binding).getEnclosingAnnotationType() &&
	       InternUtil.intern("LinkParms") == binding.getName();
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getFieldAnnotations(String field) {
		return (List) fieldAnnotations.get(field);
	}
}
