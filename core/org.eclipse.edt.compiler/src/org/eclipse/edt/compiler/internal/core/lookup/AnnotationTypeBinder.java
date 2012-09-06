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

import org.eclipse.edt.compiler.binding.AnnotationTypeCompletor;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.core.ast.Assignment;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.StructureItem;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


public class AnnotationTypeBinder extends DefaultBinder {

    private org.eclipse.edt.mof.egl.AnnotationType annotationType;
    private IRPartBinding irBinding;
    private Scope scope;

    public AnnotationTypeBinder(IRPartBinding irBinding, Scope scope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, irBinding.getIrPart(), dependencyRequestor, problemRequestor, compilerOptions);
    	this.irBinding = irBinding;
        this.annotationType = (org.eclipse.edt.mof.egl.AnnotationType)irBinding.getIrPart();
        this.scope = scope;
    }

    public boolean visit(Record record) {
        record.accept(new AnnotationTypeCompletor(scope, irBinding, dependencyRequestor, problemRequestor, compilerOptions));

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
