/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBindingCompletor;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class FlexibleRecordBinder extends DefaultBinder {

    private FlexibleRecordBinding recordBinding;
    private Scope scope;

    public FlexibleRecordBinder(FlexibleRecordBinding recordBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, recordBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.recordBinding = recordBinding;
        this.scope = scope;
    }

    public boolean visit(Record record) {
        // First we have to complete the record binding (as a side effect some of the AST nodes are bound)
        record.accept(new FlexibleRecordBindingCompletor(scope, recordBinding, dependencyRequestor, problemRequestor, compilerOptions));

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
