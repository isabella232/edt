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

import java.util.Iterator;
import java.util.Set;

import org.eclipse.edt.compiler.binding.annotationType.AnnotationTypeBindingImpl;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.core.ast.NameType;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.ProgramParameter;
import org.eclipse.edt.compiler.core.ast.Type;
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
public class ProgramBindingCompletor extends FunctionContainerBindingCompletor {

    private ProgramBinding programBinding;
    private IProblemRequestor problemRequestor;
    private String canonicalProgramName;

    public ProgramBindingCompletor(Scope currentScope, ProgramBinding programBinding, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions) {
        super(programBinding, currentScope, dependencyRequestor, problemRequestor, compilerOptions);
        this.programBinding = programBinding;
        this.problemRequestor = problemRequestor;
    }
    
    public boolean visit(Program program) {
    	canonicalProgramName = program.getName().getCanonicalName();
        program.getName().setBinding(programBinding);
        program.accept(getPartSubTypeAndAnnotationCollector());
        
        programBinding.setPrivate(program.isPrivate());
        programBinding.setCallable(program.isCallable());
        
        addImplicitFieldsFromAnnotations();
        
        return true;
    }  
    
	public void endVisit(Program program) {
		/*
		 * Process the program parameters after the rest of the binding has
		 * been completed, since some of the types may be forms declared in
		 * the main form group which we may not know about otherwise.
		 */
		for(Iterator iter = program.getParameters().iterator(); iter.hasNext();) {
			ProgramParameter programParameter = (ProgramParameter) iter.next();
			Name parameterNameNode = programParameter.getName();
	        String parameterName = parameterNameNode.getIdentifier();
	        Type parameterType = programParameter.getType();
	        
	        ITypeBinding typeBinding = null;
	        try {
	            typeBinding = bindType(parameterType);
	        } catch (ResolutionException e) {
	        	Type baseType = parameterType.getBaseType();
				IDataBinding formDB = baseType.isNameType() ? getForm(((NameType) baseType).getName().getIdentifier(), usedForms) : null;
	        	if(formDB != null && IBinding.NOT_FOUND_BINDING != formDB && usedForms.contains(formDB.getType().getBaseType())) {
	        		typeBinding = formDB.getType();
	        		((NameType) baseType).getName().setBinding(typeBinding);
	        		
	        		if(parameterType.isArrayType()) {
	        			problemRequestor.acceptProblem(
	        				parameterType,
							IProblemRequestor.PROGRAM_PARAMTER_NO_FORM_ARRAY,
							new String[] {
	        					parameterNameNode.getCanonicalName(),
								canonicalProgramName
	        				});
	        		}
	        	}
	        	else {
		        	problemRequestor.acceptProblem(e.getStartOffset(), e.getEndOffset(), IMarker.SEVERITY_ERROR, e.getProblemKind(), e.getInserts());
		            continue;
	        	}
	        }
	        
            if(typeBinding.isNullable()) {
            	problemRequestor.acceptProblem(programParameter.getType(), IProblemRequestor.NULLABLE_TYPE_NOT_ALLOWED_IN_PROGRAM_PARAMETER);
            	typeBinding = typeBinding.getBaseType();
            }
	        
	        ProgramParameterBinding parameterBinding = new ProgramParameterBinding(parameterNameNode.getCaseSensitiveIdentifier(), programBinding, typeBinding);
	        
	        if(definedDataNames.contains(parameterName)) {
	        	problemRequestor.acceptProblem(
	        		parameterNameNode,
					IProblemRequestor.DUPLICATE_NAME_ACROSS_LISTS,
					new String[] {parameterNameNode.getCanonicalName(), programBinding.getCaseSensitiveName()});
	        }
	        else {
	        	programBinding.addParameter(parameterBinding);
	        	definedDataNames.add(parameterName);
	        }
	        
	        parameterNameNode.setBinding(parameterBinding);
		}
        processSettingsBlocks();
		endVisitFunctionContainer(program);
    }
	
    protected IPartSubTypeAnnotationTypeBinding getDefaultSubType() {
    	try {
    		return new AnnotationTypeBindingImpl((FlexibleRecordBinding) currentScope.findPackage(InternUtil.intern("eglx")).resolvePackage(InternUtil.intern("lang")).resolveType(InternUtil.intern("BasicProgram")), programBinding);
    	}
    	catch(UnsupportedOperationException e) {
    		return null;
    	}
    	catch(ClassCastException e) {
    		return null;
    	}
    }
    
	private IDataBinding getForm(String name, Set usedForms) {
		for(Iterator iter = usedForms.iterator(); iter.hasNext();) {
			IBinding next = (IBinding) iter.next();
			if(next.getName() == name) {
				return ((FormBinding) next).getStaticFormDataBinding();
			}
		}
		return null;
	}
}
