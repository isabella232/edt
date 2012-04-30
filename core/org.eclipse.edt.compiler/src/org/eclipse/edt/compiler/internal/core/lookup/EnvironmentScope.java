/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.mof.egl.Member;


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
		return null; // Can't import parts in the default package
	}

	public Member findMember(String simpleName) {
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
