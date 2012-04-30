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


public abstract class TypeBinding extends Binding implements ITypeBinding {
	
		
	public TypeBinding( String caseSensitiveInternedName ) {
		super(caseSensitiveInternedName);
	}

	public boolean isValid() {
		return true;
	}
	
    public String getPackageQualifiedName() {
    	StringBuffer result = new StringBuffer();
    	String packageName = getPackageName();
    	if(packageName != null && packageName.length() > 0) {
    		result.append(getCaseSenstivePackageName());
  			result.append('.');
    	}
    	result.append(getCaseSensitiveName());
    	return result.toString();
    }


}
