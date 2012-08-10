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
package org.eclipse.edt.compiler.internal.core.validation.part;

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.mof.utils.NameUtile;


public class ServiceValidator extends FunctionContainerValidator {
	IRPartBinding irBinding;
	org.eclipse.edt.mof.egl.Service serviceBinding;
	protected Service service;
	
	public ServiceValidator(IProblemRequestor problemRequestor, IRPartBinding irBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, irBinding, compilerOptions);
		this.irBinding = irBinding;
		serviceBinding = (org.eclipse.edt.mof.egl.Service)irBinding.getIrPart();
	}
	
	@Override
	public boolean visit(Service aservice) {
		//TODO validate wsdl properties
		this.service = aservice;
		partNode = aservice;
		EGLNameValidator.validate(service.getName(), EGLNameValidator.PART, problemRequestor, compilerOptions);
		checkImplements(service.getImplementedInterfaces());
		checkInterfaceFunctionsOverriden(serviceBinding);
		
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(aservice); TODO

		return true;
	}
	
	@Override
	public boolean visit(NestedFunction nestedFunction) {
		super.visit(nestedFunction);
		ServiceInterfaceValidatorUtil.validateParametersAndReturn(nestedFunction,problemRequestor); 
		
		if (NameUtile.equals(nestedFunction.getName().getCanonicalName(), IEGLConstants.MNEMONIC_MAIN)){
			problemRequestor.acceptProblem(service.getName(),
					IProblemRequestor.LIBRARY_NO_MAIN_FUNCTION_ALLOWED,
					new String[] {serviceBinding.getCaseSensitiveName()});
		}
		
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(nestedFunction); TODO
		
		return false;
	}
}
