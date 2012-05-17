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

import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FormBindingCompletor;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.ConstantFormField;
import org.eclipse.edt.compiler.core.ast.FormField;
import org.eclipse.edt.compiler.core.ast.NestedForm;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.core.ast.VariableFormField;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class FormBinder extends DefaultBinder {

    private FormBinding formBinding;
    private Scope scope;

    public FormBinder(FormBinding formBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, formBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.formBinding = formBinding;
        this.scope = scope;
    }

    public boolean visit(TopLevelForm form) {
        // First we have to complete the record binding (as a side effect some of the AST nodes are bound)
    	form.accept(new FormBindingCompletor(scope, formBinding, dependencyRequestor, problemRequestor, compilerOptions));

    	// The current scope only changes once the initial program binding is complete
        currentScope = new FormScope(currentScope, formBinding);

        // We will bind the rest of the form now
        return true;
    }
    
    public boolean visit(NestedForm form) {
        // First we have to complete the record binding (as a side effect some of the AST nodes are bound)
    	form.accept(new FormBindingCompletor(scope, formBinding, dependencyRequestor, problemRequestor, compilerOptions));

    	// The current scope only changes once the initial program binding is complete
        currentScope = new FormScope(currentScope, formBinding);

        // We will bind the rest of the form now
        return true;
    }
    
	public boolean visit(VariableFormField variableFormField) {
		return visitFormField(variableFormField);
	}
	
	public boolean visit(ConstantFormField constantFormField) {
		return visitFormField(constantFormField);
	}

    public boolean visitFormField(FormField field) {
        // Because part of the field declaration (i.e. its type) has already
        // been processed, we take over the traversal of FieldDeclaration in here
        if(field.hasSettingsBlock()) field.getSettingsBlock().accept(this);
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
