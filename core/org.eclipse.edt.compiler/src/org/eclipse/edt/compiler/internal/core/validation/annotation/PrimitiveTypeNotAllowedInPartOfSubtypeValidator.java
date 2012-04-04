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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import java.util.Map;

import org.eclipse.edt.compiler.binding.FieldContentValidationAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IAnnotationTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class PrimitiveTypeNotAllowedInPartOfSubtypeValidator extends FieldContentValidationAnnotationTypeBinding {
	
	Primitive invalidType;
	IAnnotationTypeBinding annotationType;
	
	public PrimitiveTypeNotAllowedInPartOfSubtypeValidator(Primitive invalidType, IAnnotationTypeBinding annotationType) {
		super(InternUtil.internCaseSensitive("TypeNotAllowedInPartOfSubtypeValidator"));
		this.annotationType = annotationType;
		this.invalidType = invalidType;
	}

	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		ITypeBinding containerType = containerBinding.getType();
		if(containerType != null && IBinding.NOT_FOUND_BINDING != containerType) {
			if(ITypeBinding.PRIMITIVE_TYPE_BINDING == containerType.getKind() && invalidType == ((PrimitiveTypeBinding) containerType).getPrimitive()) {
				problemRequestor.acceptProblem(
					errorNode,
					IProblemRequestor.PRIMITIVE_TYPE_NOT_ALLOWED_IN_PART_OF_SUBTYPE,
					new String[] {
						invalidType.getName(),
						annotationType.getCaseSensitiveName()
					});
			}
		}
	}
}
