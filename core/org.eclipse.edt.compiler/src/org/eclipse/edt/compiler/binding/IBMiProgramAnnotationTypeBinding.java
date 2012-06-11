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
package org.eclipse.edt.compiler.binding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IBMiProgramParameterAnnotationsValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IBMiProgramValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

public class IBMiProgramAnnotationTypeBinding extends AnnotationTypeBinding {

	private static IBMiProgramAnnotationTypeBinding INSTANCE = new IBMiProgramAnnotationTypeBinding();

	public static final String caseSensitiveName = InternUtil.internCaseSensitive("IBMiProgram");
	public static final String name = InternUtil.intern(caseSensitiveName);
	public static final String[] pkgName = InternUtil.intern(new String[] {"eglx", "jtopen", "annotations"});
	
   	private static final List validationAnns = new ArrayList();
   	static{
   		validationAnns.add(new UserDefinedAnnotationValidationRule(IBMiProgramValidator.class));
   	}

    private static final ArrayList parameterAnnotationsValidationAnns = new ArrayList();
   	static{
   		parameterAnnotationsValidationAnns.add(new UserDefinedValueValidationRule(IBMiProgramParameterAnnotationsValidator.class));
   	}

   	private static final HashMap fieldValidationAnns = new HashMap();
   	static{
   		fieldValidationAnns.put(InternUtil.intern("parameterAnnotations"), parameterAnnotationsValidationAnns);	  		
   	}
   	


	public IBMiProgramAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static IBMiProgramAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}


	@Override
	public boolean isApplicableFor(IBinding binding) {
		return true;
	}
	
	public List getAnnotations(){
		return validationAnns;
	}
	
	@Override
	public List getFieldAnnotations(String field) {
		return (List)fieldValidationAnns.get(field);
	}

	

}
