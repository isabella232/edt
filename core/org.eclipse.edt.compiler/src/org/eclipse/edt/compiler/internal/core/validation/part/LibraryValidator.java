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
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.validation.name.EGLNameValidator;
import org.eclipse.edt.mof.utils.NameUtile;

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
	
	public boolean visit(Library alibrary) {
		this.library = alibrary;
		partNode = alibrary;
		EGLNameValidator.validate(library.getName(), EGLNameValidator.LIBRARY, problemRequestor, compilerOptions);
//		new AnnotationValidator(problemRequestor, compilerOptions).validateAnnotationTarget(alibrary); TODO
		return true;
	}
	
	public boolean visit(ClassDataDeclaration classDataDeclaration) {
		super.visit(classDataDeclaration);
		return false;
	}
	
	public boolean visit(NestedFunction nestedFunction) {
		super.visit(nestedFunction);
		validateLibraryFunctions(nestedFunction);
		return false;
	}
	
	private void validateLibraryFunctions(final NestedFunction nestedFunction) {
		if (NameUtile.equals(nestedFunction.getName().getCanonicalName(), IEGLConstants.MNEMONIC_MAIN)){
			problemRequestor.acceptProblem(library.getName(),
					IProblemRequestor.LIBRARY_NO_MAIN_FUNCTION_ALLOWED,
					new String[] {libraryName});
			return;
		}
	}
}
