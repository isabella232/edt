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

import org.eclipse.edt.compiler.binding.DelegateBindingCompletor;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class DelegateBinder extends DefaultBinder {

    private IRPartBinding irBinding;
    private Scope scope;

    public DelegateBinder(IRPartBinding irBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, irBinding.getIrPart(), dependencyRequestor, problemRequestor, compilerOptions);
        this.scope = scope;
        this.irBinding = irBinding;
    }

    public boolean visit(Delegate delegate) {
    	delegate.accept(new DelegateBindingCompletor(scope, irBinding, dependencyRequestor, problemRequestor, compilerOptions));

        currentScope = new DelegateScope(currentScope, (org.eclipse.edt.mof.egl.Delegate)irBinding.getIrPart());
        return true;
    }
    
    public boolean visit(FunctionParameter functionParameter) {
        return false;
    }
    
}
