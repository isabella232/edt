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

import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.mof.egl.utils.InternUtil;


/**
 * @author winghong
 */
public class PackageBinding extends Binding implements IPackageBinding {

    private PackageBinding parent;
    private IEnvironment environment;
    private PackageBinding[] subPackages;
    
    private String[] packageName;
    
    public PackageBinding(String[] packageName, PackageBinding parent, IEnvironment environment) {
        super(packageName.length == 0 ? "" : packageName[packageName.length - 1]);
        this.packageName = packageName;
        this.parent = parent;
        this.environment = environment;
    }
    
    public String[] getPackageName() {
        return packageName;
    }
    
    public PackageBinding getParent() {
        return parent;
    }
    
    private PackageBinding addPackage(String[] packageName) {
        PackageBinding packageBinding = new PackageBinding(packageName, this, environment);
        if(subPackages == null) {
            subPackages = new PackageBinding[] { packageBinding };
        }
        else {
            PackageBinding[] newSubPackages = new PackageBinding[subPackages.length + 1];
            System.arraycopy(subPackages, 0, newSubPackages, 0, subPackages.length);
            newSubPackages[subPackages.length] = packageBinding;
            subPackages = newSubPackages;
        }
        return packageBinding;
    }

    public IPackageBinding resolvePackage(String simpleName) {
        // Return existing package binding if there is one
        if(subPackages != null) {
            for(int i = 0; i < subPackages.length; i++) {
                if(subPackages[i].getName() == simpleName) {
                    return subPackages[i];
                }
            }
        }
        
        // Ask project scope if the sub package actually exists, if it does, create a new PackageBinding for it
        String[] subPackageName = new String[packageName.length + 1];
        System.arraycopy(packageName, 0, subPackageName, 0, packageName.length);
        subPackageName[packageName.length] = simpleName;
        subPackageName = InternUtil.intern(subPackageName);
        if(environment.hasPackage(subPackageName)) {
            return addPackage(subPackageName);
        }
 
        // No such sub package exists
        return IBinding.NOT_FOUND_BINDING;
    }

    public ITypeBinding resolveType(String simpleName) {
        return environment.getPartBinding(packageName, simpleName);
    }

    public boolean isPackageBinding() {
        return true;
    }

}
