/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author winghong
 */
public class LibraryBindingCompletor extends FunctionContainerBindingCompletor {

    private org.eclipse.edt.mof.egl.Library libraryBinding;

    public LibraryBindingCompletor(Scope currentScope, IRPartBinding irBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(irBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.libraryBinding = (org.eclipse.edt.mof.egl.Library)irBinding.getIrPart();
    }
    
    public boolean visit(Library library) {
    	library.getName().setType(libraryBinding);
        library.accept(getPartSubTypeAndAnnotationCollector());
        
        if (library.isPrivate()) {
        	libraryBinding.setAccessKind(AccessKind.ACC_PRIVATE);
        }        
       setDefaultSuperType();
       return true;
    }
 
    protected StereotypeType getDefaultStereotypeType() {
    	try {
    		return (StereotypeType)BindingUtil.getAnnotationType(NameUtile.getAsName("eglx.lang"), NameUtile.getAsName("BasicLibrary"));
    	}
    	catch(ClassCastException e) {
    		return null;
    	}
    }
    
    public void endVisit(Library library) {
        processSettingsBlocks();        
        endVisitFunctionContainer(library);        
    }
    
    protected boolean membersStaticByDefault() {
    	return true;
    }
}
