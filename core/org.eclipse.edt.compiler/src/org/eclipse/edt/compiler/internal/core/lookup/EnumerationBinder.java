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
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author demurray
 */

public class EnumerationBinder extends DefaultBinder {

    private org.eclipse.edt.mof.egl.Enumeration enumerationBinding;
    private IRPartBinding irBinding;
    private Scope scope;

    public EnumerationBinder(IRPartBinding enumerationBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, enumerationBinding.getIrPart(), dependencyRequestor, problemRequestor, compilerOptions);
        this.enumerationBinding = (org.eclipse.edt.mof.egl.Enumeration) enumerationBinding.getIrPart();
        this.scope = scope;
        irBinding = enumerationBinding;
    }

    public boolean visit(Enumeration enumeration) {
    	enumeration.accept(new EnumerationBindingCompletor(scope, irBinding, dependencyRequestor, problemRequestor, compilerOptions));
        return true;
    }
}
