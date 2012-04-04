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

import org.eclipse.edt.compiler.binding.DataItemBinding;
import org.eclipse.edt.compiler.binding.DataItemBindingCompletor;
import org.eclipse.edt.compiler.binding.DataTableBinding;
import org.eclipse.edt.compiler.binding.DataTableBindingCompletor;
import org.eclipse.edt.compiler.binding.DelegateBinding;
import org.eclipse.edt.compiler.binding.DelegateBindingCompletor;
import org.eclipse.edt.compiler.binding.EnumerationBindingCompletor;
import org.eclipse.edt.compiler.binding.EnumerationTypeBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.ExternalTypeBindingCompletor;
import org.eclipse.edt.compiler.binding.FileBinding;
import org.eclipse.edt.compiler.binding.FileBindingCompletor;
import org.eclipse.edt.compiler.binding.FixedRecordBinding;
import org.eclipse.edt.compiler.binding.FixedRecordBindingCompletor;
import org.eclipse.edt.compiler.binding.FlexibleRecordBinding;
import org.eclipse.edt.compiler.binding.FlexibleRecordBindingCompletor;
import org.eclipse.edt.compiler.binding.FormBinding;
import org.eclipse.edt.compiler.binding.FormBindingCompletor;
import org.eclipse.edt.compiler.binding.FormGroupBinding;
import org.eclipse.edt.compiler.binding.FormGroupBindingCompletor;
import org.eclipse.edt.compiler.binding.FunctionBindingCompletor;
import org.eclipse.edt.compiler.binding.HandlerBinding;
import org.eclipse.edt.compiler.binding.HandlerBindingCompletor;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.InterfaceBinding;
import org.eclipse.edt.compiler.binding.InterfaceBindingCompletor;
import org.eclipse.edt.compiler.binding.LibraryBinding;
import org.eclipse.edt.compiler.binding.LibraryBindingCompletor;
import org.eclipse.edt.compiler.binding.ProgramBinding;
import org.eclipse.edt.compiler.binding.ProgramBindingCompletor;
import org.eclipse.edt.compiler.binding.ServiceBinding;
import org.eclipse.edt.compiler.binding.ServiceBindingCompletor;
import org.eclipse.edt.compiler.binding.TopLevelFunctionBinding;
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
				astNode.accept(new ProgramBindingCompletor(parentScope, (ProgramBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.FUNCTION_BINDING:
				astNode.accept(new FunctionBindingCompletor((TopLevelFunctionBinding)binding, parentScope, (TopLevelFunctionBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.DATAITEM_BINDING:
				astNode.accept(new DataItemBindingCompletor(parentScope, (DataItemBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.DATATABLE_BINDING:
				astNode.accept(new DataTableBindingCompletor(parentScope, (DataTableBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.FORMGROUP_BINDING:
				astNode.accept(new FormGroupBindingCompletor(parentScope, (FormGroupBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.HANDLER_BINDING:
				astNode.accept(new HandlerBindingCompletor(parentScope, (HandlerBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.INTERFACE_BINDING:
				astNode.accept(new InterfaceBindingCompletor(parentScope, (InterfaceBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.LIBRARY_BINDING:
				astNode.accept(new LibraryBindingCompletor(parentScope, (LibraryBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.FLEXIBLE_RECORD_BINDING:
				astNode.accept(new FlexibleRecordBindingCompletor(parentScope, (FlexibleRecordBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.FIXED_RECORD_BINDING:
				astNode.accept(new FixedRecordBindingCompletor(parentScope, (FixedRecordBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.SERVICE_BINDING:
				astNode.accept(new ServiceBindingCompletor(parentScope, (ServiceBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;
			case ITypeBinding.FORM_BINDING:
				astNode.accept(new FormBindingCompletor(parentScope, (FormBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;			
			case ITypeBinding.DELEGATE_BINDING:
				astNode.accept(new DelegateBindingCompletor(parentScope, (DelegateBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;			
			case ITypeBinding.ENUMERATION_BINDING:
				astNode.accept(new EnumerationBindingCompletor(parentScope, (EnumerationTypeBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;			
			case ITypeBinding.EXTERNALTYPE_BINDING:
				astNode.accept(new ExternalTypeBindingCompletor(parentScope, (ExternalTypeBinding)binding, NullDependencyRequestor.getInstance(), NullProblemRequestor.getInstance(), compilerOptions));
				break;			
		}
	}
}
