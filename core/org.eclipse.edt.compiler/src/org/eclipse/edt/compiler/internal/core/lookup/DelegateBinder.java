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

import org.eclipse.edt.compiler.binding.DelegateBinding;
import org.eclipse.edt.compiler.binding.DelegateBindingCompletor;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class DelegateBinder extends DefaultBinder {

    private DelegateBinding delegateBinding;
    private Scope scope;

    public DelegateBinder(DelegateBinding delegateBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, delegateBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.delegateBinding = delegateBinding;
        this.scope = scope;
    }

    public boolean visit(Delegate delegate) {
    	delegate.accept(new DelegateBindingCompletor(scope, delegateBinding, dependencyRequestor, problemRequestor, compilerOptions));

        return true;
    }
}
