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
package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.compiler.internal.util.BindingUtil;
import org.eclipse.edt.mof.egl.utils.InternUtil;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author winghong
 */
public class PackageBinding extends Binding implements IPackageBinding {

    private PackageBinding parent;
    private IEnvironment environment;
    private PackageBinding[] subPackages;
    
    private String packageName;
    private String caseSensitivePackageName;
    
    public PackageBinding(String caseSensitivePackageName, PackageBinding parent, IEnvironment environment) {
        super(BindingUtil.getLastSegment(caseSensitivePackageName));
        this.caseSensitivePackageName = caseSensitivePackageName;
        this.parent = parent;
        this.environment = environment;
    }
    
    public String getPackageName() {
    	if (packageName == null) {
    		packageName = NameUtile.getAsName(getCaseSensitivePackageName());
    	}
        return packageName;
    }
    
    public String getCaseSensitivePackageName() {
		return caseSensitivePackageName;
	}
    
    public PackageBinding getParent() {
        return parent;
    }
    
    private PackageBinding addPackage(String packageName) {
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
                if(NameUtile.equals(subPackages[i].getName(), simpleName)) {
                    return subPackages[i];
                }
            }
        }
        
        // Ask environment if the sub package actually exists, if it does, create a new PackageBinding for it
        String subPackageName;
        if (packageName.length() > 0) {
        	subPackageName = packageName + "." + simpleName;
        }
        else {
        	subPackageName = simpleName;
        }
        if(environment.hasPackage(NameUtile.getAsName(subPackageName))) {
            return addPackage(subPackageName);
        }
 
        // No such sub package exists
        return null;
    }

    public IPartBinding resolveType(String simpleName) {
        return environment.getPartBinding(packageName, simpleName);
    }

    public boolean isPackageBinding() {
        return true;
    }

}
