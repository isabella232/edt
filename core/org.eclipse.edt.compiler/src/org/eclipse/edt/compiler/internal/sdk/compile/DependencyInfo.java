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
package org.eclipse.edt.compiler.internal.sdk.compile;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.edt.compiler.binding.IBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;


public class DependencyInfo implements IDependencyRequestor {

    private Set topLevelFunctions = new HashSet();
    private FunctionContainerScope functionContainerScope;
    private boolean recordTopLevelFunctions = true;
    
    public Set getQualifiedNames() {
        return null;
    }

    public Set getSimpleNames() {
        return null;
    }

    public Set getTopLevelFunctions() {
        return topLevelFunctions;
    }

    public FunctionContainerScope getFunctionContainerScope() {
        return functionContainerScope;
    }

    public void recordSimpleName(String simpleName) {}

    public void recordName(Name name) {}

    public void recordBinding(IBinding binding) {}

    public void recordPackageBinding(IPackageBinding binding) {}

    public void recordTypeBinding(ITypeBinding binding) {}

    public void recordTopLevelFunctionBinding(IFunctionBinding binding) {
        if(recordTopLevelFunctions){
            topLevelFunctions.add(binding);            
        }
    }

    public void recordFunctionContainerScope(FunctionContainerScope scope) {
        this.functionContainerScope = scope;
    }

    public void stopRecordingTopLevelFunctionBindings() {
        recordTopLevelFunctions = false;            
    }

    public void startRecordingTopLevelFunctionBindings() {
       recordTopLevelFunctions = true;            
    }        
}
