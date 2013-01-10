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
package org.eclipse.edt.compiler.internal.core.lookup;

import org.eclipse.edt.compiler.binding.ClassBindingCompletor;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.ast.Class;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class ClassBinder extends FunctionContainerBinder {

    private org.eclipse.edt.mof.egl.EGLClass classBinding;
    private IRPartBinding irBinding;
    private Scope fileScope;

    public ClassBinder(IRPartBinding irBinding, Scope fileScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(irBinding.getIrPart(), fileScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.irBinding =irBinding;
        this.classBinding = (org.eclipse.edt.mof.egl.EGLClass)irBinding.getIrPart();
        this.fileScope = fileScope;
    }

    public boolean visit(Class eglClass) {
        // First we have to complete the class binding (as a side effect some of the AST nodes are bound)
        eglClass.accept(new ClassBindingCompletor(fileScope, irBinding, dependencyRequestor, problemRequestor, compilerOptions));

        // The current scope only changes once the initial class binding is complete
        currentScope = new FunctionContainerScope(currentScope, classBinding);
        
        preprocessPart(eglClass);

        // We will bind the rest of the class now
        return true;
    }
    
	public void endVisit(Class classs) {
		doneVisitingPart();
	}
		
}
