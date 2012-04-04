/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.FunctionParameterBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

/**
 * @author svihovec
 *
 */
public class PublishMessageHelperValidator implements IValueValidationRule{
	
	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
		if (annotationBinding.getValue() instanceof FunctionBinding) {
			FunctionBinding function = (FunctionBinding) annotationBinding.getValue();
			if (!isValidPublishMessageHelperFunction(function)) {
				problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.PUBLISHMESSAGEHELPER_FUNCTION_INVALID,
						new String[] {
								function.getName()
						});
			}
		}
		
	}
	
	public boolean isValidPublishMessageHelperFunction(FunctionBinding function) {
		
		if (function.getReturnType() != null) {
			return false;
		}
		
		if (function.getParameters().size() != 1) {
			return false;
		}
		
		FunctionParameterBinding parm = (FunctionParameterBinding)function.getParameters().get(0);
		
		
		if (parm.getType() != getStringType()) {
			return false;
		}
		
		if (parm.isOutput() || parm.isInputOutput()) {
			return false;
		}
		
		return true;
	}
	
	private PrimitiveTypeBinding getStringType() {
		return PrimitiveTypeBinding.getInstance(Primitive.STRING);
	}
	
}
