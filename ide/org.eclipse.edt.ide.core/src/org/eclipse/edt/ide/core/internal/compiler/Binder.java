/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.compiler;

import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.DataTableBinding;
import org.eclipse.edt.compiler.binding.DelegateBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FormGroupBinding;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.ProgramBinding;
import org.eclipse.edt.compiler.binding.ServiceBinding;
import org.eclipse.edt.compiler.binding.TopLevelFunctionBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.CancelledException;
import org.eclipse.edt.compiler.internal.core.builder.CircularBuildRequestException;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.DataItemBinder;
import org.eclipse.edt.compiler.internal.core.lookup.DataTableBinder;
import org.eclipse.edt.compiler.internal.core.lookup.DelegateBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ExternalTypeBinder;
import org.eclipse.edt.compiler.internal.core.lookup.FileBinder;
import org.eclipse.edt.compiler.internal.core.lookup.FixedRecordBinder;
import org.eclipse.edt.compiler.internal.core.lookup.FlexibleRecordBinder;
import org.eclipse.edt.compiler.internal.core.lookup.FormBinder;
import org.eclipse.edt.compiler.internal.core.lookup.FormGroupBinder;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionBinder;
import org.eclipse.edt.compiler.internal.core.lookup.HandlerBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.InterfaceBinder;
import org.eclipse.edt.compiler.internal.core.lookup.LibraryBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ProgramBinder;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.lookup.ServiceBinder;

public class Binder {

	private static final Binder INSTANCE = new Binder();
	
	private Binder(){}
	
	public static Binder getInstance(){
		return INSTANCE;
	}
	
	public synchronized void bindPart(Node astNode, IPartBinding partBinding, Scope parentScope, IDependencyRequestor dependencyRequestor, IProblemRequestor problemRequestor, ICompilerOptions compilerOptions){
		
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
				}
				break;
			case ITypeBinding.PROGRAM_BINDING:
				try{
				    astNode.accept(new ProgramBinder((ProgramBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
			case ITypeBinding.FUNCTION_BINDING:
				try{
				    astNode.accept(new FunctionBinder((TopLevelFunctionBinding)partBinding, (TopLevelFunctionBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
			case ITypeBinding.DATAITEM_BINDING:
				try{
				    astNode.accept(new DataItemBinder((DataItemBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
			case ITypeBinding.DATATABLE_BINDING:
				try{
				    astNode.accept(new DataTableBinder((DataTableBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
			case ITypeBinding.FORMGROUP_BINDING:
				try{
				    astNode.accept(new FormGroupBinder((FormGroupBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
			case ITypeBinding.HANDLER_BINDING:
				try{
				    astNode.accept(new HandlerBinder((HandlerBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
				    astNode.accept(new InterfaceBinder((InterfaceBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
				    astNode.accept(new LibraryBinder((LibraryBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
			case ITypeBinding.FLEXIBLE_RECORD_BINDING:
				try{
				    astNode.accept(new FlexibleRecordBinder((FlexibleRecordBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
			case ITypeBinding.FIXED_RECORD_BINDING:
				try{
				    astNode.accept(new FixedRecordBinder((FixedRecordBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
			case ITypeBinding.SERVICE_BINDING:
				try{
				    astNode.accept(new ServiceBinder((ServiceBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
			case ITypeBinding.FORM_BINDING:
				try{
				    astNode.accept(new FormBinder((FormBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
				
			case ITypeBinding.EXTERNALTYPE_BINDING:
				try{
				    astNode.accept(new ExternalTypeBinder((ExternalTypeBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
				
			case ITypeBinding.DELEGATE_BINDING:
				try{
				    astNode.accept(new DelegateBinder((DelegateBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
		}
	}
	
	public void bindTopLevelFunction(TopLevelFunction functionAST, IPartBinding functionBinding, Scope scope, IDependencyRequestor dependencyInfo, IProblemRequestor requestor, ICompilerOptions compilerOptions) {
       try{
           functionAST.accept(new FunctionBinder((TopLevelFunctionBinding)functionBinding, (TopLevelFunctionBinding)functionBinding, scope, dependencyInfo, requestor, compilerOptions));
		}catch(CancelledException e){
		    throw e;
		}catch(CircularBuildRequestException e){
		    throw e; 
		}catch(BuildException e){
		    throw e;
		}catch(RuntimeException e){
		   requestor.acceptProblem(functionAST.getName(), IProblemRequestor.CONTEXT_SPECIFIC_COMPILATION_EXCEPTION, new String[]{functionAST.getIdentifier()});
		}
	}
    
    private void handleBinderException(Part astNode, IPartBinding partBinding, IProblemRequestor problemRequestor, RuntimeException e) {
        if(!partBinding.isValid()){
	        throw e;
	    }
        problemRequestor.acceptProblem(astNode.getName(), IProblemRequestor.COMPILATION_EXCEPTION, new String[]{astNode.getIdentifier()});
	}
}
