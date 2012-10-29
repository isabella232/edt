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

import org.eclipse.edt.compiler.binding.HandlerBindingCompletor;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class HandlerBinder extends FunctionContainerBinder {

    private org.eclipse.edt.mof.egl.Handler handlerBinding;
    private IRPartBinding irBinding;
    private Scope fileScope;

    public HandlerBinder(IRPartBinding irBinding, Scope fileScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(irBinding.getIrPart(), fileScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.irBinding =irBinding;
        this.handlerBinding = (org.eclipse.edt.mof.egl.Handler)irBinding.getIrPart();
        this.fileScope = fileScope;
    }

    public boolean visit(Handler handler) {
        // First we have to complete the handler binding (as a side effect some of the AST nodes are bound)
        handler.accept(new HandlerBindingCompletor(fileScope, irBinding, dependencyRequestor, problemRequestor, compilerOptions));

        // The current scope only changes once the initial handler binding is complete
        currentScope = new HandlerScope(currentScope, handlerBinding);
        
        preprocessPart(handler);

        // We will bind the rest of the handler now
        return true;
    }
    
	public void endVisit(Handler handler) {
		doneVisitingPart();
	}
	
	protected void doneVisitingPart() {
		super.doneVisitingPart();
    }
	
}
