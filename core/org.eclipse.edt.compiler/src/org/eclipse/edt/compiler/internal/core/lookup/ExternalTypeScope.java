/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.ExternalTypeBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;

public class ExternalTypeScope extends Scope {
    
    private ExternalTypeBinding etBinding;
    
    public ExternalTypeScope(Scope parentScope, ExternalTypeBinding etBinding) {
        super(parentScope);
        this.etBinding = etBinding;
    }

    public IDataBinding findData(String simpleName) {
        IDataBinding result = etBinding.findData(simpleName);
        if(result != IBinding.NOT_FOUND_BINDING) return result;
        
        return parentScope.findData(simpleName);
    }

    public IFunctionBinding findFunction(String simpleName) {
    	IFunctionBinding result = etBinding.findFunction(simpleName);
        if(result != IBinding.NOT_FOUND_BINDING) return result;
        return parentScope.findFunction(simpleName);
    }

    public IPackageBinding findPackage(String simpleName) {
        return parentScope.findPackage(simpleName);
    }

    public ITypeBinding findType(String simpleName) {
        return parentScope.findType(simpleName);
    }
    
    public IPartBinding getPartBinding() {
    	return etBinding;
    }

}
