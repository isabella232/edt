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
import java.util.HashMap;
import java.util.List;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PublishHelperValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PublishMessageHelperValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.RetrieveValidStateHelperValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.RetrieveViewHelperValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;

class MVCViewAnnotationTypeBinding extends BooleanValueAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("mvcView");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static MVCViewAnnotationTypeBinding INSTANCE = new MVCViewAnnotationTypeBinding();
		
	private MVCViewAnnotationTypeBinding() {
		super(caseSensitiveName);
	}
	
	public static MVCViewAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
		
	private Object readResolve() {
		return INSTANCE;
	}
		
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isDataBinding()) {
		    return true;
		}
		return false;
	}
	
    private static final ArrayList publishHelperAnnotations = new ArrayList();
   	static{
   		publishHelperAnnotations.add(new UserDefinedValueValidationRule(PublishHelperValidator.class));
   	}

    private static final ArrayList retrieveViewHelperAnnotations = new ArrayList();
   	static{
   		retrieveViewHelperAnnotations.add(new UserDefinedValueValidationRule(RetrieveViewHelperValidator.class));
   	}

    private static final ArrayList retrieveValidStateHelperAnnotations = new ArrayList();
   	static{
   		retrieveValidStateHelperAnnotations.add(new UserDefinedValueValidationRule(RetrieveValidStateHelperValidator.class));
   	}

   	private static final ArrayList publishMessageHelperAnnotations = new ArrayList();
   	static{
   		publishMessageHelperAnnotations.add(new UserDefinedValueValidationRule(PublishMessageHelperValidator.class));
   	}
   	
   	
   	private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_PUBLISHHELPER), publishHelperAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_RETRIEVEVIEWHELPER), retrieveViewHelperAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_RETRIEVEVALIDSTATEHELPER), retrieveValidStateHelperAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_PUBLISHMESSAGEHELPER), publishMessageHelperAnnotations);
   	}

    public List getFieldAnnotations(String member){
		return (List)fieldAnnotations.get(member);
	}
}
