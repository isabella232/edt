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

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.RequiredFieldInComplexPropertyAnnotationTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.mof.egl.utils.InternUtil;


class RelationshipAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("relationship");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static RelationshipAnnotationTypeBinding INSTANCE = new RelationshipAnnotationTypeBinding();
	
	private static final List relationshipAnnotations = new ArrayList();
   	static{
   		relationshipAnnotations.add(new RequiredFieldInComplexPropertyAnnotationTypeBinding(
   			new String[]{IEGLConstants.PROPERTY_SEGMENTRECORD},
   			caseSensitiveName));
   	}
   	
	private RelationshipAnnotationTypeBinding() {
		super(caseSensitiveName, new Object[] {
			IEGLConstants.PROPERTY_PARENTRECORD,	SystemPartManager.INTERNALREF_BINDING,
			IEGLConstants.PROPERTY_SEGMENTRECORD,	SystemPartManager.INTERNALREF_BINDING
		});
	}
	
	public static RelationshipAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return binding.isAnnotationBinding() &&
	       PCBAnnotationTypeBinding.getInstance() == ((IAnnotationBinding) binding).getEnclosingAnnotationType() &&
	       InternUtil.intern("Hierarchy") == binding.getName();
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public List getAnnotations() {
		return relationshipAnnotations;
	}
}
