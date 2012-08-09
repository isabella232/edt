/*******************************************************************************
 * Copyright © 2005, 2012 IBM Corporation and others.
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
import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

public class DelegateScope extends Scope {
	private Delegate delegate;
    public DelegateScope(Scope parentScope, Delegate delegate) {
        super(parentScope);
        this.delegate = delegate;
    }

    public List<Member> findMember(String simpleName) {
        return parentScope.findMember(simpleName);
    }

    public IPackageBinding findPackage(String simpleName) {
        return parentScope.findPackage(simpleName);
    }

    public List<Type> findType(String simpleName) {
        return parentScope.findType(simpleName);
    }
    
    public Delegate getPart() {
    	return delegate;
    }
}
