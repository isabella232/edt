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

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;


/**
 * @author svihovec
 *
 */
public class EnvironmentScope extends Scope {

	private IPackageBinding rootPackageBinding; 
	protected IDependencyRequestor dependencyRequestor;
	
	public EnvironmentScope(IEnvironment environment, IDependencyRequestor dependencyRequestor) {
		super(null);
		this.rootPackageBinding = environment.getRootPackage();
		this.dependencyRequestor = dependencyRequestor;
	}

	public ITypeBinding findType(String simpleName) {
		return IBinding.NOT_FOUND_BINDING; // Can't import parts in the default package
	}

	public IFunctionBinding findFunction(String simpleName) {
		return null;
	}

	public IDataBinding findData(String simpleName) {
		return null;
	}

	public IPackageBinding findPackage(String simpleName) {
		dependencyRequestor.recordSimpleName(simpleName);
		return rootPackageBinding.resolvePackage(simpleName);
	}
	
	public IPackageBinding getRootPackageBinding(){
		return rootPackageBinding;
	}
	
	public EnvironmentScope getEnvironmentScope() {
		return this;
	}
}
