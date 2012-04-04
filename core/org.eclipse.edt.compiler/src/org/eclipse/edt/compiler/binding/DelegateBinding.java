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
import java.util.Collections;
import java.util.List;


/**
 * @author Dave Murray
 */
public class DelegateBinding extends PartBinding {
	
	protected List parameters = Collections.EMPTY_LIST;
	protected transient ITypeBinding returnType;
	protected boolean returnTypeIsSqlNullable;
	
    public DelegateBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
    
    private DelegateBinding(DelegateBinding old) {
        super(old);
    	if (old.parameters == Collections.EMPTY_LIST) {
    		old.parameters = new ArrayList();
    	}
        this.parameters = old.parameters;
        this.returnType = old.returnType;
        this.returnTypeIsSqlNullable = old.returnTypeIsSqlNullable;
    	
    }
    
    public int getKind() {
		return DELEGATE_BINDING;
	}
    
    public List getParemeters() {
    	return parameters;
    }
    
    public void addParameter(FunctionParameterBinding parameter) {
    	if(parameters == Collections.EMPTY_LIST) {
    		parameters = new ArrayList();
    	}
    	parameters.add(parameter);
    }
    
    public ITypeBinding getReturnType() {
        return realizeTypeBinding(returnType, getEnvironment());
    }
    
    public void setReturnType(ITypeBinding type) {
    	returnType = getTypeBinding(type, null);
    }
    
    public boolean returnTypeIsSqlNullable() {
        return returnTypeIsSqlNullable;
    }
    
    public void setReturnTypeIsSqlNullable(boolean returnIsNullable) {
    	this.returnTypeIsSqlNullable = returnIsNullable;
    }

	public void clear() {
		super.clear();
		parameters = Collections.EMPTY_LIST;
		returnType = null;
		returnTypeIsSqlNullable = false;
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isDeclarablePart() {
		return true;
	}
	
	public boolean isReference() {
		return true;
	}
	
	@Override
	public boolean isInstantiable() {
		return false;
	}
	
	@Override
	public ITypeBinding primGetNullableInstance() {
		DelegateBinding nullable = new DelegateBinding(this);
		nullable.setNullable(true);
		return nullable;
	}

}
