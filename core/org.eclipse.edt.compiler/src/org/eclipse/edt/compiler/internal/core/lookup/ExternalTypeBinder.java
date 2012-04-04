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

import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBindingCompletor;
import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author demurray
 */

public class ExternalTypeBinder extends DefaultBinder {

    private ExternalTypeBinding externalTypeBinding;
    private Scope scope;

    public ExternalTypeBinder(ExternalTypeBinding externalTypeBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, externalTypeBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.externalTypeBinding = externalTypeBinding;
        this.scope = scope;
    }

    public boolean visit(ExternalType externalType) {
    	externalType.accept(new ExternalTypeBindingCompletor(scope, externalTypeBinding, dependencyRequestor, problemRequestor, compilerOptions));
		return true;
	}
    
    public boolean visit(ClassDataDeclaration classDataDeclaration) {
        // Because part of the field declaration (i.e. its type) has already
        // been processed, we take over the traversal of FieldDeclaration in
        // here
        if (classDataDeclaration.getSettingsBlockOpt() != null)
            classDataDeclaration.getSettingsBlockOpt().accept(this);
        if (classDataDeclaration.hasInitializer())
            classDataDeclaration.getInitializer().accept(this);
        return false;
    }
}
