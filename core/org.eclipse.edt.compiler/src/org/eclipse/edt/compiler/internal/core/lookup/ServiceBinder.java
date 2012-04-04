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

import org.eclipse.edt.compiler.binding.ServiceBinding;
import org.eclipse.edt.compiler.binding.ServiceBindingCompletor;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class ServiceBinder extends FunctionContainerBinder {

    private ServiceBinding serviceBinding;
    private Scope fileScope;

    public ServiceBinder(ServiceBinding serviceBinding, Scope fileScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(serviceBinding, fileScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.serviceBinding = serviceBinding;
        this.fileScope = fileScope;
    }

    public boolean visit(Service service) {
        // First we have to complete the service binding (as a side effect some of the AST nodes are bound)
        service.accept(new ServiceBindingCompletor(fileScope, serviceBinding, dependencyRequestor, problemRequestor, compilerOptions));

        // The current scope only changes once the initial service binding is complete
        currentScope = new ServiceScope(currentScope, serviceBinding);
        
        preprocessPart(service);

        // We will bind the rest of the service now
        return true;
    }
    
    public void endVisit(Service service) {
		doneVisitingPart();
	}
}
