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

import java.util.Iterator;

import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.Type;


/**
 * @author winghong
 */
public class ServiceBindingCompletor extends FunctionContainerBindingCompletor {

    private org.eclipse.edt.mof.egl.Service serviceBinding;

    public ServiceBindingCompletor(Scope currentScope, IRPartBinding irBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(irBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.serviceBinding = (org.eclipse.edt.mof.egl.Service)irBinding.getIrPart();
    }
    
    public boolean visit(Service service) {
    	service.getName().setType(serviceBinding);
    	service.accept(getPartSubTypeAndAnnotationCollector());
    	
    	if (service.isPrivate()) {
    		serviceBinding.setAccessKind(AccessKind.ACC_PRIVATE);
    	}

    	for(Name nextName : service.getImplementedInterfaces()) {
    		try {
    			Type typeBinding = bindTypeName(nextName);
    			if (typeBinding instanceof StructPart) {
    				serviceBinding.getSuperTypes().add((StructPart)typeBinding);
    			}
     		}
    		catch (ResolutionException e) {
    			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
    		}
    	}
        
        return true;
    }

    public void endVisit(Service service) {
        processSettingsBlocks();
        endVisitFunctionContainer(service);
    }

	@Override
	protected StereotypeType getDefaultStereotypeType() {
		return null;
	}
}
