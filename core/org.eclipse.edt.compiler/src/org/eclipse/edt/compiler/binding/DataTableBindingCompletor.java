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
package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.DataTableScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public class DataTableBindingCompletor extends FixedStructureBindingCompletor {
	
    private DataTableBinding tableBinding;
    private IProblemRequestor problemRequestor;

    public DataTableBindingCompletor(Scope currentScope, DataTableBinding tableBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(tableBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.tableBinding = tableBinding;
        this.problemRequestor = problemRequestor;
    }
    
    public boolean visit(DataTable table) {
    	table.getName().setBinding(tableBinding);
    	tableBinding.setPrivate(table.isPrivate());
        table.accept(getPartSubTypeAndAnnotationCollector());
        processSubType();
        table.accept(new StructureItemsCompletor(currentScope, tableBinding, table.getName().getCanonicalName(), dependencyRequestor, problemRequestor, compilerOptions));
        tableBinding.clearSimpleNamesToDataBindingsMap();
        processSettingsBlocks();
        return false;
    }
    
	public void endVisit(DataTable dataTable) {
		tableBinding.setValid(true);
	}

    protected IPartSubTypeAnnotationTypeBinding getDefaultSubType() {
    	try {
    		return new AnnotationTypeBindingImpl((FlexibleRecordBinding) currentScope.findPackage(InternUtil.intern("eglx")).resolvePackage(InternUtil.intern("lang")).resolveType(InternUtil.intern("BasicTable")), tableBinding);
    	}
    	catch(UnsupportedOperationException e) {
    		return null;
    	}
    	catch(ClassCastException e) {
    		return null;
    	}
    }
    
    protected Scope getFixedStructureScope() {
        return new DataTableScope(currentScope, tableBinding);
    }

}
