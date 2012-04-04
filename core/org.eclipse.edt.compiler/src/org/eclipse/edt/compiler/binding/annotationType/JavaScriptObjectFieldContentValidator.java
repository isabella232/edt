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

import java.util.Map;

import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.DefaultFieldContentAnnotationValidationRule;

public class JavaScriptObjectFieldContentValidator extends DefaultFieldContentAnnotationValidationRule {
	
	public void validate(Node errorNode, Node container, IDataBinding containerBinding, String canonicalContainerName, Map allAnnotations, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
	}
	
	public void validateFunctionParameter(FunctionParameter fParameter, IDataBinding dataBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		ITypeBinding parameterTypeBinding = dataBinding.getType();
		if(Binding.isValidBinding(parameterTypeBinding) && dataBinding.getKind() == IDataBinding.FUNCTION_PARAMETER_BINDING) {
			FunctionParameterBinding parameterBinding = (FunctionParameterBinding) dataBinding;

			if(!parameterBinding.isInput()) {
				problemRequestor.acceptProblem(
						fParameter,
						IProblemRequestor.IN_MODIFIER_REQUIRED_FOR_JAVAOBJECT_FUNCTION_PARAMETERS,
						new String[] {dataBinding.getCaseSensitiveName()});
				}
		}			
	}
	
	public void validateFunctionReturnType(Type typeNode, ITypeBinding typeBinding, IPartBinding declaringPart, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
	}
	
	
}
