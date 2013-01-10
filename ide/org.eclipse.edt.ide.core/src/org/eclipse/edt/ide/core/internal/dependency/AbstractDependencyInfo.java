/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.core.internal.dependency;

import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;


public abstract class AbstractDependencyInfo implements IDependencyInfo, IDependencyRequestor {

	private FunctionContainerScope functionContainerScope;
	
	protected abstract void recordQualifiedName(String strings);

	public void recordFunctionContainerScope(FunctionContainerScope scope) {
		functionContainerScope = scope;
	}
	
	public FunctionContainerScope getFunctionContainerScope(){
		return functionContainerScope;
	}
}
