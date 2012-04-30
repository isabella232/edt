/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.binding.ProgramBindingCompletor;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.ProgramParameter;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class ProgramBinder extends FunctionContainerBinder {

    private org.eclipse.edt.mof.egl.Program programBinding;
    private IRPartBinding irBinding;
    private Scope scope;

    public ProgramBinder(IRPartBinding irBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(irBinding.getIrPart(), scope, dependencyRequestor, problemRequestor, compilerOptions);
    	this.irBinding = irBinding;
    	this.programBinding = (org.eclipse.edt.mof.egl.Program)irBinding.getIrPart();
        this.scope = scope;
    }

    public boolean visit(Program program) {
        // First we have to complete the program binding (as a side effect some of the AST nodes are bound)
        program.accept(new ProgramBindingCompletor(scope, irBinding, dependencyRequestor, problemRequestor, compilerOptions));

        // The current scope only changes once the initial program binding is complete
        currentScope = new ProgramScope(currentScope, programBinding);
        
        preprocessPart(program);

        // We will bind the rest of the program now
        return true;
    }
    
    public void endVisit(Program program) {
		doneVisitingPart();
	}
    
    public boolean visit(ProgramParameter programParameter) {
        if (programParameter.getName().resolveType() == null) {
            return false;
        }
        processResolvableProperties(programParameter.getName());
        return false;
    }

}
