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

import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;


/**
 * @author winghong
 */
public class EGLClassBindingCompletor extends FunctionContainerBindingCompletor {

    private org.eclipse.edt.mof.egl.EGLClass classBinding;

    public EGLClassBindingCompletor(Scope currentScope, IRPartBinding irBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(irBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.classBinding = (org.eclipse.edt.mof.egl.EGLClass)irBinding.getIrPart();
    }
        
    public boolean visit(EGLClass eglClass) {
    	eglClass.getName().setType(classBinding);
        eglClass.accept(getPartSubTypeAndAnnotationCollector());
    	
    	if (eglClass.isPrivate()) {
    		classBinding.setAccessKind(AccessKind.ACC_PRIVATE);
    	}
    	
    	if (eglClass.getExtends() != null) {
    		try {
    			org.eclipse.edt.mof.egl.Type typeBinding = bindTypeName(eglClass.getExtends());
    			if (typeBinding instanceof StructPart) {
    				classBinding.getSuperTypes().add((StructPart)typeBinding);
    			}
   		}
    		catch (ResolutionException e) {
    			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
    		}
    	}
    					
        for(Name nextName : eglClass.getImplementedInterfaces()) {
    		try {
    			org.eclipse.edt.mof.egl.Type typeBinding = bindTypeName(nextName);
    			
    			if (typeBinding instanceof StructPart) {
    				classBinding.getSuperTypes().add((StructPart)typeBinding);
    			}
    		}
    		catch (ResolutionException e) {
    			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
    		}
    	}		
		
        setDefaultSuperType();
        return true;
    }
    
    public void endVisit(EGLClass eglClass) {
        processSettingsBlocks();
        endVisitFunctionContainer(eglClass);
    }
    
    protected StereotypeType getDefaultStereotypeType() {
    	return null;
    }

}
