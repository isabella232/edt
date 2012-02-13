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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.edt.compiler.core.ast.ConverseStatement;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.NestedFunction;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;


/**
 * @author winghong
 */
public class FunctionBindingCompletor extends AbstractBinder {

    private FunctionBinding functionBinding;

    private IPartBinding partBinding;

    private IProblemRequestor problemRequestor;

    private Set definedParameters = new HashSet();
    
    private String canonicalFunctionName;

    public FunctionBindingCompletor(IPartBinding partBinding, Scope currentScope, FunctionBinding functionBinding,
            IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, partBinding, dependencyRequestor, compilerOptions);
        this.partBinding = partBinding;
        this.functionBinding = functionBinding;
        this.problemRequestor = problemRequestor;
    }

    public boolean visit(NestedFunction function) {
        function.getName().setBinding(functionBinding);
        canonicalFunctionName = function.getName().getCanonicalName();

        functionBinding.setStatic(function.isStatic());
        functionBinding.setAbstract(function.isAbstract());
        functionBinding.setPrivate(function.isPrivate());

        if (function.hasReturnType()) {
            ITypeBinding typeBinding = null;
            try {
                typeBinding = bindType(function.getReturnType());
                
                if(checkReturnType(function.getReturnType(), typeBinding)) {
                	IAnnotationBinding aBinding = partBinding.getAnnotation(new String[] {"egl", "core"}, "I4GLItemsNullable");
                	
    	            functionBinding.setReturnType(typeBinding);
    	            functionBinding.setReturnTypeIsSqlNullable(function.returnTypeIsSqlNullable());
                }
            } catch (ResolutionException e) {
                problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());                
            }
        }

        return true;
    }
    
    public void endVisit(NestedFunction function) {
        function.accept(new DefaultASTVisitor() {
            public boolean visit(NestedFunction function) {
                return true;
            }

            public boolean visit(SettingsBlock settingsBlock) {
                FunctionScope functionScope = new FunctionScope(currentScope, functionBinding);
                AnnotationLeftHandScope scope = new AnnotationLeftHandScope(functionScope, functionBinding, null, functionBinding, -1, functionBinding.getDeclarer());
                SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(functionScope, functionBinding.getDeclarer(), scope,
                        dependencyRequestor, problemRequestor, compilerOptions);
                settingsBlock.accept(blockCompletor);
                return false;
            }
        });
    	
    }


    public boolean visit(TopLevelFunction function) {
        function.getName().setBinding(functionBinding);
        canonicalFunctionName = function.getName().getCanonicalName();
        
        if (function.hasReturnType()) {
            ITypeBinding typeBinding = null;
            try {
                typeBinding = bindType(function.getReturnType());
            } catch (ResolutionException e) {
                problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
                return false;
            }
            
            if(checkReturnType(function.getReturnType(), typeBinding)) {
            	functionBinding.setReturnType(typeBinding);
            	functionBinding.setReturnTypeIsSqlNullable(function.returnTypeIsSqlNullable());
            }
        }

        functionBinding.setPrivate(function.isPrivate());

        return true;
    }
    
    public void endVisit(TopLevelFunction function){
    	
        function.accept(new DefaultASTVisitor() {

            public boolean visit(TopLevelFunction function) {
                return true;
            }

            public boolean visit(SettingsBlock settingsBlock) {
              FunctionScope functionScope = new FunctionScope(currentScope, functionBinding);
              AnnotationLeftHandScope scope = new AnnotationLeftHandScope(functionScope, functionBinding, null, functionBinding, -1, functionBinding.getDeclarer());
                SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(functionScope, functionBinding.getDeclarer(), scope, 
                        dependencyRequestor, problemRequestor, compilerOptions);
                settingsBlock.accept(blockCompletor);
                return false;
            }
        });
    	
        ((IPartBinding)functionBinding).setValid(true);
    }
    
    private boolean checkReturnType(Type type, ITypeBinding typeBinding) {
        if(!typeBinding.isReference() &&
           ITypeBinding.DATAITEM_BINDING != typeBinding.getKind() &&
           ITypeBinding.PRIMITIVE_TYPE_BINDING != typeBinding.getKind() &&
           ITypeBinding.FLEXIBLE_RECORD_BINDING != typeBinding.getKind() &&
           ITypeBinding.FIXED_RECORD_BINDING != typeBinding.getKind() &&
           ITypeBinding.ENUMERATION_BINDING != typeBinding.getKind() &&
		   typeBinding != DictionaryBinding.INSTANCE &&
		   typeBinding != ArrayDictionaryBinding.INSTANCE) {
        	problemRequestor.acceptProblem(
        		type,
				IProblemRequestor.FUNCTION_RETURN_HAS_INCORRECT_TYPE,
				new String[] {type.getCanonicalName(), canonicalFunctionName});
        	return false;
        }
        if(partBinding != null && partBinding.getAnnotation(new String[] {"egl", "ui", "jasper"}, "JasperReport") != null) {
        	if(ITypeBinding.DATAITEM_BINDING != typeBinding.getKind() &&
	           ITypeBinding.PRIMITIVE_TYPE_BINDING != typeBinding.getKind()) {
        		problemRequestor.acceptProblem(
            		type,
    				IProblemRequestor.ONLY_DATAITEMS_ALLOWED_AS_PARAMETER_OR_RETURN_IN_REPORTHANDLER);
        	}
        }
        return true;
    }

    public boolean visit(FunctionParameter functionParameter) {
        String parmName = functionParameter.getName().getIdentifier();
        Type parmType = functionParameter.getType();        
        ITypeBinding typeBinding = null;
        try {
            typeBinding = bindType(parmType);
            if(currentScope.I4GLItemsNullableIsEnabled()) {
            	if(ITypeBinding.PRIMITIVE_TYPE_BINDING == typeBinding.getBaseType().getKind()) {
            		typeBinding = typeBinding.getNullableInstance();
            	}
            }
        } catch (ResolutionException e) {
        	functionParameter.getName().setBinding(new FunctionParameterBinding(functionParameter.getName().getCaseSensitiveIdentifier(), partBinding, IBinding.NOT_FOUND_BINDING, functionBinding));
            problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
            return false;
        }
        
        FunctionParameterBinding funcParmBinding = new FunctionParameterBinding(functionParameter.getName().getCaseSensitiveIdentifier(), partBinding, typeBinding, functionBinding);
        functionParameter.getName().setBinding(funcParmBinding);
        
        if(!BindingUtilities.isValidDeclarationType(typeBinding)) {
        	problemRequestor.acceptProblem(
        		parmType,
        		IProblemRequestor.FUNCTION_PARAMETER_HAS_INCORRECT_TYPE,
				new String[] {functionParameter.getName().getCanonicalName(), canonicalFunctionName});
        	return false;				
        }
        
        FunctionParameter.AttrType attrType = functionParameter.getAttrType();
        if (attrType == FunctionParameter.AttrType.FIELD) {
            funcParmBinding.setField(true);
        } else if (attrType == FunctionParameter.AttrType.SQLNULLABLE) {
            funcParmBinding.setSqlNullable(true);
        }
        
        funcParmBinding.setConst(functionParameter.isParmConst());
        
        FunctionParameter.UseType useType = functionParameter.getUseType();
        if (useType == FunctionParameter.UseType.IN) {
            funcParmBinding.setInput(true);
        } else if (useType == FunctionParameter.UseType.OUT) {
            funcParmBinding.setOutput(true);
        } else if (useType == null && Binding.isValidBinding(typeBinding) && typeBinding.isReference()) {
            funcParmBinding.setInput(true);
        }

        if (definedParameters.contains(parmName)) {
            problemRequestor.acceptProblem(functionParameter, IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] { parmName,
                    functionBinding.getCaseSensitiveName() });
        } else {
            functionBinding.addParameter(funcParmBinding);
            definedParameters.add(parmName);
        }

 
        return false;
    }
    
    public boolean visit(ConverseStatement converseStatement) {
    	functionBinding.setHasConverse(true);
    	return false;
    }

}
