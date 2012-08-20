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

import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.ProgramParameter;
import org.eclipse.edt.compiler.core.ast.Type;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.ResolutionException;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.AccessKind;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Program;
import org.eclipse.edt.mof.egl.StereotypeType;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author winghong
 */
public class ProgramBindingCompletor extends FunctionContainerBindingCompletor {

    private Program programBinding;
 
    public ProgramBindingCompletor(Scope currentScope, IRPartBinding irBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(irBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.programBinding = (Program) irBinding.getIrPart();
    }
    
    public boolean visit(org.eclipse.edt.compiler.core.ast.Program program) {
        program.getName().setType(programBinding);
        program.accept(getPartSubTypeAndAnnotationCollector());
        
        if (program.isPrivate()) {
        	programBinding.setAccessKind(AccessKind.ACC_PRIVATE);
        }
        programBinding.setIsCallable(program.isCallable());
                
        setDefaultSuperType();
        return true;
    }  
    
	public void endVisit(org.eclipse.edt.compiler.core.ast.Program program) {
		/*
		 * Process the program parameters after the rest of the binding has
		 * been completed, since some of the types may be forms declared in
		 * the main form group which we may not know about otherwise.
		 */
		
		
		for (ProgramParameter programParameter : program.getParameters()) {
			Name parameterNameNode = programParameter.getName();;
	        String parameterName = parameterNameNode.getIdentifier();
	        Type parameterType = programParameter.getType();
	        
	        org.eclipse.edt.mof.egl.Type typeBinding = null;
	        try {
	            typeBinding = bindType(parameterType);
	        } catch (ResolutionException e) {
		        	problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
		            continue;
	        }
	        	        
	        
	        org.eclipse.edt.mof.egl.ProgramParameter parameterBinding = IrFactory.INSTANCE.createProgramParameter();
	        parameterBinding.setType(typeBinding);
	        parameterBinding.setName(parameterNameNode.getCaseSensitiveIdentifier());
	        parameterBinding.setIsNullable(programParameter.isNullable());
	        parameterBinding.setAccessKind(AccessKind.ACC_PRIVATE);
	        
	        if(definedDataNames.contains(parameterName)) {
	        	problemRequestor.acceptProblem(
	        		parameterNameNode,
					IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS,
					new String[] {parameterNameNode.getCanonicalName(), programBinding.getCaseSensitiveName()});
	        }
	        else {
	        	programBinding.getParameters().add(parameterBinding);
	        	definedDataNames.add(parameterName);
	        }
	        
	        parameterNameNode.setMember(parameterBinding);
	        parameterNameNode.setType(parameterBinding.getType());
		}
        processSettingsBlocks();
		endVisitFunctionContainer(program);
    }
	
    protected StereotypeType getDefaultStereotypeType() {
    	try {
    		return (StereotypeType)BindingUtil.getAnnotationType(NameUtile.getAsName("eglx.lang"), NameUtile.getAsName("BasicProgram"));
    	}
    	catch(ClassCastException e) {
    		return null;
    	}
    }
    
    @Override
    protected boolean membersPrivateByDefault() {
    	return true;
    }
    
}
