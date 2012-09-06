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
package org.eclipse.edt.compiler.internal.core.compiler;

import org.eclipse.edt.compiler.binding.AnnotationTypeCompletor;
import org.eclipse.edt.compiler.binding.DelegateBindingCompletor;
import org.eclipse.edt.compiler.binding.EGLClassBindingCompletor;
import org.eclipse.edt.compiler.binding.EnumerationBindingCompletor;
import org.eclipse.edt.compiler.binding.ExternalTypeBindingCompletor;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.FileBindingCompletor;
import org.eclipse.edt.compiler.binding.HandlerBindingCompletor;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.IRPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.InterfaceBindingCompletor;
import org.eclipse.edt.compiler.binding.LibraryBindingCompletor;
import org.eclipse.edt.compiler.binding.ProgramBindingCompletor;
import org.eclipse.edt.compiler.binding.RecordBindingCompletor;
import org.eclipse.edt.compiler.binding.ServiceBindingCompletor;
import org.eclipse.edt.compiler.binding.StereotypeTypeCompletor;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.internal.core.builder.NullProblemRequestor;
import org.eclipse.edt.compiler.internal.core.dependency.NullDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.ICompilerOptions;
import org.eclipse.edt.compiler.internal.core.lookup.Scope;


/**
 * @author svihovec
 *
 */
public class BindingCompletor {
	
	private static final BindingCompletor INSTANCE = new BindingCompletor();
	
	private BindingCompletor(){}
	
	public static final BindingCompletor getInstance(){
		return INSTANCE;
	}
	
	public synchronized void completeBinding(Node astNode, IPartBinding binding, Scope parentScope, ICompilerOptions compilerOptions){
		
		switch(binding.getKind()){
			case ITypeBinding.FILE_BINDING:
				astNode.accept(new FileBindingCompletor(parentScope, (FileBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.PROGRAM_BINDING:
				astNode.accept(new ProgramBindingCompletor(parentScope, (IRPartBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.HANDLER_BINDING:
				astNode.accept(new HandlerBindingCompletor(parentScope, (IRPartBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.CLASS_BINDING:
				astNode.accept(new EGLClassBindingCompletor(parentScope, (IRPartBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.INTERFACE_BINDING:
				astNode.accept(new InterfaceBindingCompletor(parentScope, (IRPartBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.LIBRARY_BINDING:
				astNode.accept(new LibraryBindingCompletor(parentScope, (IRPartBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.FLEXIBLE_RECORD_BINDING:
				astNode.accept(new RecordBindingCompletor(parentScope, (IRPartBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.SERVICE_BINDING:
				astNode.accept(new ServiceBindingCompletor(parentScope, (IRPartBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.DELEGATE_BINDING:
				astNode.accept(new DelegateBindingCompletor(parentScope, (IRPartBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;			
			case ITypeBinding.ENUMERATION_BINDING:
				astNode.accept(new EnumerationBindingCompletor(parentScope, (IRPartBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;			
			case ITypeBinding.EXTERNALTYPE_BINDING:
				astNode.accept(new ExternalTypeBindingCompletor(parentScope, (IRPartBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;			
			case ITypeBinding.ANNOTATION_BINDING:
				astNode.accept(new AnnotationTypeCompletor(parentScope, (IRPartBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;			
			case ITypeBinding.STEREOTYPE_BINDING:
				astNode.accept(new StereotypeTypeCompletor(parentScope, (IRPartBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;			
		}
	}
}
