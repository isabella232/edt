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

import org.eclipse.edt.compiler.binding.RecordBindingCompletor;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


public class RecordBinder extends DefaultBinder {

    private org.eclipse.edt.mof.egl.Record recordBinding;
    private IRPartBinding irBinding;
    private Scope scope;

    public RecordBinder(IRPartBinding irBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, irBinding.getIrPart(), dependencyRequestor, problemRequestor, compilerOptions);
    	this.irBinding = irBinding;
        this.recordBinding = (org.eclipse.edt.mof.egl.Record)irBinding.getIrPart();
        this.scope = scope;
    }

    public boolean visit(Record record) {
        // First we have to complete the record binding (as a side effect some of the AST nodes are bound)
        record.accept(new RecordBindingCompletor(scope, irBinding, dependencyRequestor, problemRequestor, compilerOptions));

        // The current scope only changes once the initial record binding is complete
        currentScope = new RecordScope(currentScope, recordBinding);

        // We will bind the rest of the record now
        return true;
    }

    public boolean visit (StructureItem item) {
        // Because part of the field declaration (i.e. its type) has already
        // been processed, we take over the traversal of FieldDeclaration in here
        if(item.hasSettingsBlock()) item.getSettingsBlock().accept(this);
        if(item.hasInitializer()) item.getInitializer().accept(this);
        return false;
    }
    
	public boolean visit(Assignment assignment) {
		Scope currentScopeParent = currentScope.getParentScope();		
		currentScope.setParentScope(NullScope.INSTANCE);
		assignment.getLeftHandSide().accept(this);
		currentScope.setParentScope(currentScopeParent);
		assignment.getRightHandSide().accept(this);
		return false;
	}
}
