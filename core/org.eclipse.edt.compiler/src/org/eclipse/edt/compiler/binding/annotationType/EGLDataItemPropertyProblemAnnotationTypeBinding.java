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
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class EGLDataItemPropertyProblemAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("eglDataItemPropertyProblem");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	public static final String QUALIFIED_DATAITEM_NAME = InternUtil.intern("qualifiedDataItemName");
	public static final String ANNOTATION_NAME = InternUtil.intern("annotationName");
	public static final String ERROR_MSG = InternUtil.intern("errorMsg");
	
	private static EGLDataItemPropertyProblemAnnotationTypeBinding INSTANCE = new EGLDataItemPropertyProblemAnnotationTypeBinding();
	
	private EGLDataItemPropertyProblemAnnotationTypeBinding() {
		super(caseSensitiveName,
			new Object[] {
				QUALIFIED_DATAITEM_NAME,	PrimitiveTypeBinding.getInstance(Primitive.STRING),
				ANNOTATION_NAME,			PrimitiveTypeBinding.getInstance(Primitive.STRING),
				ERROR_MSG,					PrimitiveTypeBinding.getInstance(Primitive.STRING)
		});
	}
	
	public static EGLDataItemPropertyProblemAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return true; 
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
}
