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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.binding.ConstructorBinding;
import org.eclipse.edt.compiler.binding.EGLClassBinding;
import org.eclipse.edt.compiler.binding.EGLClassBindingCompletor;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class EGLClassBinder extends FunctionContainerBinder {

    private EGLClassBinding classBinding;
    private Scope fileScope;

    public EGLClassBinder(EGLClassBinding classBinding, Scope fileScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(classBinding, fileScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.classBinding = classBinding;
        this.fileScope = fileScope;
    }

    public boolean visit(EGLClass eglClass) {
        // First we have to complete the class binding (as a side effect some of the AST nodes are bound)
        eglClass.accept(new EGLClassBindingCompletor(fileScope, classBinding, dependencyRequestor, problemRequestor, compilerOptions));

        // The current scope only changes once the initial class binding is complete
        currentScope = new FunctionContainerScope(currentScope, classBinding);
        
        preprocessPart(eglClass);

        // We will bind the rest of the class now
        return true;
    }
    
	public void endVisit(EGLClass classs) {
		doneVisitingPart();
	}
		
   
    public boolean visit(Constructor constructor) {    	
    	IFunctionBinding functionBinding = (IFunctionBinding) ((ConstructorBinding) constructor.getBinding()).getType();
        if (functionBinding != null) {
            FunctionBinder functionBinder = new FunctionBinder(classBinding, functionBinding, currentScope,
                    dependencyRequestor, problemRequestor, compilerOptions);
            constructor.accept(functionBinder);
        }
        return false;
    }

}
