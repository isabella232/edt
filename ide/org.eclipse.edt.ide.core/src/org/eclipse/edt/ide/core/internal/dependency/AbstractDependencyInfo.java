/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.TopLevelFunctionBinding;
import org.eclipse.edt.compiler.internal.core.dependency.IDependencyRequestor;
import org.eclipse.edt.compiler.internal.core.lookup.FunctionContainerScope;
import org.eclipse.edt.mof.egl.utils.InternUtil;


public abstract class AbstractDependencyInfo  implements IDependencyInfo, IDependencyRequestor {

	private Set topLevelFunctions = new LinkedHashSet();
	private FunctionContainerScope functionContainerScope;
	private boolean recordTopLevelFunctions = true;
	
	public Set getTopLevelFunctions(){
		return Collections.unmodifiableSet(topLevelFunctions);
	}
	
	public void recordTopLevelFunctionBinding(IFunctionBinding functionBinding) {
	    
	    if(recordTopLevelFunctions && functionBinding.isTopLevelFunction()){
			topLevelFunctions.add(functionBinding);	
			
			String[] qualifiedName = ((TopLevelFunctionBinding)functionBinding).getPackageName();
			int length = qualifiedName.length;
			System.arraycopy(qualifiedName, 0, qualifiedName = new String[length + 1], 0, length);
			qualifiedName[length] = functionBinding.getName();
			
			recordQualifiedName(InternUtil.intern(qualifiedName));
	    }
	}	
	
	protected abstract void recordQualifiedName(String[] strings);

	public void recordFunctionContainerScope(FunctionContainerScope scope) {
		functionContainerScope = scope;
	}
	
	public FunctionContainerScope getFunctionContainerScope(){
		return functionContainerScope;
	}
    
	public void stopRecordingTopLevelFunctionBindings() {
        recordTopLevelFunctions = false;       
    }

	public void startRecordingTopLevelFunctionBindings() {
       recordTopLevelFunctions = true;        
    }

}
