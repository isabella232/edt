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

import java.util.Iterator;

import org.eclipse.edt.compiler.core.ast.ExternalType;
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
public class ExternalTypeBindingCompletor extends FunctionContainerBindingCompletor {

    private org.eclipse.edt.mof.egl.ExternalType externalTypeBinding;
	
    public ExternalTypeBindingCompletor(Scope currentScope, IRPartBinding irBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(irBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.externalTypeBinding = (org.eclipse.edt.mof.egl.ExternalType)irBinding.getIrPart();
    }
    
    public boolean visit(ExternalType externalType) {
    	externalType.getName().setType(externalTypeBinding);
    	externalType.accept(getPartSubTypeAndAnnotationCollector());
        
        if (externalType.isPrivate()) {
        	externalTypeBinding.setAccessKind(AccessKind.ACC_PRIVATE);
        }
        
        externalTypeBinding.setIsAbstract(externalType.isAbstract());
        
        processExtends(externalType);
    	
    	setDefaultSuperType();
        return true;
    }
    
    private void processExtends(ExternalType externalType) {
        if(externalType.hasExtendedType()) {
    		for(Iterator iter = externalType.getExtendedTypes().iterator(); iter.hasNext();) {
            	try {
            		Name name = (Name) iter.next();
            		org.eclipse.edt.mof.egl.Type typeBinding = bindTypeName(name);
            		
            		if (typeBinding instanceof StructPart) {
        				externalTypeBinding.getSuperTypes().add((StructPart)typeBinding);
        			}
            	}
        		catch (ResolutionException e) {
        			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
        		}
    		}
        }
    }
    
    
	public void endVisit(ExternalType externalType) {
		processSettingsBlocks();
		endVisitFunctionContainer(externalType);
	}
    
    protected StereotypeType getDefaultStereotypeType() {
    	return null;
    }
}
