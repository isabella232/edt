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
package org.eclipse.edt.compiler.binding;

import org.eclipse.edt.compiler.internal.core.lookup.IEnvironment;
import org.eclipse.edt.mof.utils.NameUtile;


/**
 * @author winghong
 */
public abstract class PartBinding extends TypeBinding implements IPartBinding {

    protected String caseSenstivePackageName;
    private String packageName;

    protected transient IEnvironment environment;
    protected boolean isValid; 
          
    public PartBinding(String caseSenstivePackageName, String caseSensitiveInternedName) {
        super(caseSensitiveInternedName);
        this.caseSenstivePackageName = caseSenstivePackageName;
    }
        
    public boolean isPartBinding(){
    	return true;
    }
    
    public IEnvironment getEnvironment() {
        return environment;
    }
    
    public void setEnvironment(IEnvironment environment) {
        this.environment = environment;
    }
        
    public boolean isValid() {
        return isValid;
    }
    
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
        
    public String getPackageName() {
    	if (packageName == null) {
    		packageName = NameUtile.getAsName(caseSenstivePackageName);
    	}
        return packageName;
    }
    
    @Override
    public String getCaseSenstivePackageName() {
    	return caseSenstivePackageName;
    }
    
    @Override
    public boolean isPrivate() {
    	return false;
    }

}
