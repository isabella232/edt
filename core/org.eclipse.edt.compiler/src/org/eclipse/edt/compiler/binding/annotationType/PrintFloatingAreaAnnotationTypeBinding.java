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

import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.DeviceTypeKind;
import org.eclipse.edt.compiler.internal.core.validation.annotation.NonNegativeIntegerAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.TwoElementArrayWhoseElementsAreGreaterThanZeroAnnotationValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class PrintFloatingAreaAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("printFloatingArea");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private PrintFloatingAreaAnnotationTypeBinding() {
		super(caseSensitiveName, new Object[] {
			IEGLConstants.PROPERTY_BOTTOMMARGIN,	PrimitiveTypeBinding.getInstance(Primitive.INT),
			IEGLConstants.PROPERTY_DEVICETYPE,		DeviceTypeKind.TYPE,
			IEGLConstants.PROPERTY_LEFTMARGIN,		PrimitiveTypeBinding.getInstance(Primitive.INT),
			IEGLConstants.PROPERTY_PAGESIZE,		ArrayTypeBinding.getInstance(PrimitiveTypeBinding.getInstance(Primitive.INT)),
			IEGLConstants.PROPERTY_RIGHTMARGIN,		PrimitiveTypeBinding.getInstance(Primitive.INT),
			IEGLConstants.PROPERTY_TOPMARGIN,		PrimitiveTypeBinding.getInstance(Primitive.INT)			
		}, new Object[] {
			IEGLConstants.PROPERTY_BOTTOMMARGIN,	new Integer(3),
			IEGLConstants.PROPERTY_LEFTMARGIN,		new Integer(5),
			IEGLConstants.PROPERTY_RIGHTMARGIN,		new Integer(5),
			IEGLConstants.PROPERTY_TOPMARGIN,		new Integer(3),
			
			IEGLConstants.PROPERTY_DEVICETYPE,		DeviceTypeKind.SINGLEBYTE
		});
	}
	
	private static PrintFloatingAreaAnnotationTypeBinding INSTANCE = new PrintFloatingAreaAnnotationTypeBinding();
	
	private static final ArrayList pageSizeAnnotations = new ArrayList();
   	static{
   		pageSizeAnnotations.add(new TwoElementArrayWhoseElementsAreGreaterThanZeroAnnotationValidator(
   			IEGLConstants.PROPERTY_PAGESIZE, IProblemRequestor.INVALID_SIZE_PROPERTY_VALUE));
   	}
   	
   	private static final ArrayList leftMarginAnnotations = new ArrayList();
   	static{
   		leftMarginAnnotations.add(new NonNegativeIntegerAnnotationValidator(
   			IEGLConstants.PROPERTY_LEFTMARGIN, IProblemRequestor.INVALID_POSITIVE_INTEGER_PROPERTY_VALUE));
   	}
   	
   	private static final ArrayList rightMarginAnnotations = new ArrayList();
   	static{
   		rightMarginAnnotations.add(new NonNegativeIntegerAnnotationValidator(
   			IEGLConstants.PROPERTY_RIGHTMARGIN, IProblemRequestor.INVALID_POSITIVE_INTEGER_PROPERTY_VALUE));   		
   	}
   	
   	private static final ArrayList bottomMarginAnnotations = new ArrayList();
   	static{
   		bottomMarginAnnotations.add(new NonNegativeIntegerAnnotationValidator(
   			IEGLConstants.PROPERTY_BOTTOMMARGIN, IProblemRequestor.INVALID_POSITIVE_INTEGER_PROPERTY_VALUE));
   	}
   	
   	private static final ArrayList topMarginAnnotations = new ArrayList();
   	static{
   		topMarginAnnotations.add(new NonNegativeIntegerAnnotationValidator(
   			IEGLConstants.PROPERTY_TOPMARGIN, IProblemRequestor.INVALID_POSITIVE_INTEGER_PROPERTY_VALUE));
   	}
   	
    private static final HashMap fieldAnnotations = new HashMap();
   	static{
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_PAGESIZE), pageSizeAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_LEFTMARGIN), leftMarginAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_RIGHTMARGIN), rightMarginAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_BOTTOMMARGIN), bottomMarginAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_TOPMARGIN), topMarginAnnotations);
   	}
	
	public static PrintFloatingAreaAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return binding.isTypeBinding() && ((ITypeBinding) binding).getKind() == ITypeBinding.FORMGROUP_BINDING;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getFieldAnnotations(String field) {
		return (List)fieldAnnotations.get(field);
	}
}
