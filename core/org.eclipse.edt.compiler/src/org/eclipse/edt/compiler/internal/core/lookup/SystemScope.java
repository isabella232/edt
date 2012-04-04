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

import org.eclipse.edt.compiler.binding.AmbiguousDataBinding;
import org.eclipse.edt.compiler.binding.AmbiguousFunctionBinding;
import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.binding.TopLevelFunctionBinding;
import org.eclipse.edt.compiler.internal.core.lookup.System.SystemLibraryManager;


/**
 * @author Harmon
 */
public class SystemScope extends Scope {
	
	private IEnvironment sysEnvironment = null;

    /**
     * @param parentScope
     */
    public SystemScope(Scope parentScope,IEnvironment env) {
        super(parentScope);
        sysEnvironment = env;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.Scope#findType(java.lang.String)
     */
    public ITypeBinding findType(String simpleName) {
        ITypeBinding result = parentScope.findType(simpleName);
        if(result != IBinding.NOT_FOUND_BINDING) return result;
        
        result = sysEnvironment.getPartBinding(null,simpleName);
        
        if(result != null) return result;
        
        return IBinding.NOT_FOUND_BINDING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.Scope#findFunction(java.lang.String)
     */
    public IFunctionBinding findFunction(String simpleName) {
    	IFunctionBinding result =  parentScope.findFunction(simpleName);
    	if(result != IBinding.NOT_FOUND_BINDING) return result;
    	
    	result = sysEnvironment.getSystemEnvironment().getSystemLibraryManager().findFunction(simpleName);
    	if(result == null) result = IBinding.NOT_FOUND_BINDING;
    	if(result != IBinding.NOT_FOUND_BINDING) return result;
        
        return result;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.Scope#findData(java.lang.String)
     */
    public IDataBinding findData(String simpleName) {
    	IFunctionBinding topLevelFunctionBinding = parentScope.findFunction(simpleName);
    	if(topLevelFunctionBinding == AmbiguousFunctionBinding.getInstance()) {
    		return AmbiguousDataBinding.getInstance();
    	}    	
    	else if(topLevelFunctionBinding != IBinding.NOT_FOUND_BINDING) {
    		return ((TopLevelFunctionBinding) topLevelFunctionBinding).getStaticTopLevelFunctionDataBinding();
    	}
    	
        IDataBinding result = sysEnvironment.getSystemEnvironment().getSystemLibraryManager().findData(simpleName);
        if (result != null) {
            return result;
        }
        
        result = sysEnvironment.getSystemEnvironment().getEnumerationManager().findData(simpleName);
        if (result != null) {
            return result;
        }
        return parentScope.findData(simpleName);
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.Scope#findPackage(java.lang.String)
     */
    public IPackageBinding findPackage(String simpleName) {
        return parentScope.findPackage(simpleName);
    }
    
    public boolean isSystemScope() {
    	return true;
    }

}
