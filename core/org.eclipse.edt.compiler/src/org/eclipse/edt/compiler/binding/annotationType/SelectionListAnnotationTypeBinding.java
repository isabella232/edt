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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.validation.annotation.LabelItemAnnotationValueValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.ValueItemAnnotationValueValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class SelectionListAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String name = InternUtil.intern("selectionList");
	
	private static SelectionListAnnotationTypeBinding INSTANCE = new SelectionListAnnotationTypeBinding();
	
	private static final ArrayList labelItemAnnotations = new ArrayList();
   	static{
   		labelItemAnnotations.add(new UserDefinedValueValidationRule(LabelItemAnnotationValueValidator.class));
   	}
	
	private static final ArrayList valueItemAnnotations = new ArrayList();
   	static{
   		valueItemAnnotations.add(new UserDefinedValueValidationRule(ValueItemAnnotationValueValidator.class));
   	}
   	
    private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_LABELITEM), labelItemAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_VALUEITEM), valueItemAnnotations);
   	}
   	
	private SelectionListAnnotationTypeBinding() {
		super(name, new Object[0]);
	}
	
	public static SelectionListAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return true;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getFieldAnnotations(String field) {
		return (List) fieldAnnotations.get(field);
	}
}
