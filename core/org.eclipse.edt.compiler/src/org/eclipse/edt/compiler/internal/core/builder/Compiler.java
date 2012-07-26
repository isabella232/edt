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
package org.eclipse.edt.compiler.internal.core.builder;

import org.eclipse.edt.compiler.PartValidator;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.EGLClassBinder;
import org.eclipse.edt.compiler.internal.core.lookup.FileBinder;
import org.eclipse.edt.compiler.internal.core.lookup.HandlerBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.InterfaceBinder;
import org.eclipse.edt.compiler.internal.core.lookup.LibraryBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ProgramBinder;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.lookup.ServiceBinder;


/**
 * @author winghong
 */
public abstract class Compiler extends DefaultASTVisitor{
	
	public synchronized void compilePart(Node astNode, IPartBinding partBinding, Scope parentScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
	    switch(partBinding.getKind()){
			case ITypeBinding.FILE_BINDING:
				try{
				    astNode.accept(new FileBinder((FileBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
				}catch(CancelledException e){
				    throw e;
				}catch(CircularBuildRequestException e){
				    throw e;
				}catch(BuildException e){
				    throw e;
				}catch(RuntimeException e){
				    if(!partBinding.isValid()){
				        throw e;
				    }
				    problemRequestor.acceptProblem(0, 0, IMarker.SEVERITY_ERROR, IProblemRequestor.COMPILATION_EXCEPTION, new String[]{partBinding.getName()});
				    logPartBinderException(e);
				}
				break;
			case ITypeBinding.PROGRAM_BINDING:
				try{
				    astNode.accept(new ProgramBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
				    
				    //TODO add code to handle circular buildexception
				    try{
				    	PartValidator validator = partBinding.getEnvironment().getCompiler().getValidatorFor((Part)astNode);
				    	if (validator != null) {
				    		validator.validatePart((Part)astNode, (IRPartBinding)partBinding, problemRequestor, compilerOptions);
				    	}
					}catch(CancelledException e){
					    throw e;
					}catch(RuntimeException e){
					    handleValidationException((Part)astNode, problemRequestor, e);
					}
				}catch(CancelledException  e){
				    throw e;
				}catch(CircularBuildRequestException e){
				    throw e;
				}catch(BuildException e){
				    throw e;
				}catch(RuntimeException e){
				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
				}
				break;
//			case ITypeBinding.DELEGATE_BINDING:
//				try{
//				    astNode.accept(new DelegateBinder((DelegateBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
//				    
//				    try{
//					    astNode.accept(new DelegateValidator(problemRequestor, (DelegateBinding)partBinding, compilerOptions));
//					}catch(CancelledException e){
//					    throw e;
//					}catch(RuntimeException e){
//					    handleValidationException((Part)astNode, problemRequestor, e);
//					}
//				}catch(CancelledException  e){
//				    throw e;
//				}catch(CircularBuildRequestException e){
//				    throw e;
//				}catch(BuildException e){
//				    throw e;
//				}catch(RuntimeException e){
//				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
//				}
//				break;
//			case ITypeBinding.EXTERNALTYPE_BINDING:
//				try{
//				    astNode.accept(new ExternalTypeBinder((ExternalTypeBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
//				    
//				    try{
//					    astNode.accept(new ExternalTypeValidator(problemRequestor, (ExternalTypeBinding)partBinding, compilerOptions));
//					}catch(CancelledException e){
//					    throw e;
//					}catch(RuntimeException e){
//					    handleValidationException((Part)astNode, problemRequestor, e);
//					}
//				}catch(CancelledException  e){
//				    throw e;
//				}catch(CircularBuildRequestException e){
//				    throw e;
//				}catch(BuildException e){
//				    throw e;
//				}catch(RuntimeException e){
//				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
//				}
//				break;
//			case ITypeBinding.ENUMERATION_BINDING:
//				try{
//				    astNode.accept(new EnumerationBinder((EnumerationTypeBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
//				    
//				    try{
//					    astNode.accept(new EnumerationValidator(problemRequestor, compilerOptions));
//					}catch(CancelledException e){
//					    throw e;
//					}catch(RuntimeException e){
//					    handleValidationException((Part)astNode, problemRequestor, e);
//					}
//				}catch(CancelledException  e){
//				    throw e;
//				}catch(CircularBuildRequestException e){
//				    throw e;
//				}catch(BuildException e){
//				    throw e;
//				}catch(RuntimeException e){
//				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
//				}
//				break;
//			case ITypeBinding.FUNCTION_BINDING:
//				try{
//				    astNode.accept(new FunctionBinder((TopLevelFunctionBinding)partBinding, (TopLevelFunctionBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
//				
//					try{
//					    astNode.accept(new FunctionValidator(problemRequestor, compilerOptions));
//					}catch(CancelledException e){
//					    throw e;
//					}catch(RuntimeException e){
//					    handleValidationException((Part)astNode, problemRequestor, e);
//					}
//				}catch(CancelledException  e){
//				    throw e;
//				}catch(CircularBuildRequestException e){
//				    throw e;
//				}catch(BuildException e){
//				    throw e;
//				}catch(RuntimeException e){
//				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
//				}
//				break;
//			case ITypeBinding.DATAITEM_BINDING:
//				try{
//				    astNode.accept(new DataItemBinder((DataItemBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
//					
//					try{
//					    astNode.accept(new DataItemValidator(problemRequestor, compilerOptions));
//					}catch(CancelledException e){
//					    throw e;
//					}catch(RuntimeException e){
//					    handleValidationException((Part)astNode, problemRequestor, e);
//					}
//				}catch(CancelledException  e){
//				    throw e;
//				}catch(CircularBuildRequestException e){
//				    throw e;
//				}catch(BuildException e){
//				    throw e;
//				}catch(RuntimeException e){
//				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
//				}
//				break;
//			case ITypeBinding.DATATABLE_BINDING:
//				try{
//				    astNode.accept(new DataTableBinder((DataTableBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
//					
//					try{
//					    astNode.accept(new DataTableValidator(problemRequestor, compilerOptions));
//					}catch(CancelledException e){
//					    throw e;
//					}catch(RuntimeException e){
//					    handleValidationException((Part)astNode, problemRequestor, e);
//					}
//				}catch(CancelledException  e){
//				    throw e;
//				}catch(CircularBuildRequestException e){
//				    throw e;
//				}catch(BuildException e){
//				    throw e;
//				}catch(RuntimeException e){
//				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
//				}
//				break;
//			case ITypeBinding.FORMGROUP_BINDING:
//				try{
//				    astNode.accept(new FormGroupBinder((FormGroupBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
//				
//					try{
//					    astNode.accept(new FormGroupValidator(problemRequestor, compilerOptions));
//					}catch(CancelledException e){
//					    throw e;
//					}catch(RuntimeException e){
//					    handleValidationException((Part)astNode, problemRequestor, e);
//					}
//				}catch(CancelledException  e){
//				    throw e;
//				}catch(CircularBuildRequestException e){
//				    throw e;
//				}catch(BuildException e){
//				    throw e;
//				}catch(RuntimeException e){
//				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
//				}
//				break;
			case ITypeBinding.HANDLER_BINDING:
				try{
				    astNode.accept(new HandlerBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
								
				    //TODO add code to handle circular buildexception
					try{
						PartValidator validator = partBinding.getEnvironment().getCompiler().getValidatorFor((Part)astNode);
				    	if (validator != null) {
				    		validator.validatePart((Part)astNode, (IRPartBinding)partBinding, problemRequestor, compilerOptions);
				    	}
					}catch(CancelledException e){
					    throw e;
					}catch(RuntimeException e){
					    handleValidationException((Part)astNode, problemRequestor, e);
					}
				}catch(CancelledException  e){
				    throw e;
				}catch(CircularBuildRequestException e){
				    throw e;
				}catch(BuildException e){
				    throw e;
				}catch(RuntimeException e){
				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
				}
				break;
			case ITypeBinding.CLASS_BINDING:
				try{
				    astNode.accept(new EGLClassBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
								
				    //TODO add code to handle circular buildexception
					try{
						PartValidator validator = partBinding.getEnvironment().getCompiler().getValidatorFor((Part)astNode);
				    	if (validator != null) {
				    		validator.validatePart((Part)astNode, (IRPartBinding)partBinding, problemRequestor, compilerOptions);
				    	}
					}catch(CancelledException e){
					    throw e;
					}catch(RuntimeException e){
					    handleValidationException((Part)astNode, problemRequestor, e);
					}
				}catch(CancelledException  e){
				    throw e;
				}catch(CircularBuildRequestException e){
				    throw e;
				}catch(BuildException e){
				    throw e;
				}catch(RuntimeException e){
				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
				}
				break;
			case ITypeBinding.INTERFACE_BINDING:
				try{
					astNode.accept(new InterfaceBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
				    
				    //TODO add code to handle circular buildexception
				    try{
				    	PartValidator validator = partBinding.getEnvironment().getCompiler().getValidatorFor((Part)astNode);
				    	if (validator != null) {
				    		validator.validatePart((Part)astNode, (IRPartBinding)partBinding, problemRequestor, compilerOptions);
				    	}
					}catch(CancelledException e){
					    throw e;
					}catch(RuntimeException e){
					    handleValidationException((Part)astNode, problemRequestor, e);
					}
				}catch(CancelledException  e){
				    throw e;
				}catch(CircularBuildRequestException e){
				    throw e;
				}catch(BuildException e){
				    throw e;
				}catch(RuntimeException e){
				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
				}
				break;
			case ITypeBinding.LIBRARY_BINDING:
				try{
					astNode.accept(new LibraryBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
				    
				    //TODO add code to handle circular buildexception
				    try{
				    	PartValidator validator = partBinding.getEnvironment().getCompiler().getValidatorFor((Part)astNode);
				    	if (validator != null) {
				    		validator.validatePart((Part)astNode, (IRPartBinding)partBinding, problemRequestor, compilerOptions);
				    	}
					}catch(CancelledException e){
					    throw e;
					}catch(RuntimeException e){
					    handleValidationException((Part)astNode, problemRequestor, e);
					}
				}catch(CancelledException  e){
				    throw e;
				}catch(CircularBuildRequestException e){
				    throw e;
				}catch(BuildException e){
				    throw e;
				}catch(RuntimeException e){
				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
				}
				break;
//			case ITypeBinding.FLEXIBLE_RECORD_BINDING:
//				try{
//				    astNode.accept(new FlexibleRecordBinder((FlexibleRecordBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
//								
//					try{
//					    astNode.accept(new FlexibleRecordValidator(problemRequestor, compilerOptions));
//					}catch(CancelledException e){
//					    throw e;
//					}catch(RuntimeException e){
//					    handleValidationException((Part)astNode, problemRequestor, e);
//					}
//				}catch(CancelledException  e){
//				    throw e;
//				}catch(CircularBuildRequestException e){
//				    throw e;
//				}catch(BuildException e){
//				    throw e;
//				}catch(RuntimeException e){
//				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
//				}
//				break;
//			case ITypeBinding.FIXED_RECORD_BINDING:
//				try{
//				    astNode.accept(new FixedRecordBinder((FixedRecordBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
//								
//					try{
//					    astNode.accept(new FixedRecordValidator(problemRequestor, compilerOptions));
//					}catch(CancelledException e){
//					    throw e;
//					}catch(RuntimeException e){
//					    handleValidationException((Part)astNode, problemRequestor, e);
//					}
//				}catch(CancelledException  e){
//				    throw e;
//				}catch(CircularBuildRequestException e){
//				    throw e;
//				}catch(BuildException e){
//				    throw e;
//				}catch(RuntimeException e){
//				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
//				}
//				break;
			case ITypeBinding.SERVICE_BINDING:
				try{
					astNode.accept(new ServiceBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
				    
				    //TODO add code to handle circular buildexception
				    try{
				    	PartValidator validator = partBinding.getEnvironment().getCompiler().getValidatorFor((Part)astNode);
				    	if (validator != null) {
				    		validator.validatePart((Part)astNode, (IRPartBinding)partBinding, problemRequestor, compilerOptions);
				    	}
					}catch(CancelledException e){
					    throw e;
					}catch(RuntimeException e){
					    handleValidationException((Part)astNode, problemRequestor, e);
					}
				}catch(CancelledException  e){
				    throw e;
				}catch(CircularBuildRequestException e){
				    throw e;
				}catch(BuildException e){
				    throw e;
				}catch(RuntimeException e){
				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
				}
				break;
//			case ITypeBinding.FORM_BINDING:
//				try{
//				    astNode.accept(new FormBinder((FormBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
//				
//					try{
//					    astNode.accept(new FormValidator(problemRequestor, compilerOptions));			
//					}catch(CancelledException e){
//					    throw e;
//					}catch(RuntimeException e){
//					    handleValidationException((Part)astNode, problemRequestor, e);
//					}
//				}catch(CancelledException  e){
//				    throw e;
//				}catch(CircularBuildRequestException e){
//				    throw e;
//				}catch(BuildException e){
//				    throw e;
//				}catch(RuntimeException e){
//				    handleBinderException((Part)astNode, partBinding, problemRequestor, e);
//				}
//				break;
		}
	}
	
	protected abstract void logPartBinderException(RuntimeException e);
	
	protected abstract void logValidationException(RuntimeException e);

    public void compileTopLevelFunction(TopLevelFunction functionAST, IPartBinding functionBinding, Scope scope, IPartBinding parentBinding, IDependencyRequestor dependencyInfo, IProblemRequestor requestor, ICompilerOptions compilerOptions) {
//       try{
//           functionAST.accept(new FunctionBinder((TopLevelFunctionBinding)functionBinding, (TopLevelFunctionBinding)functionBinding, scope, dependencyInfo, requestor, compilerOptions));
//           
//			try{
//			    functionAST.accept(new FunctionValidator(requestor, parentBinding, compilerOptions));
//			}catch(CancelledException e){
//			    throw e;
//       		}catch(RuntimeException e){
//			    requestor.acceptProblem(functionAST.getName(), IProblemRequestor.CONTEXT_SPECIFIC_COMPILATION_EXCEPTION, new String[]{functionAST.getName().getCanonicalName()});
//			    logValidationException(e);
//			}
//		}catch(CancelledException e){
//		    throw e;
//		}catch(CircularBuildRequestException e){
//		    throw e; 
//		}catch(RuntimeException e){
//		   requestor.acceptProblem(functionAST.getName(), IProblemRequestor.CONTEXT_SPECIFIC_COMPILATION_EXCEPTION, new String[]{functionAST.getName().getCanonicalName()});
//		   logPartBinderException(e);
//		}
	}
    
    private void handleValidationException(Part astNode, IProblemRequestor problemRequestor, RuntimeException e) {
        problemRequestor.acceptProblem(astNode.getName(), IProblemRequestor.COMPILATION_EXCEPTION, new String[]{astNode.getName().getCanonicalName()});
        logValidationException(e);
    }  
    
    private void handleBinderException(Part astNode, IPartBinding partBinding, IProblemRequestor problemRequestor, RuntimeException e) {
        if(!partBinding.isValid()){
	        throw e;
	    }
        problemRequestor.acceptProblem(astNode.getName(), IProblemRequestor.COMPILATION_EXCEPTION, new String[]{astNode.getName().getCanonicalName()});
	    logPartBinderException(e);
    }
}
