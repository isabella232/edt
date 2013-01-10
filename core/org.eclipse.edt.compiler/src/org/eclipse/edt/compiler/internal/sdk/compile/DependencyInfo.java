/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.core.ast.Name;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;
import org.eclipse.edt.mof.egl.FunctionPart;
import org.eclipse.edt.mof.egl.Type;


public class DependencyInfo implements IDependencyRequestor {

    private Set<FunctionPart> topLevelFunctions = new HashSet<FunctionPart>();
    private FunctionContainerScope functionContainerScope;
    private boolean recordTopLevelFunctions = true;
    
    public Set getQualifiedNames() {
        return null;
    }

    public Set getSimpleNames() {
        return null;
    }

    public Set<FunctionPart> getTopLevelFunctions() {
        return topLevelFunctions;
    }

    public FunctionContainerScope getFunctionContainerScope() {
        return functionContainerScope;
    }

    public void recordSimpleName(String simpleName) {}

    public void recordName(Name name) {}

    public void recordPackageBinding(IPackageBinding binding) {}

    public void recordType(Type binding) {}

    public void recordTopLevelFunction(FunctionPart function) {
        if(recordTopLevelFunctions){
            topLevelFunctions.add(function);            
        }
    }

    public void recordFunctionContainerScope(FunctionContainerScope scope) {
        this.functionContainerScope = scope;
    }

    public void stopRecordingTopLevelFunctions() {
        recordTopLevelFunctions = false;            
    }

    public void startRecordingTopLevelFunctions() {
       recordTopLevelFunctions = true;            
    }        
}
