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


/**
 * @author winghong
 */
public class ServiceBindingCompletor extends FunctionContainerBindingCompletor {

    private ServiceBinding serviceBinding;

    public ServiceBindingCompletor(Scope currentScope, ServiceBinding serviceBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(serviceBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.serviceBinding = serviceBinding;
    }
    
    public boolean visit(Service service) {
    	service.getName().setBinding(serviceBinding);
    	service.getName().setTypeBinding(serviceBinding);
    	service.accept(getPartSubTypeAndAnnotationCollector());
    	
    	serviceBinding.setPrivate(service.isPrivate());
    	
    	addImplicitFieldsFromAnnotations();
         
        for(Iterator iter = service.getImplementedInterfaces().iterator(); iter.hasNext();) {
    		Name nextName = (Name) iter.next();
    		try {
    			ITypeBinding typeBinding = bindTypeName(nextName);
    			//TODO should probably check to see if this is an interfaceBinding before adding it
    			serviceBinding.addExtenedInterface(typeBinding);
    			
    			if(ITypeBinding.INTERFACE_BINDING != typeBinding.getKind()) {
    				problemRequestor.acceptProblem(
    					nextName,
						IProblemRequestor.SERVICE_OR_HANDLER_MUST_IMPLEMENT_AN_INTERFACE);
    			}
    		}
    		catch (ResolutionException e) {
    			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
    		}
    	}
        
        return true;
    }

    protected IPartSubTypeAnnotationTypeBinding getDefaultSubType() {
        return null;
    }   
    
    public void endVisit(Service service) {
        processSettingsBlocks();
        endVisitFunctionContainer(service);
    }
}
