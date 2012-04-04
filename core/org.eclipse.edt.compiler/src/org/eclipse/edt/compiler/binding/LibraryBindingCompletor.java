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
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public class LibraryBindingCompletor extends FunctionContainerBindingCompletor {

    private LibraryBinding libraryBinding;

    public LibraryBindingCompletor(Scope currentScope, LibraryBinding libraryBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(libraryBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.libraryBinding = libraryBinding;
    }
    
    public boolean visit(Library library) {
    	library.getName().setBinding(libraryBinding);
        library.accept(getPartSubTypeAndAnnotationCollector());
        
        libraryBinding.setPrivate(library.isPrivate());
        
        addImplicitFieldsFromAnnotations();
        
        return true;
    }
    protected IPartSubTypeAnnotationTypeBinding getDefaultSubType() {
    	try {
    		return null;
    	}
    	catch(UnsupportedOperationException e) {
    		return null;
    	}
    	catch(ClassCastException e) {
    		return null;
    	}
    }
    
    public void endVisit(Library library) {
        processSettingsBlocks();        
        endVisitFunctionContainer(library);        
    }
}
