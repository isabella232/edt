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

import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.binding.InterfaceBindingCompletor;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author demurray
 */

public class InterfaceBinder extends DefaultBinder {

    private InterfaceBinding interfaceBinding;
    private Scope scope;

    public InterfaceBinder(InterfaceBinding interfaceBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, interfaceBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.interfaceBinding = interfaceBinding;
        this.scope = scope;
    }

    public boolean visit(Interface interfaceNode) {
    	interfaceNode.accept(new InterfaceBindingCompletor(scope, interfaceBinding, dependencyRequestor, problemRequestor, compilerOptions));
		return true;
	}
}
