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

import org.eclipse.edt.compiler.binding.IDataBinding;
import org.eclipse.edt.compiler.binding.IFunctionBinding;
import org.eclipse.edt.compiler.binding.IPackageBinding;
import org.eclipse.edt.compiler.binding.ITypeBinding;

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

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.Scope#findType(java.lang.String)
     */
    public ITypeBinding findType(String simpleName) {
        return IDataBinding.NOT_FOUND_BINDING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.Scope#findFunction(java.lang.String)
     */
    public IFunctionBinding findFunction(String simpleName) {
        return IDataBinding.NOT_FOUND_BINDING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.Scope#findData(java.lang.String)
     */
    public IDataBinding findData(String simpleName) {
        return IDataBinding.NOT_FOUND_BINDING;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.internal.core.lookup.Scope#findPackage(java.lang.String)
     */
    public IPackageBinding findPackage(String simpleName) {
        return IDataBinding.NOT_FOUND_BINDING;
    }

}
