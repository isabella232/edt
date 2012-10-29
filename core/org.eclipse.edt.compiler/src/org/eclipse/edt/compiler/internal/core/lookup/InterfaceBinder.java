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
import org.eclipse.edt.compiler.binding.InterfaceBindingCompletor;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author demurray
 */

public class InterfaceBinder extends FunctionContainerBinder {

	private org.eclipse.edt.mof.egl.Interface interfaceBinding;
    private IRPartBinding irBinding;
    private Scope scope;

    public InterfaceBinder(IRPartBinding irBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
    	super(irBinding.getIrPart(), scope, dependencyRequestor, problemRequestor, compilerOptions);
        this.irBinding =irBinding;
        this.interfaceBinding = (org.eclipse.edt.mof.egl.Interface)irBinding.getIrPart();
        this.scope = scope;
    }

    public boolean visit(Interface interfaceNode) {
    	interfaceNode.accept(new InterfaceBindingCompletor(scope, irBinding, dependencyRequestor, problemRequestor, compilerOptions));
    	
        // The current scope only changes once the initial interfce binding is complete
    	//TODO do we need an InterfaceScope?
        currentScope = new FunctionContainerScope(currentScope, interfaceBinding);
        
        preprocessPart(interfaceNode);

        // We will bind the rest of the interface now
		return true;
	}
}
