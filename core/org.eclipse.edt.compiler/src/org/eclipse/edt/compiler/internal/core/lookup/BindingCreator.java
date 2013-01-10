/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.internal.core.lookup;

import org.eclipse.edt.compiler.Util;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.Class;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.internal.util.PackageAndPartName;


/**
 * @author svihovec
 *
 */
public class BindingCreator extends DefaultASTVisitor {

	private IEnvironment environment;
	private IPartBinding partBinding;
	PackageAndPartName ppName;
	
	public BindingCreator(IEnvironment environment, PackageAndPartName ppName, Node astNode){
		this.environment = environment;
		this.ppName = ppName;
		astNode.accept(this);
	}
	
	public IPartBinding getPartBinding(){
		return partBinding;
	}
	
	public boolean visit(File file) {
		partBinding = environment.getNewPartBinding(ppName, ITypeBinding.FILE_BINDING);
		return false;
	}
	
	public boolean visit(Record record) {
		
	    partBinding = environment.getNewPartBinding(ppName, Util.getPartType(record));
		return false;
	}
	
	public boolean visit(Delegate delegate) {
		partBinding = environment.getNewPartBinding(ppName, ITypeBinding.DELEGATE_BINDING);
		return false;
	}

	public boolean visit(ExternalType extType) {
		partBinding = environment.getNewPartBinding(ppName, ITypeBinding.EXTERNALTYPE_BINDING);
		return false;
	}

	public boolean visit(Enumeration enumeration) {
		partBinding = environment.getNewPartBinding(ppName, ITypeBinding.ENUMERATION_BINDING);
		return false;
	}

	public boolean visit(Handler handler) {
		partBinding = environment.getNewPartBinding(ppName, ITypeBinding.HANDLER_BINDING);
		return false;
	}

	public boolean visit(Class eglClass) {
		partBinding = environment.getNewPartBinding(ppName, ITypeBinding.CLASS_BINDING);
		return false;
	}

	public boolean visit(Library library) {
		partBinding = environment.getNewPartBinding(ppName, ITypeBinding.LIBRARY_BINDING);
		return false;
	}	
	
	public boolean visit(Program program) {
		partBinding = environment.getNewPartBinding(ppName, ITypeBinding.PROGRAM_BINDING);
		return false;
	}
	
	public boolean visit(Service service) {
		partBinding = environment.getNewPartBinding(ppName, ITypeBinding.SERVICE_BINDING);
		return false;
	}
		
	public boolean visit(Interface interfaceNode) {
		partBinding = environment.getNewPartBinding(ppName, ITypeBinding.INTERFACE_BINDING);
		return false;
	}
}
