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
package org.eclipse.edt.compiler.internal.core.validation.part;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;


public class DelegateValidator extends FunctionValidator {

	public DelegateValidator(IProblemRequestor problemRequestor, IPartBinding enclosingPart, ICompilerOptions compilerOptions) {
		super(problemRequestor, enclosingPart, compilerOptions);
	}
	
	public boolean visit(Delegate delegate) {
		EGLNameValidator.validate(delegate.getName(), EGLNameValidator.DELEGATE, problemRequestor, compilerOptions);
		checkNumberOfParms(delegate.getParameters(), delegate.getName(), delegate.getName().getCanonicalName());		
		return true;
	}
}
