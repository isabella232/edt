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
package org.eclipse.edt.compiler.internal.core.lookup;

import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.binding.LibraryBindingCompletor;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class LibraryBinder extends FunctionContainerBinder {

    private org.eclipse.edt.mof.egl.Library libraryBinding;
    private IRPartBinding irBinding;
    private Scope fileScope;

    public LibraryBinder(IRPartBinding irBinding, Scope fileScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(irBinding.getIrPart(), fileScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.irBinding =irBinding;
        this.libraryBinding = (org.eclipse.edt.mof.egl.Library)irBinding.getIrPart();
        this.fileScope = fileScope;
    }

    public boolean visit(Library library) {
        // First we have to complete the library binding (as a side effect some of the AST nodes are bound)
        library.accept(new LibraryBindingCompletor(fileScope, irBinding, dependencyRequestor, problemRequestor, compilerOptions));

        // The current scope only changes once the initial library binding is complete
        currentScope = new LibraryScope(currentScope, libraryBinding);
        
        preprocessPart(library);

        // We will bind the rest of the library now
        return true;
    }
    
    public void endVisit(Library library) {
		doneVisitingPart();
	}
}
