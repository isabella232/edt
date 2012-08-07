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
package org.eclipse.edt.compiler.internal.core.lookup;

import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.DataItem;
import org.eclipse.edt.compiler.core.ast.DataTable;
import org.eclipse.edt.compiler.core.ast.DefaultASTVisitor;
import org.eclipse.edt.compiler.core.ast.Delegate;
import org.eclipse.edt.compiler.core.ast.EGLClass;
import org.eclipse.edt.compiler.core.ast.Enumeration;
import org.eclipse.edt.compiler.core.ast.ExternalType;
import org.eclipse.edt.compiler.core.ast.File;
import org.eclipse.edt.compiler.core.ast.FormGroup;
import org.eclipse.edt.compiler.core.ast.Handler;
import org.eclipse.edt.compiler.core.ast.Interface;
import org.eclipse.edt.compiler.core.ast.Library;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.Program;
import org.eclipse.edt.compiler.core.ast.Record;
import org.eclipse.edt.compiler.core.ast.Service;
import org.eclipse.edt.compiler.core.ast.TopLevelForm;
import org.eclipse.edt.compiler.core.ast.TopLevelFunction;


/**
 * @author svihovec
 *
 */
public class BindingCreator extends DefaultASTVisitor {

	private IEnvironment environment;
	private IPartBinding partBinding;
	private String packageName;
	private String caseSensitiveInternedPartName;

	public BindingCreator(IEnvironment environment, String packageName, String caseSensitiveInternedPartName, Node astNode){
		this.environment = environment;
		this.packageName = packageName;
		this.caseSensitiveInternedPartName = caseSensitiveInternedPartName;
		
		astNode.accept(this);
	}
	
	public IPartBinding getPartBinding(){
		return partBinding;
	}
	
	public boolean visit(File file) {
		partBinding = environment.getNewPartBinding(packageName, caseSensitiveInternedPartName, ITypeBinding.FILE_BINDING);
		return false;
	}
	
	public boolean visit(Record record) {
	    partBinding = environment.getNewPartBinding(packageName, caseSensitiveInternedPartName, ITypeBinding.FLEXIBLE_RECORD_BINDING);
		return false;
	}
	
	public boolean visit(Delegate delegate) {
		partBinding = environment.getNewPartBinding(packageName, caseSensitiveInternedPartName, ITypeBinding.DELEGATE_BINDING);
		return false;
	}

	public boolean visit(ExternalType extType) {
		partBinding = environment.getNewPartBinding(packageName, caseSensitiveInternedPartName, ITypeBinding.EXTERNALTYPE_BINDING);
		return false;
	}

	public boolean visit(Enumeration enumeration) {
		partBinding = environment.getNewPartBinding(packageName, caseSensitiveInternedPartName, ITypeBinding.ENUMERATION_BINDING);
		return false;
	}

	public boolean visit(Handler handler) {
		partBinding = environment.getNewPartBinding(packageName, caseSensitiveInternedPartName, ITypeBinding.HANDLER_BINDING);
		return false;
	}

	public boolean visit(EGLClass eglClass) {
		partBinding = environment.getNewPartBinding(packageName, caseSensitiveInternedPartName, ITypeBinding.CLASS_BINDING);
		return false;
	}

	public boolean visit(Library library) {
		partBinding = environment.getNewPartBinding(packageName, caseSensitiveInternedPartName, ITypeBinding.LIBRARY_BINDING);
		return false;
	}	
	
	public boolean visit(Program program) {
		partBinding = environment.getNewPartBinding(packageName, caseSensitiveInternedPartName, ITypeBinding.PROGRAM_BINDING);
		return false;
	}
	
	public boolean visit(Service service) {
		partBinding = environment.getNewPartBinding(packageName, caseSensitiveInternedPartName, ITypeBinding.SERVICE_BINDING);
		return false;
	}
		
	public boolean visit(Interface interfaceNode) {
		partBinding = environment.getNewPartBinding(packageName, caseSensitiveInternedPartName, ITypeBinding.INTERFACE_BINDING);
		return false;
	}
}
