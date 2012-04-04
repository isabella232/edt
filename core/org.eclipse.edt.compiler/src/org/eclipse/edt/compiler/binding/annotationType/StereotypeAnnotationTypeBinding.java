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

import org.eclipse.edt.compiler.binding.AnnotationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.ArrayTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class StereotypeAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("stereotype");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static StereotypeAnnotationTypeBinding INSTANCE = new StereotypeAnnotationTypeBinding();
	
	private StereotypeAnnotationTypeBinding() {
		super(caseSensitiveName, new Object[] {
			"Associations",			ArrayTypeBinding.getInstance(ArrayTypeBinding.getInstance(SystemPartManager.RECORDREF_BINDING)),			
			"MemberAnnotations",	ArrayTypeBinding.getInstance(SystemPartManager.RECORDREF_BINDING),
			"MutualExclusions",		ArrayTypeBinding.getInstance(ArrayTypeBinding.getInstance(SystemPartManager.RECORDREF_BINDING)),		
			"ReferenceType",		PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN),
			"StereotypeContexts",	ArrayTypeBinding.getInstance(SystemPartManager.RECORDREF_BINDING),			
			"PartType",				SystemPartManager.TYPEREF_BINDING,			
			"DefaultSuperType",		SystemPartManager.TYPEREF_BINDING			
		});
	}
	
	public static StereotypeAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		if(binding.isAnnotationBinding()) {
			return AnnotationAnnotationTypeBinding.getInstance() == ((IAnnotationBinding) binding).getType();
		}
		return false;
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
	
	public boolean isSystemAnnotation() {
		return true;
	}
}
