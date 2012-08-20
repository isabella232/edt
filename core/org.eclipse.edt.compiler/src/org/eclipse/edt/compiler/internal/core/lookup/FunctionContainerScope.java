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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;

public class FunctionContainerScope extends Scope {
	
	Part functionContainerBinding;
	private List<Part> usedParts = new ArrayList<Part>();		
	
	
    public FunctionContainerScope(Scope parentScope, Part functionContainerBinding) {
        super(parentScope);
        this.functionContainerBinding = functionContainerBinding;
        
    }
    
    public FunctionContainerScope(Scope parentScope, FunctionContainerScope fContainerScope) {
    	super(parentScope);
    	
    	functionContainerBinding = fContainerScope.functionContainerBinding;
    	usedParts = fContainerScope.usedParts;	
    }

    public List<Member> findMember(String simpleName) {
    	
    	//Search for fields/functions in this part
    	List<Member> result = findMemberInPart(simpleName);
    	if (result != null) {
    		return result;
    	}
    	
    	//If not found, search for fields/functions in all the used types
    	result = new ArrayList<Member>();
    	for (Part part : usedParts) {
    		List<Member> temp = BindingUtil.findMembers(part, simpleName);
    		if (temp != null) {
    			result.addAll(temp);
    		}
    	}
    	if (result.isEmpty()) {
    		return null;
    	}
    	else {
    		return result;
    	}
    }
    
    public IPackageBinding findPackage(String simpleName) {
        return parentScope.findPackage(simpleName);
    }

    public List<Type> findType(String simpleName) {
    	return parentScope.findType(simpleName);
    }
    
    public void addUsedPart(Part usedPart) {
    	usedParts.add(usedPart);
    }
            
	public Scope getScopeForKeywordThis() {
		return this;
	}
			
    public Part getPart() {
		return functionContainerBinding;
	}
	

    protected List<Member> findMemberInPart(String id) {
    	return BindingUtil.findMembers(functionContainerBinding, id);
    }
    
    @Override
    public Type getType() {
    	return functionContainerBinding;
    }
}
