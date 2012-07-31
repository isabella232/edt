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
import java.util.Iterator;
import java.util.Set;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.Constructor;
import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
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


/**
 * @author winghong
 */
public class EGLClassBindingCompletor extends FunctionContainerBindingCompletor {

    private EGLClassBinding classBinding;

    public EGLClassBindingCompletor(Scope currentScope, EGLClassBinding classBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(classBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.classBinding = classBinding;
    }
        
    public boolean visit(EGLClass eglClass) {
    	eglClass.getName().setBinding(classBinding);
    	
    	classBinding.setPrivate(eglClass.isPrivate());
    	
        PartSubTypeAndAnnotationCollector col = getPartSubTypeAndAnnotationCollector();
		eglClass.accept(col);
				
		if (eglClass.getExtends() != null) {
    		try {
    			ITypeBinding typeBinding = bindTypeName(eglClass.getExtends());
    			if (typeBinding instanceof EGLClassBinding) {
    				classBinding.setExtends((EGLClassBinding)typeBinding);
    			}
    		}
        	catch (ResolutionException e) {
        		problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
        	}
 		}
		
        for(Iterator iter = eglClass.getImplementedInterfaces().iterator(); iter.hasNext();) {
    		Name nextName = (Name) iter.next();
    		try {
    			ITypeBinding typeBinding = bindTypeName(nextName);
    			//TODO should probably check to see if this is an interfaceBinding before adding it
    			classBinding.addExtenedInterface(typeBinding);
    			
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
    
    public boolean visit(Constructor constructor) {
    	final ConstructorBinding constructorBinding = new ConstructorBinding(classBinding);
    	final Set definedParameters = new HashSet();
    	
    	constructor.setBinding(constructorBinding);
    	
    	constructorBinding.setPrivate(constructor.isPrivate());
    	
    	constructor.accept(new AbstractASTVisitor() {
    		public boolean visit(FunctionParameter functionParameter) {
    			String parmName = functionParameter.getName().getIdentifier();
    	        Type parmType = functionParameter.getType();        
    	        ITypeBinding typeBinding = null;
    	        try {
    	            typeBinding = bindType(parmType);
    	        } catch (ResolutionException e) {
    	        	functionParameter.getName().setBinding(new FunctionParameterBinding(functionParameter.getName().getCaseSensitiveIdentifier(), classBinding, IBinding.NOT_FOUND_BINDING, (IFunctionBinding) constructorBinding.getType()));
    	            problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
    	            return false;
    	        }
    	        
    	        FunctionParameterBinding funcParmBinding = new FunctionParameterBinding(functionParameter.getName().getCaseSensitiveIdentifier(), classBinding, typeBinding, (IFunctionBinding) constructorBinding.getType());
    	        functionParameter.getName().setBinding(funcParmBinding);
    	        
    	        if(!BindingUtilities.isValidDeclarationType(typeBinding)) {
    	        	problemRequestor.acceptProblem(
    	        		parmType,
    	        		IProblemRequestor.FUNCTION_PARAMETER_HAS_INCORRECT_TYPE,
    					new String[] {functionParameter.getName().getCanonicalName(), IEGLConstants.KEYWORD_CONSTRUCTOR});
    	        	return false;				
    	        }
    	        
    	        FunctionParameter.AttrType attrType = functionParameter.getAttrType();
    	        if (attrType == FunctionParameter.AttrType.FIELD) {
    	            funcParmBinding.setField(true);
    	        } else if (attrType == FunctionParameter.AttrType.SQLNULLABLE) {
    	            funcParmBinding.setSqlNullable(true);
    	        }
    	        FunctionParameter.UseType useType = functionParameter.getUseType();
    	        if (useType == FunctionParameter.UseType.IN) {
    	            funcParmBinding.setInput(true);
    	        } else if (useType == FunctionParameter.UseType.OUT) {
    	            funcParmBinding.setOutput(true);
    	        } else if (useType == null && Binding.isValidBinding(typeBinding) && typeBinding.isReference()) {
    	            funcParmBinding.setInput(true);
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
    	
    	classBinding.addConstructor(constructorBinding);
    	
    	if (constructor.hasSettingsBlock()) {
            FunctionScope functionScope = new FunctionScope(currentScope, (FunctionBinding)constructorBinding.getType());
            AnnotationLeftHandScope scope = new AnnotationLeftHandScope(functionScope, constructorBinding, null, constructorBinding, -1, classBinding);
            SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(functionScope, classBinding, scope,
                    dependencyRequestor, problemRequestor, compilerOptions);
            constructor.getSettingsBlock().accept(blockCompletor);
    		
    	}
    	
    	return false;
    }

    
    public void endVisit(EGLClass eglClass) {
        processSettingsBlocks();
		if(classBinding.getConstructors().isEmpty()) {
			//Add default constructor
			classBinding.addConstructor(new ConstructorBinding(classBinding));
		}
        endVisitFunctionContainer(eglClass);
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
}
