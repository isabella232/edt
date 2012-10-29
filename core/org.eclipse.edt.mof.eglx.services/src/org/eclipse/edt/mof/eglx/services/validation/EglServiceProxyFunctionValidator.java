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
