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



import java.util.List;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;

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
    
    public abstract List<Type> findType(String simpleName);
        
    public abstract List<Member> findMember(String simpleName);
    
    public abstract IPackageBinding findPackage(String simpleName);
    
    public Scope getScopeForKeywordThis() {
    	return null;
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
    
    public Part getPart() {
    	return null;
    }

	public EnvironmentScope getEnvironmentScope() {
		return parentScope.getEnvironmentScope();
	}
	
	public Type getType() {
		return null;
	}
}
