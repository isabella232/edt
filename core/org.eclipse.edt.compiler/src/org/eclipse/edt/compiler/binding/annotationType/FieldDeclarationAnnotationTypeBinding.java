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
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemPartManager;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public class FieldDeclarationAnnotationTypeBinding extends ComplexAnnotationTypeBinding {
	public static final String caseSensitiveName = InternUtil.internCaseSensitive("fieldDeclaration");
	public static final String name = InternUtil.intern(caseSensitiveName);
	
	private static FieldDeclarationAnnotationTypeBinding INSTANCE = new FieldDeclarationAnnotationTypeBinding();
	
	private FieldDeclarationAnnotationTypeBinding() {
		super(caseSensitiveName, new Object[] {
			"FieldName",	PrimitiveTypeBinding.getInstance(Primitive.STRING),
			"FieldType",	SystemPartManager.TYPEREF_BINDING,
			"IsPrivate",	PrimitiveTypeBinding.getInstance(Primitive.BOOLEAN)
		});
	}
	
	public static FieldDeclarationAnnotationTypeBinding getInstance() {
		return INSTANCE;
	}
	
	public boolean isApplicableFor(IBinding binding) {
		return binding.isAnnotationBinding() &&
		       AnnotationAnnotationTypeBinding.getInstance() == ((IAnnotationBinding) binding).getEnclosingAnnotationType() &&
		       InternUtil.intern("ImplicitFields") == binding.getName();
	}
	
	private Object readResolve() {
		return INSTANCE;
	}
}
