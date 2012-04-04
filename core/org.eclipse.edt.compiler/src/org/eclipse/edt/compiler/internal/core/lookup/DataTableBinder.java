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

import org.eclipse.edt.compiler.binding.DataTableBinding;
import org.eclipse.edt.compiler.binding.DataTableBindingCompletor;
import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class DataTableBinder extends DefaultBinder {

    private DataTableBinding tableBinding;
    private Scope fileScope;

    public DataTableBinder(DataTableBinding tableBinding, Scope fileScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(fileScope, tableBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.tableBinding = tableBinding;
        this.fileScope = fileScope;
    }

    public boolean visit(DataTable table) {
        // First we have to complete the record binding (as a side effect some of the AST nodes are bound)
    	table.accept(new DataTableBindingCompletor(fileScope, tableBinding, dependencyRequestor, problemRequestor, compilerOptions));

        // The current scope only changes once the initial record binding is complete
        //currentScope = new FixedRecordScope(currentScope, recordBinding);

        // We will bind the rest of the record now
        return true;
    }

    public boolean visit (StructureItem item) {
        // Because part of the field declaration (i.e. its type) has already
        // been processed, we take over the traversal of FieldDeclaration in here
        if(item.hasSettingsBlock()) item.getSettingsBlock().accept(this);
        return false;
    }
}
