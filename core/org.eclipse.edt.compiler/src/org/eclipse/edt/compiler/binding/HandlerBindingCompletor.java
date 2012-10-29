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

import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author winghong
 */
public class HandlerBindingCompletor extends FunctionContainerBindingCompletor {

    private org.eclipse.edt.mof.egl.Handler handlerBinding;

    public HandlerBindingCompletor(Scope currentScope, IRPartBinding irBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(irBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.handlerBinding = (org.eclipse.edt.mof.egl.Handler)irBinding.getIrPart();
    }
        
    public boolean visit(Handler handler) {
    	handler.getName().setType(handlerBinding);
        handler.accept(getPartSubTypeAndAnnotationCollector());
    	
    	if (handler.isPrivate()) {
    		handlerBinding.setAccessKind(AccessKind.ACC_PRIVATE);
    	}
    					
        for(Name nextName : handler.getImplementedInterfaces()) {
    		try {
    			org.eclipse.edt.mof.egl.Type typeBinding = bindTypeName(nextName);
    			
    			if (typeBinding instanceof StructPart) {
    				handlerBinding.getSuperTypes().add((StructPart)typeBinding);
    			}
    		}
    		catch (ResolutionException e) {
    			problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
    		}
    	}		
		
        setDefaultSuperType();
        return true;
    }
    
    public void endVisit(Handler handler) {
        processSettingsBlocks();
        endVisitFunctionContainer(handler);
    }
    
    protected StereotypeType getDefaultStereotypeType() {
    	try {
    		return (StereotypeType)BindingUtil.getAnnotationType(NameUtile.getAsName("eglx.lang"), NameUtile.getAsName("BasicHandler"));
    	}
    	catch(ClassCastException e) {
    		return null;
    	}
    }
}
