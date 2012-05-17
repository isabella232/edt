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
import java.util.List;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.EnumerationEntry;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;


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

    public List<Type> findType(String simpleName) {
        List<Type> result = parentScope.findType(simpleName);
        if(result != null) return result;
        
        Part part = BindingUtil.getPart(sysEnvironment.getPartBinding(null,simpleName));
        if (part == null) {
        	return null;
        }
        List<Type> list = new ArrayList<Type>();
        list.add(part);
        return list;
    }
    
    public IPackageBinding findPackage(String simpleName) {
        return parentScope.findPackage(simpleName);
    }
    
    public boolean isSystemScope() {
    	return true;
    }

	@Override
	public List<Member> findMember(String simpleName) {

		List<Member> result = sysEnvironment.getSystemEnvironment().getSystemLibraryManager().findMember(simpleName);
		if (result != null) {
			return result;
		}
		
		EnumerationEntry entry = sysEnvironment.getSystemEnvironment().getEnumerationManager().findData(simpleName);
		if (entry != null) {
			List<Member> list = new ArrayList<Member>();
			list.add(entry);
			return list;
		}
		
		return parentScope.findMember(simpleName);
	}

}
