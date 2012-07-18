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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.egl.StructPart;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
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
		
        return true;
    }
    
    public boolean visit(Constructor constructor) {
    	final org.eclipse.edt.mof.egl.Constructor constructorBinding = IrFactory.INSTANCE.createConstructor();
    	final Set<String> definedParameters = new HashSet<String>();
    	
    	constructor.setBinding(constructorBinding);
    	
    	if (constructor.isPrivate()) {
    		constructorBinding.setAccessKind(AccessKind.ACC_PRIVATE);
    	}
    	
    	constructor.accept(new AbstractASTVisitor() {
    		public boolean visit(FunctionParameter functionParameter) {
    			String parmName = functionParameter.getName().getIdentifier();
    	        Type parmType = functionParameter.getType();        
    	        org.eclipse.edt.mof.egl.Type typeBinding = null;
    	        try {
    	            typeBinding = bindType(parmType);
    	        } catch (ResolutionException e) {
    	            problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
    	            return false;
    	        }
    	        
    	        org.eclipse.edt.mof.egl.FunctionParameter funcParmBinding = IrFactory.INSTANCE.createFunctionParameter();
    	        funcParmBinding.setName(functionParameter.getName().getCaseSensitiveIdentifier());
    	        functionParameter.getName().setMember(funcParmBinding);
    	        funcParmBinding.setType(typeBinding);
    	        funcParmBinding.setIsNullable(functionParameter.isNullable());   	        
    	        
    	        funcParmBinding.setIsConst(functionParameter.isParmConst());
    	        
    	        FunctionParameter.UseType useType = functionParameter.getUseType();
    	        if (useType == FunctionParameter.UseType.IN) {
    	            funcParmBinding.setParameterKind(ParameterKind.PARM_IN);
    	        } else if (useType == FunctionParameter.UseType.OUT) {
    	            funcParmBinding.setParameterKind(ParameterKind.PARM_OUT);
    	        } else if (useType == null && typeBinding != null && TypeUtils.isReferenceType(typeBinding)) {
    	        	funcParmBinding.setParameterKind(ParameterKind.PARM_IN);
    	        }
    	        else {
    	        	funcParmBinding.setParameterKind(ParameterKind.PARM_INOUT);
    	        }
 
    	        if (definedParameters.contains(parmName)) {
    	            problemRequestor.acceptProblem(functionParameter, IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] { functionParameter.getName().getCanonicalName(), IEGLConstants.KEYWORD_CONSTRUCTOR });
    	        } else {
    	            constructorBinding.addParameter(funcParmBinding);
    	            definedParameters.add(parmName);
    	        }
    	        
    	        return false;
    		}
    	});
    	
    	handlerBinding.getConstructors().add(constructorBinding);
    	
    	if (constructor.hasSettingsBlock()) {
            FunctionScope functionScope = new FunctionScope(currentScope, constructorBinding);
            AnnotationLeftHandScope scope = new AnnotationLeftHandScope(functionScope, constructorBinding, null, constructorBinding);
            SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(functionScope, handlerBinding, scope,
                    dependencyRequestor, problemRequestor, compilerOptions);
            constructor.getSettingsBlock().accept(blockCompletor);
    		
    	}
    	
    	return false;
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
