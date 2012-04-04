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
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;

/**
 * @author winghong
 */
public abstract class Scope {
    
    protected Scope parentScope;
    
    public Scope(Scope parentScope) {
        super();
        this.parentScope = parentScope;
    }
    
    public Scope getParentScope() {
        return parentScope;
    }
    
    public void setParentScope(Scope newParent) {
    	parentScope = newParent;
    }
    
    public abstract ITypeBinding findType(String simpleName);
    
    public abstract IFunctionBinding findFunction(String simpleName);
    
    public abstract IDataBinding findData(String simpleName);
    
    public abstract IPackageBinding findPackage(String simpleName);
    
    public Scope getScopeForKeywordThis() {
    	return null;
    }
    
    public boolean unqualifiedItemReferencesAreAllowed() {
    	return false;
    }
    
    public boolean I4GLItemsNullableIsEnabled() {
    	return false;
    }
    
    public void stopReturningTopLevelFunctions() {
    	if(parentScope != null) parentScope.stopReturningTopLevelFunctions();
    }
    
    public void startReturningTopLevelFunctions() {
    	if(parentScope != null) parentScope.startReturningTopLevelFunctions();
    }
    
    public boolean isReturningTopLevelFunctions() {
    	if(parentScope != null) return parentScope.isReturningTopLevelFunctions(); 
    	return true;
    }
    
    public void stopReturningFunctionContainerFunctions() {
    	if(parentScope != null) parentScope.stopReturningFunctionContainerFunctions();
    }
    
    public void startReturningFunctionContainerFunctions() {
    	if(parentScope != null) parentScope.startReturningFunctionContainerFunctions();
    }
    
    public boolean isAnnotationLeftHandScope() {
        return false;
    }
    
    public boolean isProgramScope() {
        return false;
    }
    
    public boolean isSystemScope() {
    	return false;
    }
    
    public IDataBinding findIOTargetData(String simpleName) {
    	return IBinding.NOT_FOUND_BINDING;
    }
    
    public IPartBinding getPartBinding() {
    	return null;
    }

	public EnvironmentScope getEnvironmentScope() {
		return parentScope.getEnvironmentScope();
	}
}
