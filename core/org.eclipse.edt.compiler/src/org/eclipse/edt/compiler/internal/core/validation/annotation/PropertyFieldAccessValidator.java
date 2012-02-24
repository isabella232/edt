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
package org.eclipse.edt.compiler.internal.core.validation.annotation;

import org.eclipse.edt.compiler.binding.AnnotationFieldBinding;
import org.eclipse.edt.compiler.binding.Binding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;


public class PropertyFieldAccessValidator implements IFieldAccessAnnotationValidationRule {
	
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
	
	protected IAnnotationBinding getAnnotation(IDataBinding binding) {
		return binding.getAnnotation(new String[] {"eglx", "lang"}, "Property");
	}
	
	private boolean hasGetterButNotSetter(IDataBinding binding) {
		IAnnotationBinding aBinding = getAnnotation(binding);
		if(aBinding != null) {
			return hasGet(aBinding) &&
			       !hasSet(aBinding);
		}
		return false;
	}
	
	private boolean hasGet(IAnnotationBinding aBinding) {
		return hasValue(aBinding, "getMethod");
	}
	private boolean hasSet(IAnnotationBinding aBinding) {
		return hasValue(aBinding, "setMethod");
	}

	
	private boolean hasValue(IAnnotationBinding aBinding, String fieldName) {
		IDataBinding annField = aBinding.findData(fieldName);
		if (!Binding.isValidBinding(annField)) {
			return false;
		}
		return hasValue(((AnnotationFieldBinding)annField).getValue());
		
	}
	
	protected boolean hasValue(Object obj) {
		
		return (obj != null) && obj.toString().length() > 0;
	}
	
	private boolean hasSetterButNotGetter(IDataBinding binding) {
		IAnnotationBinding aBinding = getAnnotation(binding);
		if(aBinding != null) {
			return hasSet(aBinding) &&
			       !hasGet(aBinding);
		}
		return false;
	}
}
