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

import java.util.Collections;
import java.util.Map;

/**
 * @author Dave Murray
 */
public abstract class TypeBinding extends Binding implements ITypeBinding {
	
	private String[] packageName;
	private boolean isReference;
	
	public TypeBinding( String caseSensitiveInternedName, String[] packageName ) {
		super(caseSensitiveInternedName);
		this.packageName = packageName;
	}
	
	public TypeBinding( String caseSensitiveInternedName ) {
		super(caseSensitiveInternedName);
	}

	public boolean isValid() {
		return true;
	}

	public String[] getPackageName() {
		return packageName;
	}

	public IDataBinding findData(String simpleName) {
		return IBinding.NOT_FOUND_BINDING;
	}
	
	public IDataBinding findPublicData(String simpleName) {
		return findData(simpleName);
	}
	
	public Map getSimpleNamesToDataBindingsMap() {
		return Collections.EMPTY_MAP;
	}
	
	public IFunctionBinding findFunction(String simpleName) {
		return IBinding.NOT_FOUND_BINDING;
	}
	
	public IFunctionBinding findPublicFunction(String simpleName) {
		return findFunction(simpleName);
	}
	
	public boolean isReference() {
		return isReference;
	}
	
	public boolean isDynamic() {
		return false;
	}
	
	public boolean isDynamicallyAccessible() {
		return isDynamic();
	}

	public boolean isReferentiallyEqual(ITypeBinding anotherTypeBinding) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPackageBinding() {
		return false;
	}

	public boolean isFunctionBinding() {
		return false;
	}

	public boolean isTypeBinding() {
		return true;
	}

	public boolean isDataBinding() {
		return false;
	}

	public boolean isAnnotationBinding() {
		return false;
	}
	
	public boolean isPartBinding(){
    	return false;
    }  
    
    public ITypeBinding copyTypeBinding() {
		throw new UnsupportedOperationException( "copyTypeBinding() not overriden for " + getClass().getName() );
	}
    
    public boolean isNullable() {
    	return false;
    }
    
    public ITypeBinding getNullableInstance() {
    	return this;
    }
    
    public String getPackageQualifiedName() {
    	StringBuffer result = new StringBuffer();
    	String[] packageName = getPackageName();
    	if(packageName != null) {
    		for(int i = 0; i < packageName.length; i++) {
    			result.append(packageName[i]);
    			result.append('.');
    		}    		
    	}
    	result.append(getCaseSensitiveName());
    	return result.toString();
    }
}
