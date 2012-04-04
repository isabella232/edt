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

import org.eclipse.edt.compiler.binding.FormGroupBinding;
import org.eclipse.edt.compiler.binding.FormGroupBindingCompletor;
import org.eclipse.edt.compiler.core.ast.ConstantFormField;
import org.eclipse.edt.compiler.core.ast.FormField;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.VariableFormField;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class FormGroupBinder extends DefaultBinder {

    private FormGroupBinding formGroupBinding;
    private Scope fileScope;

    public FormGroupBinder(FormGroupBinding formGroupBinding, Scope fileScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(fileScope, formGroupBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.formGroupBinding = formGroupBinding;
        this.fileScope = fileScope;
    }

    public boolean visit(FormGroup formGroup) {
    	formGroup.accept(new FormGroupBindingCompletor(fileScope, formGroupBinding, dependencyRequestor, problemRequestor, compilerOptions));
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
}
