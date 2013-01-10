/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.validation;

import org.eclipse.edt.compiler.internal.core.validation.part.FunctionValidator;

public class DefaultFunctionValidator extends AbstractFunctionValidator {
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.NestedFunction nestedFunction) {
		nestedFunction.accept(new FunctionValidator(problemRequestor, declaringPart, compilerOptions));
		return false;
	};
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.Constructor constructor) {
		constructor.accept(new FunctionValidator(problemRequestor, declaringPart, compilerOptions));
		return false;
	};
}
