/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.ParameterKind;


public class JavaObjectFieldTypeValidator extends DefaultFieldContentAnnotationValidationRule {
	
	@Override
	public void validate(Node errorNode, Node container, Member containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
	}
	
	@Override
	public void validateFunctionParameter(FunctionParameter fParameter, Member dataBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		org.eclipse.edt.mof.egl.Type parameterTypeBinding = dataBinding.getType();
		if (parameterTypeBinding != null && dataBinding instanceof org.eclipse.edt.mof.egl.FunctionParameter) {
			org.eclipse.edt.mof.egl.FunctionParameter parameterBinding = (org.eclipse.edt.mof.egl.FunctionParameter) dataBinding;

			if (parameterBinding.getParameterKind() != ParameterKind.PARM_IN) {
				problemRequestor.acceptProblem(
					fParameter,
					IProblemRequestor.IN_MODIFIER_REQUIRED_FOR_JAVAOBJECT_FUNCTION_PARAMETERS,
					new String[] {dataBinding.getCaseSensitiveName()});
			}
		}			
	}
	
	@Override
	public void validateFunctionReturnType(Type typeNode, org.eclipse.edt.mof.egl.Type typeBinding, Member memberBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
	}
}
