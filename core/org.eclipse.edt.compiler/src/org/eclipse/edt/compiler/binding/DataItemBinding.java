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
 * @author Dave Murray
 */
public class DataItemBinding extends PartBinding {
	
	PrimitiveTypeBinding primitiveTypeBinding;
		
    public DataItemBinding(String[] packageName, String caseSensitiveInternedName) {
        super(packageName, caseSensitiveInternedName);
    }
    
    private DataItemBinding(DataItemBinding old) {
    	super(old);
    	primitiveTypeBinding = old.primitiveTypeBinding;
    }
    
    public PrimitiveTypeBinding getPrimitiveTypeBinding() {
    	if (isNullable() && primitiveTypeBinding != null) {
    		return (PrimitiveTypeBinding)primitiveTypeBinding.getNullableInstance();
    	}
    	return primitiveTypeBinding;
    }
    
    public void setPrimitiveTypeBinding(PrimitiveTypeBinding primitiveType) {
    	primitiveTypeBinding = primitiveType;
    }
    
	public int getKind() {
		return DATAITEM_BINDING;
	}

	public void clear() {
		super.clear();
		primitiveTypeBinding = null;
	}

	public boolean isStructurallyEqual(IPartBinding anotherPartBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isDeclarablePart() {
		return true;
	}
	
	@Override
	public ITypeBinding primGetNullableInstance() {
		DataItemBinding nullable = new DataItemBinding(this);
		nullable.setNullable(true);
		return nullable;
	}
}
