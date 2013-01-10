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

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.ExpressionValidator;
import org.eclipse.edt.compiler.internal.core.validation.annotation.AnnotationValidator;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;

public class LibraryValidator extends FunctionContainerValidator {
	String libraryName = "";
	Library library;
	org.eclipse.edt.mof.egl.Library libraryBinding;
	IRPartBinding irBinding;
	
	public LibraryValidator(IProblemRequestor problemRequestor, IRPartBinding irBinding, ICompilerOptions compilerOptions) {
		super(problemRequestor, irBinding, compilerOptions);
		libraryName = partBinding.getCaseSensitiveName();
		this.irBinding = irBinding;
		this.libraryBinding = (org.eclipse.edt.mof.egl.Library)irBinding.getIrPart();
	}
	
	@Override
	public boolean visit(Library alibrary) {
		this.library = alibrary;
		partNode = alibrary;
		EGLNameValidator.validate(library.getName(), EGLNameValidator.LIBRARY, problemRequestor, compilerOptions);
		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(alibrary);
		alibrary.accept(new ExpressionValidator(partBinding, problemRequestor, compilerOptions));
		return true;
	}
}
