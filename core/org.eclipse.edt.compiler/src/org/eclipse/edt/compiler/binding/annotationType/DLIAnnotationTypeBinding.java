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
import java.util.List;

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.RequiredFieldInComplexPropertyAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.core.Boolean;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.DLICallInterfaceKind;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PCBParmsValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class DLIAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("dli");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static DLIAnnotationTypeBinding INSTANCE = new DLIAnnotationTypeBinding();
	
	private static final List dliAnnotations = new ArrayList();
   	static{
   		dliAnnotations.add(new RequiredFieldInComplexPropertyAnnotationTypeBinding(
   			new String[]{IEGLConstants.PROPERTY_PSB},
   			caseSensitiveName));
   		dliAnnotations.add(new UserDefinedAnnotationValidationRule(PCBParmsValidator.class));
   	}
	
	private DLIAnnotationTypeBinding() {
		super(caseSensitiveName, new Object[] {
			IEGLConstants.PROPERTY_CALLINTERFACE,		DLICallInterfaceKind.TYPE,
			IEGLConstants.PROPERTY_HANDLEHARDDLIERRORS,	PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
			IEGLConstants.PROPERTY_PCBPARMS,			ArrayTypeBinding.getInstance(SystemPartManager.INTERNALREF_BINDING),
			IEGLConstants.PROPERTY_PSB,					SystemPartManager.INTERNALREF_BINDING,
			IEGLConstants.PROPERTY_PSBPARM,				SystemPartManager.INTERNALREF_BINDING
		}, new Object[] {
			IEGLConstants.PROPERTY_CALLINTERFACE,		DLICallInterfaceKind.AIBTDLI,
			IEGLConstants.PROPERTY_HANDLEHARDDLIERRORS,	Boolean.YES
		}
		);
	}
	
	public static DLIAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isTypeBinding()) {
			int kind = ((ITypeBinding) binding).getKind();
			return kind == ITypeBinding.PROGRAM_BINDING || kind == ITypeBinding.LIBRARY_BINDING;
		}
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getAnnotations() {
		return dliAnnotations;
	}
}
