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

import java.util.List;

import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

/**
 * @author Harmon
 */
public class NullScope extends Scope {
    
    public final static NullScope INSTANCE = new NullScope(null);

    /**
     * @param parentScope
     */
    private NullScope(Scope parentScope) {
        super(parentScope);
    }

    public IPackageBinding findPackage(String simpleName) {
        return null;
    }

	@Override
	public List<Type> findType(String simpleName) {
		return null;
	}

	@Override
	public List<Member> findMember(String simpleName) {
		return null;
	}

}
