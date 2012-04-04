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

import org.eclipse.edt.compiler.binding.DataBindingWithImplicitQualifier;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;

/**
 * @author winghong
 */
public class DataBindingScope extends Scope {
    
    private IDataBinding dataBinding;
    private boolean includeImplicitQualifier = false;
    
    public DataBindingScope(Scope parentScope, IDataBinding dataBinding) {
        this(parentScope, dataBinding, false);
    }

    public DataBindingScope(Scope parentScope, IDataBinding dataBinding, boolean includeImplicitQualifier) {
        super(parentScope);
        this.dataBinding = dataBinding;
        this.includeImplicitQualifier = includeImplicitQualifier;
    }

    public IDataBinding findData(String simpleName) {
        IDataBinding result = dataBinding.findData(simpleName);
        if(result != IBinding.NOT_FOUND_BINDING) {
        	if (includeImplicitQualifier) {
        		result = new DataBindingWithImplicitQualifier(result, dataBinding);
        	}
        	return result;
        }
        else {
        	return parentScope.findData(simpleName);
        }
    }
    
    public IFunctionBinding findFunction(String simpleName) {
        return parentScope.findFunction(simpleName);
    }

    public IPackageBinding findPackage(String simpleName) {
        return parentScope.findPackage(simpleName);
    }

    public ITypeBinding findType(String simpleName) {
        return parentScope.findType(simpleName);
    }

}
