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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.edt.compiler.binding.ConstructorBinding;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordFieldBinding;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.HandlerBindingCompletor;
import org.eclipse.edt.compiler.binding.IAnnotationBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.NestedFunctionBinding;
import org.eclipse.edt.compiler.binding.StructureItemBinding;
import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author winghong
 */

public class HandlerBinder extends FunctionContainerBinder {

    private HandlerBinding handlerBinding;
    private Scope fileScope;

    public HandlerBinder(HandlerBinding handlerBinding, Scope fileScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(handlerBinding, fileScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.handlerBinding = handlerBinding;
        this.fileScope = fileScope;
    }

    public boolean visit(Handler handler) {
        // First we have to complete the handler binding (as a side effect some of the AST nodes are bound)
        handler.accept(new HandlerBindingCompletor(fileScope, handlerBinding, dependencyRequestor, problemRequestor, compilerOptions));

        // The current scope only changes once the initial handler binding is complete
        currentScope = new HandlerScope(currentScope, handlerBinding);
        
        preprocessPart(handler);

        // We will bind the rest of the handler now
        return true;
    }
    
	public void endVisit(Handler handler) {
		doneVisitingPart();
	}
	
	protected boolean isPageHandler() {
		return handlerBinding.getAnnotation(new String[] {"egl", "ui", "jsf"}, "JSFHandler") != null;
	}
	
	protected void doneVisitingPart() {
		super.doneVisitingPart();
        
        IAnnotationBinding jsfHandlerABinding = handlerBinding.getAnnotation(new String[] {"egl", "ui", "jsf"}, "JSFHandler"); 
		if(jsfHandlerABinding != null) {
			IAnnotationBinding validatorFunctionAnnotation = (IAnnotationBinding) jsfHandlerABinding.findData(IEGLConstants.PROPERTY_VALIDATORFUNCTION);
			Set validatorFunctions = new HashSet();
			
			if(validatorFunctionAnnotation != IBinding.NOT_FOUND_BINDING) {
    			IFunctionBinding validatorFunctionBinding = (IFunctionBinding) validatorFunctionAnnotation.getValue();
				if(validatorFunctionBinding != IBinding.NOT_FOUND_BINDING && validatorFunctionBinding.isPartBinding()) {
					validatorFunctions.add(validatorFunctionBinding);
				}
    		}
    		
	        for(Iterator iter = handlerBinding.getDeclaredData().iterator(); iter.hasNext();) {
	        	IDataBinding dBinding = (IDataBinding) iter.next();
	        	ITypeBinding tBinding = dBinding.getType().getBaseType();
        		
	        	validatorFunctionAnnotation = (IAnnotationBinding) dBinding.getAnnotation(new String[] {"egl", "ui"}, IEGLConstants.PROPERTY_VALIDATORFUNCTION);
    			if(validatorFunctionAnnotation != null) {
    				IFunctionBinding validatorFunctionBinding = (IFunctionBinding) validatorFunctionAnnotation.getValue();
    				if(validatorFunctionBinding != IBinding.NOT_FOUND_BINDING && validatorFunctionBinding.isPartBinding()) {
    					validatorFunctions.add(validatorFunctionBinding);
    				}
    			}
    			validatorFunctionAnnotation = (IAnnotationBinding) dBinding.getAnnotation(new String[] {"egl", "ui", "jsf"}, IEGLConstants.PROPERTY_ONVALUECHANGEFUNCTION);
    			if(validatorFunctionAnnotation != null) {
    				IFunctionBinding onValueChangeBinding = (IFunctionBinding) validatorFunctionAnnotation.getValue();
    				if(onValueChangeBinding != IBinding.NOT_FOUND_BINDING && onValueChangeBinding.isPartBinding()) {
    					validatorFunctions.add(onValueChangeBinding);
    				}
    			}
    			
    			validatorFunctionAnnotation = (IAnnotationBinding) dBinding.getAnnotation(new String[] {"egl", "ui", "jsf"}, IEGLConstants.PROPERTY_TYPEAHEADFUNCTION);
    			if(validatorFunctionAnnotation != null) {
    				IFunctionBinding onValueChangeBinding = (IFunctionBinding) validatorFunctionAnnotation.getValue();
    				if(onValueChangeBinding != IBinding.NOT_FOUND_BINDING && onValueChangeBinding.isPartBinding()) {
    					validatorFunctions.add(onValueChangeBinding);
    				}
    			}
	        
	        	addValidatorFunctions(validatorFunctions, tBinding);
	        }
	        
	        for(Iterator iter2 = validatorFunctions.iterator(); iter2.hasNext();) {
				dependencyRequestor.recordTopLevelFunctionBinding((IFunctionBinding) iter2.next());
			}
        }
    }
	
	private void addValidatorFunctions(Set functionBindings, ITypeBinding tBinding) {
		if(tBinding != null && IBinding.NOT_FOUND_BINDING != tBinding) {
			if(ITypeBinding.FIXED_RECORD_BINDING == tBinding.getKind()) {
	    		for(Iterator iter2 = ((FixedRecordBinding) tBinding).getStructureItems().iterator(); iter2.hasNext();) {
	    			addValidatorFunctions(functionBindings, (StructureItemBinding) iter2.next());
	    		}
	    	}
	    	else if(ITypeBinding.FLEXIBLE_RECORD_BINDING == tBinding.getKind()) {
	    		for(Iterator iter2 = ((FlexibleRecordBinding) tBinding).getDeclaredFields().iterator(); iter2.hasNext();) {
	    			addValidatorFunctions(functionBindings, (FlexibleRecordFieldBinding) iter2.next());
	    		}
	    	}
		}
	}

	private void addValidatorFunctions(Set functionBindings, StructureItemBinding sItemBinding) {
		List children = sItemBinding.getChildren();
		if(children.isEmpty()) {
			primAddValidatorFunctions(functionBindings, sItemBinding);
		}
		else {
			for(Iterator iter = children.iterator(); iter.hasNext();) {
				addValidatorFunctions(functionBindings, (StructureItemBinding) iter.next());
			}
		}
	}
    
    private void addValidatorFunctions(Set functionBindings, FlexibleRecordFieldBinding sItemBinding) {
    	primAddValidatorFunctions(functionBindings, sItemBinding);
    	ITypeBinding tBinding = sItemBinding.getType();
    	addValidatorFunctions(functionBindings, tBinding);
	}
    
    private void primAddValidatorFunctions(Set functionBindings, IDataBinding sItemBinding) {
		IAnnotationBinding validatorFunctionAnnotation = sItemBinding.getAnnotation(new String[] {"egl", "ui"}, IEGLConstants.PROPERTY_VALIDATORFUNCTION);
		if(validatorFunctionAnnotation != null) {
			IFunctionBinding validatorFunctionBinding = (IFunctionBinding) validatorFunctionAnnotation.getValue();
			if(validatorFunctionBinding != IBinding.NOT_FOUND_BINDING && validatorFunctionBinding.isPartBinding()) {
				functionBindings.add(validatorFunctionBinding);
			}
		}
		
		validatorFunctionAnnotation = sItemBinding.getAnnotation(new String[] {"egl", "ui", "jsf"}, IEGLConstants.PROPERTY_ONVALUECHANGEFUNCTION);
		if(validatorFunctionAnnotation != null) {
			IFunctionBinding onValueChangeBinding = (IFunctionBinding) validatorFunctionAnnotation.getValue();
			if(onValueChangeBinding != IBinding.NOT_FOUND_BINDING && onValueChangeBinding.isPartBinding()) {
				functionBindings.add(onValueChangeBinding);
			}
		}
		
		validatorFunctionAnnotation = sItemBinding.getAnnotation(new String[] {"egl", "ui", "jsf"}, IEGLConstants.PROPERTY_TYPEAHEADFUNCTION);
		if(validatorFunctionAnnotation != null) {
			IFunctionBinding onValueChangeBinding = (IFunctionBinding) validatorFunctionAnnotation.getValue();
			if(onValueChangeBinding != IBinding.NOT_FOUND_BINDING && onValueChangeBinding.isPartBinding()) {
				functionBindings.add(onValueChangeBinding);
			}
		}
	}
    
    public boolean visit(Constructor constructor) {    	
    	IFunctionBinding functionBinding = (IFunctionBinding) ((ConstructorBinding) constructor.getBinding()).getType();
        if (functionBinding != null) {
            FunctionBinder functionBinder = new FunctionBinder(handlerBinding, functionBinding, currentScope,
                    dependencyRequestor, problemRequestor, compilerOptions);
            constructor.accept(functionBinder);
        }
        return false;
    }

}
