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
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;


/**
 * @author winghong
 */
public class TypeScope extends Scope {
    
    private Type type;
    
    public TypeScope(Scope parentScope, Type type) {
        super(parentScope);
        this.type = type;
    }

    public List<Member> findMember(String simpleName) {
        List<Member> result = find(simpleName);
        
        if (result != null) {
        	return result;
        }
        return parentScope.findMember(simpleName);
    }
    
    public IPackageBinding findPackage(String simpleName) {
        return parentScope.findPackage(simpleName);
    }

    public List<Type> findType(String simpleName) {
        return parentScope.findType(simpleName);
    }

    public Type getType() {
        return type;
    }
    
	private List<Member> find(String simpleName) {
		Type myType = type;
		if (myType == null) {
			return null;
		}
		Member mbr = BindingUtil.createExplicitDynamicAccessMember(myType, simpleName);
		if (mbr != null) {
			List<Member> list = new ArrayList<Member>();
			list.add(mbr);
			return list;
		}
		
		myType = BindingUtil.getBaseType(myType);
		return BindingUtil.findMembers(myType, simpleName);		
	}

}
