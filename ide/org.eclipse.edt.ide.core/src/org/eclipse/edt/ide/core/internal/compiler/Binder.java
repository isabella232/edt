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

import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Part;
import org.eclipse.edt.compiler.internal.core.builder.BuildException;
import org.eclipse.edt.compiler.internal.core.builder.CancelledException;
import org.eclipse.edt.compiler.internal.core.builder.CircularBuildRequestException;
import org.eclipse.edt.compiler.internal.core.builder.IMarker;
import org.eclipse.edt.compiler.internal.core.builder.IProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.AnnotationTypeBinder;
import org.eclipse.edt.compiler.internal.core.lookup.DelegateBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ClassBinder;
import org.eclipse.edt.compiler.internal.core.lookup.EnumerationBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ExternalTypeBinder;
import org.eclipse.edt.compiler.internal.core.lookup.FileBinder;
import org.eclipse.edt.compiler.internal.core.lookup.HandlerBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.InterfaceBinder;
import org.eclipse.edt.compiler.internal.core.lookup.LibraryBinder;
import org.eclipse.edt.compiler.internal.core.lookup.ProgramBinder;
import org.eclipse.edt.compiler.internal.core.lookup.RecordBinder;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;
import org.eclipse.edt.compiler.internal.core.lookup.ServiceBinder;
import org.eclipse.edt.compiler.internal.core.lookup.StereotypeTypeBinder;

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
					astNode.accept(new ProgramBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
					astNode.accept(new HandlerBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
					astNode.accept(new RecordBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
					astNode.accept(new ServiceBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
					astNode.accept(new ExternalTypeBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
					astNode.accept(new DelegateBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
					astNode.accept(new ClassBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
				
			case ITypeBinding.ENUMERATION_BINDING:
				try{
					astNode.accept(new EnumerationBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
				
			case ITypeBinding.ANNOTATION_BINDING:
				try{
					astNode.accept(new AnnotationTypeBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
				
			case ITypeBinding.STEREOTYPE_BINDING:
				try{
					astNode.accept(new StereotypeTypeBinder((IRPartBinding)partBinding, parentScope, dependencyRequestor, problemRequestor, compilerOptions));
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
	
    private void handleBinderException(Part astNode, IPartBinding partBinding, IProblemRequestor problemRequestor, RuntimeException e) {
        if(!partBinding.isValid()){
	        throw e;
	    }
        problemRequestor.acceptProblem(astNode.getName(), IProblemRequestor.COMPILATION_EXCEPTION, new String[]{astNode.getIdentifier()});
	}
}
