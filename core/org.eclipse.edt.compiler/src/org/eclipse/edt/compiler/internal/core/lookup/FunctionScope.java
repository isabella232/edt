/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;


/**
 * @author winghong
 */
public class FunctionScope extends Scope implements ILocalVariableScope {
    
//	private FunctionContainerScope parentScope;
	
	private Map<String, Member> localVariables = new HashMap<String, Member>();   
    protected Set<String> declaredDataNames = new HashSet<String>();
    
    public FunctionScope(Scope parentScope, FunctionMember functionBinding) {
        this(parentScope);
        
        // Add function parameters to localVariables collection
        List<FunctionParameter> functionParameters = functionBinding.getParameters();
        for(FunctionParameter parm : functionParameters) {
         	localVariables.put(parm.getName(), parm);
        }
    }
    
    protected FunctionScope(Scope parentScope) {
    	super(parentScope);
    }

    public List<Member> findMember(String simpleName) {
    	/* 
    	 * 1) Search for constant, item or record in the local storage or
    	 *    parameter list of the function.
    	 */
        Member result = localVariables.get(simpleName);
        if(result != null) {
        	List<Member> list = new ArrayList<Member>();
        	list.add(result);
        	return list;
        }
                
        return parentScope.findMember(simpleName);
    }
    
    public IPackageBinding findPackage(String simpleName) {
        return parentScope.findPackage(simpleName);
    }

    public List<Type> findType(String simpleName) {
        return parentScope.findType(simpleName);
    }
    
    public void addLocalVariable(Member var) {
        localVariables.put(var.getName(), var);       
    }
             
	public Scope getScopeForKeywordThis() {
		return parentScope.getScopeForKeywordThis();
	}
	
	
    public Part getPart() {
		return parentScope.getPart();
	}
	
	public void addDeclaredDataName(String name) {
		declaredDataNames.add(name);
	}
	
	public boolean hasDeclaredDataName(String name) {
		return declaredDataNames.contains(name);
	}
}
