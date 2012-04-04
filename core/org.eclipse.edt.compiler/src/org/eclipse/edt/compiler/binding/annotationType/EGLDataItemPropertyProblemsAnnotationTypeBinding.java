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

import org.eclipse.edt.compiler.binding.AnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class EGLDataItemPropertyProblemsAnnotationTypeBinding extends AnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("eglDataItemPropertyProblems");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static EGLDataItemPropertyProblemsAnnotationTypeBinding INSTANCE = new EGLDataItemPropertyProblemsAnnotationTypeBinding();
	
	private EGLDataItemPropertyProblemsAnnotationTypeBinding() {
		super(caseSensitiveName, ArrayTypeBinding.getInstance(EGLDataItemPropertyProblemAnnotationTypeBinding.getInstance()));
	}
	
	public static EGLDataItemPropertyProblemsAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return true; 
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
