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
import org.eclipse.edt.compiler.internal.core.lookup.NullScope;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


/**
 * @author winghong
 */
public class FunctionBindingCompletor extends AbstractBinder {

    private FunctionMember functionBinding;

    private Part partBinding;

    private IProblemRequestor problemRequestor;

    private Set<String> definedParameters = new HashSet<String>();
    
    public FunctionBindingCompletor(Part partBinding, Scope currentScope, FunctionMember function,
            IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, partBinding, dependencyRequestor, compilerOptions);
        this.partBinding = partBinding;
        this.functionBinding = function;
        this.problemRequestor = problemRequestor;
    }

    public boolean visit(NestedFunction function) {
        function.getName().setMember(functionBinding);

        functionBinding.setIsStatic(function.isStatic());
        functionBinding.setIsAbstract(function.isAbstract());
        if (function.isPrivate()) {
        	functionBinding.setAccessKind(AccessKind.ACC_PRIVATE);
        }

        if (function.hasReturnType()) {
            org.eclipse.edt.mof.egl.Type typeBinding = null;
            try {
                typeBinding = bindType(function.getReturnType());
    	        functionBinding.setType(typeBinding);
    	        functionBinding.setIsNullable(function.getReturnDeclaration().isNullable());
    	        
    	        Field returnField = IrFactory.INSTANCE.createField();
    	        returnField.setContainer(functionBinding);
    	        returnField.setType(typeBinding);
    	        returnField.setIsNullable(function.getReturnDeclaration().isNullable());
    	        ((Function)functionBinding).setReturnField(returnField);
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
                FunctionScope functionScope = new FunctionScope(NullScope.INSTANCE, functionBinding);
                AnnotationLeftHandScope scope = new AnnotationLeftHandScope(functionScope, functionBinding, null, functionBinding);
                functionScope = new FunctionScope(currentScope, functionBinding);
                SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(functionScope, partBinding, scope,
                        dependencyRequestor, problemRequestor, compilerOptions);
                settingsBlock.accept(blockCompletor);
                return false;
            }
        });
    	
    }
    
    public boolean visit(TopLevelFunction function) {
        function.getName().setMember(functionBinding);
        

        if (function.hasReturnType()) {
            org.eclipse.edt.mof.egl.Type typeBinding = null;
            try {
                typeBinding = bindType(function.getReturnType());
            } catch (ResolutionException e) {
                problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
                return false;
            }
            
            functionBinding.setType(typeBinding);
            functionBinding.setIsNullable(function.getReturnDeclaration().isNullable());
        }

        if (function.isPrivate()) {
        	functionBinding.setAccessKind(AccessKind.ACC_PRIVATE);
        }
        
        return true;
    }
    
    public void endVisit(TopLevelFunction function){
        function.accept(new DefaultASTVisitor() {

            public boolean visit(TopLevelFunction function) {
                return true;
            }

            public boolean visit(SettingsBlock settingsBlock) {
                FunctionScope functionScope = new FunctionScope(currentScope, functionBinding);
                AnnotationLeftHandScope scope = new AnnotationLeftHandScope(functionScope, functionBinding, null, functionBinding);
                SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(currentScope, partBinding, scope, 
                        dependencyRequestor, problemRequestor, compilerOptions);
                settingsBlock.accept(blockCompletor);
                return false;
            }
        });
    	BindingUtil.setValid(partBinding, true);
    }
    
    public boolean visit(FunctionParameter functionParameter) {
        String parmName = functionParameter.getName().getIdentifier();
        Type parmType = functionParameter.getType();        
        org.eclipse.edt.mof.egl.Type typeBinding = null;

        org.eclipse.edt.mof.egl.FunctionParameter parm = IrFactory.INSTANCE.createFunctionParameter();
        parm.setContainer(functionBinding);
        parm.setName(functionParameter.getName().getCaseSensitiveIdentifier());
    	functionParameter.getName().setMember(parm);
        
        try {
            typeBinding = bindType(parmType);
            
         } catch (ResolutionException e) {
            problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
            return false;
        }
        
        parm.setType(typeBinding);
        parm.setIsNullable(functionParameter.isNullable());
                
        parm.setIsConst(functionParameter.isParmConst());
        
        FunctionParameter.UseType useType = functionParameter.getUseType();
        if (useType == FunctionParameter.UseType.IN) {
            parm.setParameterKind(ParameterKind.PARM_IN);
        } else if (useType == FunctionParameter.UseType.OUT) {
            parm.setParameterKind(ParameterKind.PARM_OUT);
        } else if (useType == null && typeBinding != null && TypeUtils.isReferenceType(typeBinding)) {
        	parm.setParameterKind(ParameterKind.PARM_IN);
        }
        else {
        	parm.setParameterKind(ParameterKind.PARM_INOUT);
        }

        if (definedParameters.contains(parmName)) {
            problemRequestor.acceptProblem(functionParameter, IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] { parmName,
                    functionBinding.getCaseSensitiveName() });
        } else {
            functionBinding.addParameter(parm);
            definedParameters.add(parmName);
        }

 
        return false;
    }
    
}
