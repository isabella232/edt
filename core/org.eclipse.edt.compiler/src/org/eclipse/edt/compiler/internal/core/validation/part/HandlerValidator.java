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
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;

public class HandlerValidator extends FunctionContainerValidator {
	
	IRPartBinding irBinding;
	org.eclipse.edt.mof.egl.Handler handlerBinding;
	Handler handler;
	
	public HandlerValidator(IProblemRequestor problemRequestor, IRPartBinding irBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, irBinding, compilerOptions);
		this.irBinding = irBinding;
		handlerBinding = (org.eclipse.edt.mof.egl.Handler)irBinding.getIrPart();
	}
	
	@Override
	public boolean visit(Handler ahandler) {
		handler = ahandler;
		partNode = ahandler;
		EGLNameValidator.validate(handler.getName(), EGLNameValidator.HANDLER, problemRequestor, compilerOptions);
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(ahandler); //TODO
		
		checkImplements(handler.getImplementedInterfaces());
		checkInterfaceFunctionsOverriden(handlerBinding);
		return true;
	}
}
