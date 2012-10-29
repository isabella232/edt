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
package org.eclipse.edt.compiler.internal.core.dependency;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;
import org.eclipse.edt.mof.egl.Type;


/**
 * @author svihovec
 *
 * Dependencies are recorded in the following situations:
 * 1) Any time a TypeBinding or a PackageBinding is set on a Name, the Binding is recorded
 * 2) Any time a File Scope or a higher Scope is asked for a Simple Name, the Simple Name is recorded
 * 3) Any time a name cannot be resolved correctly, the entire Name is recorded
 * 4) Any time a DataBinding is set on a name, the Type of the DataBinding is recorded 
 * 5) Any time a TypeBinding is accessed from another Binding?
 * 
 */
public interface IDependencyRequestor {
	
	public void recordSimpleName(String simpleName);
	public void recordName(Name name);
	
	public void recordPackageBinding(IPackageBinding binding);
	public void recordType(Type type);
	
	public void recordFunctionContainerScope(FunctionContainerScope scope);
}
