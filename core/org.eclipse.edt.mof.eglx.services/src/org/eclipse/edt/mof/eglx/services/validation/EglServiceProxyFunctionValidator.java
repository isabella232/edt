/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.eglx.services.validation;

import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.internal.core.validation.part.ServiceInterfaceValidatorUtil;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;

public class EglServiceProxyFunctionValidator extends ServiceProxyFunctionValidator {

	@Override
	protected void validate(NestedFunction nestedFunction) {
		ServiceInterfaceValidatorUtil.validateParametersAndReturn(nestedFunction, problemRequestor);
	}

	@Override
	protected String getName() {
		return "EglService";
	}

	@Override
	protected Annotation getAnnotation(Function function) {
		return function.getAnnotation("eglx.rest.EglService");
	}
}
