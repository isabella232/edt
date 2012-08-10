package org.eclipse.edt.mof.eglx.services.validation;

import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.part.ServiceInterfaceValidatorUtil;

public class EglServiceProxyFunctionValidator extends
		ServiceProxyFunctionValidator {

	@Override
	protected void validate(NestedFunction nestedFunction, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
		// TODO Auto-generated method stub
		ServiceInterfaceValidatorUtil.validateParametersAndReturn(nestedFunction, problemRequestor);
	}

	@Override
	protected String getName() {
		return "EglService";
	}

}
