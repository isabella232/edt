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

import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.FunctionParameter;
import org.eclipse.edt.compiler.core.ast.SettingsBlock;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;


/**
 * @author winghong
 */
public class DelegateBindingCompletor extends AbstractBinder {

    private DelegateBinding delegateBinding;
    private IProblemRequestor problemRequestor;
    private Set definedParameters = new HashSet();
	private String canonicalDelegateName;

    public DelegateBindingCompletor(Scope currentScope, DelegateBinding delegateBinding,
            IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, delegateBinding, dependencyRequestor, compilerOptions);
        this.delegateBinding = delegateBinding;
        this.problemRequestor = problemRequestor;
    }

    public boolean visit(Delegate delegate) {
        delegate.getName().setBinding(delegateBinding);
        canonicalDelegateName = delegate.getName().getCanonicalName();

        delegateBinding.setPrivate(delegate.isPrivate());

        if (delegate.hasReturnType()) {
            ITypeBinding typeBinding = null;
            try {
                typeBinding = bindType(delegate.getReturnType());
                
                if(checkReturnType(delegate.getReturnType(), typeBinding)) {
    	            delegateBinding.setReturnType(typeBinding);
    	            delegateBinding.setReturnTypeIsSqlNullable(delegate.returnTypeIsSqlNullable());
                }
            } catch (ResolutionException e) {
                problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());                
            }
        }

        return true;
    }
    
    public void endVisit(Delegate delegate) {
    	delegateBinding.setValid(true);
    }
    
    private boolean checkReturnType(Type type, ITypeBinding typeBinding) {
        if(!typeBinding.isReference() &&
           ITypeBinding.DATAITEM_BINDING != typeBinding.getKind() &&
           ITypeBinding.PRIMITIVE_TYPE_BINDING != typeBinding.getKind() &&
           ITypeBinding.FIXED_RECORD_BINDING != typeBinding.getKind() &&
           ITypeBinding.FLEXIBLE_RECORD_BINDING != typeBinding.getKind() &&
           ITypeBinding.ENUMERATION_BINDING != typeBinding.getKind() &&
		   typeBinding != DictionaryBinding.INSTANCE &&
		   typeBinding != ArrayDictionaryBinding.INSTANCE) {
        	problemRequestor.acceptProblem(
        		type,
				IProblemRequestor.FUNCTION_RETURN_HAS_INCORRECT_TYPE,
				new String[] {type.getCanonicalName(), canonicalDelegateName});
        	return false;
        }
        return true;
    }

    public boolean visit(FunctionParameter functionParameter) {
        String parmName = functionParameter.getName().getIdentifier();
        Type parmType = functionParameter.getType();        
        ITypeBinding typeBinding = null;
        try {
            typeBinding = bindType(parmType);
        } catch (ResolutionException e) {
        	functionParameter.getName().setBinding(new FunctionParameterBinding(functionParameter.getName().getCaseSensitiveIdentifier(), delegateBinding, IBinding.NOT_FOUND_BINDING, null));
            problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
            return false;
        }
        
        FunctionParameterBinding funcParmBinding = new FunctionParameterBinding(functionParameter.getName().getCaseSensitiveIdentifier(), delegateBinding, typeBinding, null);
        functionParameter.getName().setBinding(funcParmBinding);
        
        if(!BindingUtilities.isValidDeclarationType(typeBinding)) {
        	problemRequestor.acceptProblem(
        		parmType,
        		IProblemRequestor.FUNCTION_PARAMETER_HAS_INCORRECT_TYPE,
				new String[] {functionParameter.getName().getCanonicalName(), canonicalDelegateName});
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
            problemRequestor.acceptProblem(functionParameter, IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] { parmName, canonicalDelegateName });
        } else {
            delegateBinding.addParameter(funcParmBinding);
            definedParameters.add(parmName);
        }
        
        return false;
    }
    
    public boolean visit(SettingsBlock settingsBlock) {
        AnnotationLeftHandScope scope = new AnnotationLeftHandScope(currentScope, delegateBinding, delegateBinding, delegateBinding, -1, delegateBinding);
        SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(currentScope, delegateBinding, scope, dependencyRequestor, problemRequestor, compilerOptions);
        settingsBlock.accept(blockCompletor);
        return false;
    }    

}
