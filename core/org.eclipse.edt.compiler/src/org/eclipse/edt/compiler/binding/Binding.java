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

import org.eclipse.edt.mof.utils.NameUtile;



public abstract class Binding implements IBinding {
    
    
    protected String caseSensitiveName;
    private String name;
    
    public Binding(String caseSensitiveName) {
    	this.caseSensitiveName = caseSensitiveName;
    }
    
    public String getName() {
    	if(name == null) {
    		name = NameUtile.getAsName(caseSensitiveName);
    	}
        return name;
    }
    
    /**
     * Get a case sensitive version of the Binding's name
     * @return the case sensitive name
     */
    public String getCaseSensitiveName() {
    	return caseSensitiveName;
    }
    
    public boolean isPackageBinding() {
        return false;
    }

}
