/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.annotation.IFieldAccessAnnotationValidationRule;


public class JavaScriptObjectFieldAccessValidator implements IFieldAccessAnnotationValidationRule {

	public boolean validateLValue(Expression lValue, IDataBinding fieldBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if(hasGetterButNotSetter(fieldBinding)) {
			problemRequestor.acceptProblem(
				lValue,
				IProblemRequestor.CANNOT_WRITE_TO_EXTERNALTYPE_FIELD_WITH_NO_SETTER,
				new String[] {
						fieldBinding.getCaseSensitiveName()
				});
			return false;
		}
		return true;
	}
	
	public boolean validateRValue(Expression rValue, IDataBinding fieldBinding, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		if(hasSetterButNotGetter(fieldBinding)) {
			problemRequestor.acceptProblem(
				rValue,
				IProblemRequestor.CANNOT_READ_FROM_EXTERNALTYPE_FIELD_WITH_NO_GETTER,
				new String[] {
					fieldBinding.getCaseSensitiveName()
				});
			return false;
		}
		return true;
	}
	
	private boolean hasGetterButNotSetter(IDataBinding binding) {
		IAnnotationBinding aBinding = binding.getAnnotation(new String[] {"eglx", "lang"}, "Property");
		if(aBinding != null) {
			return aBinding.findData("getMethod") != IBinding.NOT_FOUND_BINDING &&
			       aBinding.findData("setMethod") == IBinding.NOT_FOUND_BINDING;
		}
		return false;
	}
	
	private boolean hasSetterButNotGetter(IDataBinding binding) {
		IAnnotationBinding aBinding = binding.getAnnotation(new String[] {"eglx", "lang"}, "Property");
		if(aBinding != null) {
			return aBinding.findData("setMethod") != IBinding.NOT_FOUND_BINDING &&
			       aBinding.findData("getMethod") == IBinding.NOT_FOUND_BINDING;
		}
		return false;
	}
}
