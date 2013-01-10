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
package org.eclipse.edt.rui.validation.annotation;

import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IValueValidationRule;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.rui.messages.RUIResourceKeys;


public class ValidatorFunctionAnnotationValidator implements IValueValidationRule{
	
	@Override
	public void validate(final Node annotation, Node container, Annotation annotationBinding, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
		if (!(annotationBinding.getValue() instanceof FunctionMember)) {
			return;
		}
		FunctionMember value = (FunctionMember)annotationBinding.getValue();
			
		if(!value.getParameters().isEmpty()) {
			problemRequestor.acceptProblem(
				annotation,
				RUIResourceKeys.VALIDATOR_FUNCTION_HAS_PARAMETERS,
				IMarker.SEVERITY_ERROR,
				new String[] {value.getCaseSensitiveName()},
				RUIResourceKeys.getResourceBundleForKeys());
		}
	}
}
