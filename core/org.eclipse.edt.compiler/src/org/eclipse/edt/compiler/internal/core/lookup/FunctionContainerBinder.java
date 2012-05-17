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

import org.eclipse.edt.compiler.core.ast.ClassDataDeclaration;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.UseStatement;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Type;


public abstract class FunctionContainerBinder extends DefaultBinder {

    private org.eclipse.edt.mof.egl.Part functionContainerBinding;

    public FunctionContainerBinder(org.eclipse.edt.mof.egl.Part functionContainerBinding, Scope scope,
            IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(scope, functionContainerBinding, dependencyRequestor, problemRequestor, compilerOptions);
        this.functionContainerBinding = functionContainerBinding;
    }

    /**
     * Subclasses should invoke this message after the currentScope field is set
     * to an instance of FunctionContainerScope, but before returning from the
     * visit() method for the function container AST.
     */
    protected void preprocessPart(Part part) {

        dependencyRequestor.recordFunctionContainerScope((FunctionContainerScope) currentScope);
        
        for (Node node : part.getContents()) {
            node.accept(new DefaultASTVisitor() {
                //Bind the use statements of the function container first, as
                // their
                //targets affect resolution within functions.
                public boolean visit(UseStatement useStatement) {
                    for (Name nextName : useStatement.getNames()) {
                        Type typeBinding = nextName.resolveType();
                        
                        if (typeBinding == null) {
                            continue;
                        }
                                                
                        if (typeBinding instanceof org.eclipse.edt.mof.egl.Part) {
                        	 ((FunctionContainerScope) currentScope).addUsedPart((org.eclipse.edt.mof.egl.Part)typeBinding);
                        }
                    }
                    return false;
                }
            });
        }
    }

	protected void doneVisitingPart() {

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

    public boolean visit(NestedFunction nestedFunction) {    	
    	Function functionBinding = (Function) nestedFunction.getName().resolveMember();
        if (functionBinding != null) {
            FunctionBinder functionBinder = new FunctionBinder(functionContainerBinding, functionBinding, currentScope,
                    dependencyRequestor, problemRequestor, compilerOptions);
            nestedFunction.accept(functionBinder);
        }
        return false;
    }    
}
