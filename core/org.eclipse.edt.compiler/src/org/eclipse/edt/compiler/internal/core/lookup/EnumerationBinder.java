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

import org.eclipse.edt.compiler.binding.EnumerationBindingCompletor;
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author demurray
 */

public class EnumerationBinder extends DefaultBinder {

    private EnumerationTypeBinding enumerationBinding;
    private Scope scope;

    public EnumerationBinder(EnumerationTypeBinding enumerationBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, enumerationBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.enumerationBinding = enumerationBinding;
        this.scope = scope;
    }

    public boolean visit(Enumeration enumeration) {
    	enumeration.accept(new EnumerationBindingCompletor(scope, enumerationBinding, dependencyRequestor, problemRequestor, compilerOptions));
        return true;
    }
}
