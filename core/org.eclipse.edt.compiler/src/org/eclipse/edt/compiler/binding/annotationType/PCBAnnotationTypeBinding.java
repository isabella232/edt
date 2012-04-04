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
import org.eclipse.edt.compiler.binding.FieldAssociationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.binding.UserDefinedAnnotationValidationRule;
import org.eclipse.edt.compiler.binding.UserDefinedValueValidationRule;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.PCBKind;
import org.eclipse.edt.compiler.internal.core.validation.annotation.HierarchyValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.MustBeDLINameAnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.PCBValidator;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class PCBAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("pcb");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private PCBAnnotationTypeBinding() {
		super(caseSensitiveName, new Object[] {
			IEGLConstants.PROPERTY_HIERARCHY,			ArrayTypeBinding.getInstance(RelationshipAnnotationTypeBinding.getInstance()),
			IEGLConstants.PROPERTY_PCBNAME,				PrimitiveTypeBinding.getInstance(Primitive.STRING),
			IEGLConstants.PROPERTY_PCBTYPE,				PCBKind.TYPE,
			IEGLConstants.PROPERTY_SECONDARYINDEX,		PrimitiveTypeBinding.getInstance(Primitive.STRING),
			IEGLConstants.PROPERTY_SECONDARYINDEXITEM,	PrimitiveTypeBinding.getInstance(Primitive.STRING)
		});
	}
	
	private static PCBAnnotationTypeBinding INSTANCE = new PCBAnnotationTypeBinding();
	
	private static final List pcbAnnotations = new ArrayList();
   	static{
   		pcbAnnotations.add(new FieldAssociationAnnotationTypeBinding(
   			IEGLConstants.PROPERTY_SECONDARYINDEXITEM,
   			IEGLConstants.PROPERTY_SECONDARYINDEX));
   		pcbAnnotations.add(new UserDefinedAnnotationValidationRule(PCBValidator.class));
   	}
   	
   	private static final List hierarchyAnnotations = new ArrayList();
   	static{
   		hierarchyAnnotations.add(new UserDefinedValueValidationRule(HierarchyValidator.class));
   	}
   	
   	private static final ArrayList pcbNameAnnotations = new ArrayList();
   	static{
   		pcbNameAnnotations.add(MustBeDLINameAnnotationValidator.INSTANCE);
   	}
   	
   	private static final ArrayList secondaryIndexAnnotations = new ArrayList();
   	static{
   		secondaryIndexAnnotations.add(MustBeDLINameAnnotationValidator.INSTANCE);
   	}
   	
   	private static final HashMap fieldAnnotations = new HashMap();
   	static{   		
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_HIERARCHY), hierarchyAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_PCBNAME), pcbNameAnnotations);
   		fieldAnnotations.put(InternUtil.intern(IEGLConstants.PROPERTY_SECONDARYINDEX), secondaryIndexAnnotations);
   	}
   	
	public static PCBAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {		
		if(!binding.isDataBinding()) {
			return false;
		}
		IDataBinding dataBinding = (IDataBinding) binding;
		
		if (dataBinding.getKind() != IDataBinding.FLEXIBLE_RECORD_FIELD) {
		    return false;
		}
		
		FlexibleRecordFieldBinding field = (FlexibleRecordFieldBinding) dataBinding;
		
		return field.getDeclaringPart().getAnnotation(new String[] {"egl", "dli"}, "PSBRecord") != null;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}

	public List getAnnotations() {
		return pcbAnnotations;
	}

	public List getFieldAnnotations(String field) {
		return (List)fieldAnnotations.get(field);
	}
}
