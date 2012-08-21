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

import org.eclipse.edt.compiler.binding.AnnotationValidationRule;
import org.eclipse.edt.compiler.binding.EnumerationDataBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Enumerations.SignKind;
import org.eclipse.edt.mof.egl.utils.InternUtil;



/**
 * @author Dave Murray
 */
public class SignAnnotationValidator extends AnnotationValidationRule {
	
	public SignAnnotationValidator() {
		super(InternUtil.internCaseSensitive("sign"));
	}
	
	public void validate(final Node errorNode, Node target, ITypeBinding targetTypeBinding, Map allAnnotations, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		final IAnnotationBinding annotationBinding = (IAnnotationBinding)allAnnotations.get(InternUtil.intern(IEGLConstants.PROPERTY_SIGN));
		EnumerationDataBinding value = (EnumerationDataBinding) annotationBinding.getValue();
		if (value.getName() == InternUtil.intern("trailing")) {
			if (targetTypeBinding.getKind() == ITypeBinding.PRIMITIVE_TYPE_BINDING) {
				Primitive prim = ((PrimitiveTypeBinding)targetTypeBinding).getPrimitive();
				if(prim == Primitive.SMALLFLOAT || prim == Primitive.FLOAT) {
					problemRequestor.acceptProblem(errorNode,
							IProblemRequestor.INVALID_PROPERTY_VALUE_FOR_ITEM_TYPE,
							new String[] 
							{SignKind.TRAILING.getCaseSensitiveName(),
							IEGLConstants.PROPERTY_SIGN,
							targetTypeBinding.getCaseSensitiveName()});
				}
			}
		}

	}


}
