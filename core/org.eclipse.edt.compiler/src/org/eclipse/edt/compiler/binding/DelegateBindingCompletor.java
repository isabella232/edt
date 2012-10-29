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
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AbstractBinder;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationLeftHandScope;
import org.eclipse.edt.compiler.internal.core.lookup.DelegateScope;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.NullScope;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


/**
 * @author winghong
 */
public class DelegateBindingCompletor extends AbstractBinder {

    private org.eclipse.edt.mof.egl.Delegate delegateBinding;
    private IProblemRequestor problemRequestor;
    private Set<String> definedParameters = new HashSet<String>();

    public DelegateBindingCompletor(Scope currentScope, IRPartBinding irBinding,
            IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(currentScope, irBinding.getIrPart(), dependencyRequestor, compilerOptions);
        this.delegateBinding = (org.eclipse.edt.mof.egl.Delegate)irBinding.getIrPart();
        this.problemRequestor = problemRequestor;
    }

    public boolean visit(Delegate delegate) {
        delegate.getName().setType(delegateBinding);

        if (delegate.hasReturnType()) {
            Type typeBinding = null;
            try {
                typeBinding = bindType(delegate.getReturnType());
                
	            delegateBinding.setReturnType(typeBinding);
	            delegateBinding.setIsNullable(delegate.getReturnDeclaration().isNullable());//TODO JV is this correct?
            } catch (ResolutionException e) {
                problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());                
            }
        }

        return true;
    }
    
    public void endVisit(Delegate delegate) {
    	BindingUtil.setValid(delegateBinding, true);
    }
    
    public boolean visit(FunctionParameter functionParameter) {
        
        
		org.eclipse.edt.mof.egl.FunctionParameter parm = IrFactory.INSTANCE.createFunctionParameter();
		parm.setContainer(delegateBinding);
        parm.setName(functionParameter.getName().getCaseSensitiveIdentifier());
    	functionParameter.getName().setMember(parm);
        
    	Type typeBinding;
        try {
            typeBinding = bindType(functionParameter.getType());
            
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
        } else {
        	parm.setParameterKind(ParameterKind.PARM_INOUT);
        }

        if (definedParameters.contains(parm.getName())) {
            problemRequestor.acceptProblem(functionParameter, IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS, new String[] { parm.getName(), delegateBinding.getCaseSensitiveName() });
        } else {
            delegateBinding.getParameters().add(parm);
            definedParameters.add(parm.getName());
        }
        
        return false;
    }

    public boolean visit(SettingsBlock settingsBlock) {
    	DelegateScope delScope = new DelegateScope(NullScope.INSTANCE, delegateBinding);
        AnnotationLeftHandScope scope = new AnnotationLeftHandScope(delScope, delegateBinding, delegateBinding, delegateBinding);
        SettingsBlockAnnotationBindingsCompletor blockCompletor = new SettingsBlockAnnotationBindingsCompletor(currentScope, delegateBinding, scope, dependencyRequestor, problemRequestor, compilerOptions);
        settingsBlock.accept(blockCompletor);
        return false;
    }
}
