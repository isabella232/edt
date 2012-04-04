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

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.System.ISystemLibrary;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author demurray
 */
public class ValidatorFunctionAnnotationValidator implements IValueValidationRule{
	
	public void validate(final Node annotation, Node container, IAnnotationBinding annotationBinding, final IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
		if (!(annotationBinding.getValue() instanceof IFunctionBinding)) {
			return;
		}
		final IFunctionBinding value = (IFunctionBinding)annotationBinding.getValue();
			
		if(!value.getParameters().isEmpty()) {
			problemRequestor.acceptProblem(
				annotation,
				IProblemRequestor.VALIDATOR_FUNCTION_HAS_PARAMETERS,
				new String[] {value.getCaseSensitiveName()});
		}
		
		DefaultASTVisitor visitor = new DefaultASTVisitor() {
			public boolean visit(org.eclipse.edt.compiler.core.ast.Record record) {
				IAnnotationBinding ann = ((IPartBinding)record.getName().resolveBinding()).getSubTypeAnnotationBinding();
				if (ann != null && InternUtil.internCaseSensitive(IEGLConstants.PROPERTY_VGUIRECORD) == ann.getCaseSensitiveName()) {
					//Cannot use check digit functions as validator functions for UIRecord
					if (value.getSystemFunctionType() == ISystemLibrary.VerifyChkDigitMod10_func || value.getSystemFunctionType() == ISystemLibrary.VerifyChkDigitMod11_func) {
						problemRequestor.acceptProblem(
								annotation,
								IProblemRequestor.TYPE_VALIDATOR_FUNCTION_NOT_VALID_FOR_VGUIRECORD,
								new String[] {value.getCaseSensitiveName()});
					}
				}
				return false;
			}
			
		};
		
		if (container != null) {
			container.accept(visitor);
		}
		
		
	}
}
