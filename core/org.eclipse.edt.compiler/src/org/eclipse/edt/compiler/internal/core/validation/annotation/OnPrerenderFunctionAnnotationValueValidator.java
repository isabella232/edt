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

import org.eclipse.edt.compiler.binding.FunctionBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


public class OnPrerenderFunctionAnnotationValueValidator implements IValueValidationRule {

	public void validate(Node errorNode, Node target, IAnnotationBinding annotationBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if (annotationBinding.getValue() != null && annotationBinding.getValue() != IBinding.NOT_FOUND_BINDING) {
			if (annotationBinding.getValue() instanceof FunctionBinding){
				FunctionBinding value = (FunctionBinding)annotationBinding.getValue();
				IPartBinding valueDeclarer = value.getDeclarer();
				if(valueDeclarer != null && annotationBinding.getDeclaringPart() != valueDeclarer) {
					problemRequestor.acceptProblem(
						errorNode,
						IProblemRequestor.LIBRARY_FUNCTION_NOT_ALLOWED_FOR_PROPERTY,
						new String[] {
							IEGLConstants.PROPERTY_ONPRERENDERFUNCTION,
							annotationBinding.getDeclaringPart().getCaseSensitiveName()
						});
				}
			}	
		}
	}
}
