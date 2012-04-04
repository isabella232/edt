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

import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.DataItemBindingCompletor;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class DataItemBinder extends DefaultBinder {

    private DataItemBinding itemBinding;
    private Scope scope;

    public DataItemBinder(DataItemBinding itemBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, itemBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.itemBinding = itemBinding;
        this.scope = scope;
    }

    public boolean visit(DataItem dataItem) {
        // First we have to complete the dataItem binding (as a side effect some of the AST nodes are bound)
    	dataItem.accept(new DataItemBindingCompletor(scope, itemBinding, dependencyRequestor, problemRequestor, compilerOptions));

        return true;
    }
}
