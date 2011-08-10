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
package org.eclipse.edt.compiler.binding;

import java.util.Iterator;

import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public class HandlerBindingCompletor extends FunctionContainerBindingCompletor {

    private HandlerBinding handlerBinding;

    public HandlerBindingCompletor(Scope currentScope, HandlerBinding handlerBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(handlerBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.handlerBinding = handlerBinding;
    }
        
    public boolean visit(Handler handler) {
    	handler.getName().setBinding(handlerBinding);
    	
    	handlerBinding.setPrivate(handler.isPrivate());
    	
        PartSubTypeAndAnnotationCollector col = getPartSubTypeAndAnnotationCollector();
		handler.accept(col);
		
//		AnnotationBinding subTypeAnnotationBinding = col.getSubTypeAnnotationBinding();
//		if(subTypeAnnotationBinding == null || annotationIs(subTypeAnnotationBinding.getAnnotationType(), new String[] {"egl", "ui", "jasper"}, "JasperReport")) {
//			handlerBinding.addDeclaredFunctions(JasperReportAnnotationTypeBinding.getJasperReportFunctions());
//		}
		
		addImplicitFieldsFromAnnotations();
		
        for(Iterator iter = handler.getImplementedInterfaces().iterator(); iter.hasNext();) {
    		Name nextName = (Name) iter.next();
    		try {
    			ITypeBinding typeBinding = bindTypeName(nextName);
    			//TODO should probably check to see if this is an interfaceBinding before adding it
    			handlerBinding.addExtenedInterface(typeBinding);
    			
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
    
    public void endVisit(Handler handler) {
        processSettingsBlocks();
        endVisitFunctionContainer(handler);
    }
    
    protected IPartSubTypeAnnotationTypeBinding getDefaultSubType() {
    	try {
    		return new AnnotationTypeBindingImpl((FlexibleRecordBinding) currentScope.findPackage(InternUtil.intern("eglx")).resolvePackage(InternUtil.intern("lang")).resolveType(InternUtil.intern("BasicHandler")), handlerBinding);
    	}
    	catch(UnsupportedOperationException e) {
    		return null;
    	}
    	catch(ClassCastException e) {
    		return null;
    	}
    }
}
