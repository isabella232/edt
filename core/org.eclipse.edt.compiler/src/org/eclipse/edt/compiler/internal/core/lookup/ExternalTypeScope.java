/*
 * Licensed Materials - Property of IBM
 *
 * Copyright IBM Corporation 2005, 2010. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA DP Schedule Contract with IBM Corp.
 */
package org.eclipse.edt.compiler.internal.core.lookup;

import java.util.List;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;

public class ExternalTypeScope extends Scope {
    
    private ExternalType et;
    
    public ExternalTypeScope(Scope parentScope, ExternalType et) {
        super(parentScope);
        this.et = et;
    }

    public List<Member> findMember(String simpleName) {
    	
    	List<Member> result = BindingUtil.findMembers(et, simpleName);
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
    
    public Part getPart() {
    	return et;
    }

}
