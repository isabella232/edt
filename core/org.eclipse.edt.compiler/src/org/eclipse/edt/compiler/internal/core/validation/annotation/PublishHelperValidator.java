/*
 * Licensed Materials - Property of IBM
 *
 * Copyright IBM Corporation 2005, 2010. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA DP Schedule Contract with IBM Corp.
 */
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
public class PublishHelperValidator implements IValueValidationRule{
	
	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
		if (annotationBinding.getValue() instanceof FunctionBinding) {
			FunctionBinding function = (FunctionBinding) annotationBinding.getValue();
			if (!isValidPublishHelperFunction(function)) {
				problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.PUBLISHHELPER_FUNCTION_INVALID,
						new String[] {
								function.getName()
						});
			}
		}
		
	}
	
	public boolean isValidPublishHelperFunction(FunctionBinding function) {
		
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
