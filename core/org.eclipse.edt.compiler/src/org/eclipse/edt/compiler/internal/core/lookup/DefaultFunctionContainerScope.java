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

import java.util.Collections;
import java.util.Map;

import org.eclipse.edt.compiler.binding.FunctionContainerBinding;
import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.IPartBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;


/**
 * @author Dave Murray
 */
public class DefaultFunctionContainerScope extends FunctionContainerScope {
	
	public boolean unqualifiedItemReferencesAreAllowed() {
		return parentScope.unqualifiedItemReferencesAreAllowed();
	}
	
	public boolean I4GLItemsNullableIsEnabled() {
		return parentScope.I4GLItemsNullableIsEnabled();
	}
	
	public DefaultFunctionContainerScope(Scope parentScope) {
		super(parentScope, (FunctionContainerBinding) null);
	}
	
    public ITypeBinding findType(String simpleName) {
    	return parentScope.findType(simpleName);
    }
    
    public IFunctionBinding findFunction(String simpleName) {
    	return parentScope.findFunction(simpleName);
    }
    
    public IDataBinding findData(String simpleName) {
    	return parentScope.findData(simpleName);
    }
    
	public IDataBinding findIOTargetData(String simpleName) {
		return parentScope.findIOTargetData(simpleName);
	}
    
    public IDataBinding Data(String simpleName) {
    	return parentScope.findData(simpleName);
    }
    
    public IPackageBinding findPackage(String simpleName) {
	    return parentScope.findPackage(simpleName);
	}

	public Scope getScopeForKeywordThis() {
		return parentScope.getScopeForKeywordThis();
	}

	public void startReturningFunctionContainerFunctions() {
		parentScope.startReturningFunctionContainerFunctions();
	}

	public void stopReturningFunctionContainerFunctions() {
		parentScope.stopReturningFunctionContainerFunctions();
	}
	
	public IPartBinding getPartBinding() {
		return parentScope.getPartBinding();
	}
	
	public Map getItemsWhoseNamesCanBeUnqualified() {
		return parentScope instanceof FunctionContainerScope ? ((FunctionContainerScope) parentScope).getItemsWhoseNamesCanBeUnqualified() : Collections.EMPTY_MAP;
	}
	
	public Map getRecordsFormsAndDataTables() {
		return parentScope instanceof FunctionContainerScope ? ((FunctionContainerScope) parentScope).getRecordsFormsAndDataTables() : Collections.EMPTY_MAP;
	}
}
