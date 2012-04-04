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
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.PrimitiveTypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Primitive;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;

/**
 * @author svihovec
 *
 */
public class RetrieveValidStateHelperValidator implements IValueValidationRule{
	
	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
		if (annotationBinding.getValue() instanceof FunctionBinding) {
			FunctionBinding function = (FunctionBinding) annotationBinding.getValue();
			if (!isValidRetrieveValidStateHelperFunction(function)) {
				problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.RETRIEVEVALIDSTATEHELPER_FUNCTION_INVALID,
						new String[] {
								function.getName()
						});
			}
		}
		
	}
	
	public boolean isValidRetrieveValidStateHelperFunction(FunctionBinding function) {
		
		if (function.getParameters().size() != 0) {
			return false;
		}

		if (function.getReturnType() == null) {
			return false;
		}
				
		
		if (function.getReturnType() != getNullableStringType()) {
			return false;
		}
				
		return true;
	}
	
	private ITypeBinding getNullableStringType() {
		return PrimitiveTypeBinding.getInstance(Primitive.STRING).getNullableInstance();
	}
	
}
