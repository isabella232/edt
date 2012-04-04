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

/**
 * @author Harmon
 */
public class SystemEnumerationTypeBinding extends EnumerationTypeBinding {
   
    int constantValue;
    /**
     * @param packageName
     * @param simpleName
     */
    public SystemEnumerationTypeBinding(String[] packageName, String caseSensitiveInternedName, int constantValue) {
        super(packageName, caseSensitiveInternedName);
        this.constantValue = constantValue;
    }
    
    private SystemEnumerationTypeBinding(SystemEnumerationTypeBinding old) {
    	super(old);
    	this.constantValue = old.constantValue;  	
    }
    
    public boolean isSystemEnumerationType() {
        return true;
    }
    
    public int getConstantValue() {
        return constantValue;
    }
    
    public boolean isSystemPart() {
    	return true;
    }
    
	@Override
	public ITypeBinding primGetNullableInstance() {
		SystemEnumerationTypeBinding nullable = new SystemEnumerationTypeBinding(this);
		nullable.setNullable(true);
		return nullable;
	}

}
