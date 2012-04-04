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
package org.eclipse.edt.compiler.binding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author winghong
 */
public class FileBinding extends PartBinding {

    private IPackageBinding declaringPackage;
    private List packageBindings;
    private List partBindings;
    
    public FileBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
    
    public IPackageBinding getDeclaringPackage() {
        return declaringPackage;
    }
    
    public void setDeclaringPackage(IPackageBinding declaringPackage) {
        this.declaringPackage = declaringPackage;
    }

    public List getPackageBindings() {
        if(packageBindings == null) {
            packageBindings = new ArrayList();
        }
        return packageBindings;
    }

    public List getPartBindings() {
        if(partBindings == null) {
            partBindings = new ArrayList();
        }
        return partBindings;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IPartBinding#clear()
     */
    public void clear() {
    	super.clear();
        packageBindings = null;
        partBindings = null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.IPartBinding#isStructurallyEqual(org.eclipse.edt.compiler.binding.IPartBinding)
     */
    public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.edt.compiler.binding.ITypeBinding#getKind()
     */
    public int getKind() {
        return ITypeBinding.FILE_BINDING;
    }
    
    public boolean isDeclarablePart() {
		return false;
	}

	@Override
	public ITypeBinding primGetNullableInstance() {
		return this;
	}
}
