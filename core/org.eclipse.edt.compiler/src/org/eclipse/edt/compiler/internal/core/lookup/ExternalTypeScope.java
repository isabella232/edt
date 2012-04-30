/*
 * Licensed Materials - Property of IBM
 *
 * Copyright IBM Corporation 2005, 2010. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA DP Schedule Contract with IBM Corp.
 */
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
