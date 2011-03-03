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

import org.eclipse.edt.compiler.core.ast.Primitive;

public class NullablePrimitiveTypeBinding extends PrimitiveTypeBinding implements INullableTypeBinding {
	
	private PrimitiveTypeBinding primTypeBinding;

	public NullablePrimitiveTypeBinding(PrimitiveTypeBinding primTypeBinding) {
		super(primTypeBinding.getCaseSensitiveName());
		this.primTypeBinding = primTypeBinding;
	}
	
	public ITypeBinding getValueType() {
		return primTypeBinding;
	}
	
	public boolean isNullable() {
		return true;
	}
	
	public ITypeBinding getNullableInstance() {
		return this;
	}

	public Primitive getPrimitive() {
		return primTypeBinding.getPrimitive();
	}

	public int getLength() {
		return primTypeBinding.getLength();
	}

	public String getPattern() {
		return primTypeBinding.getPattern();
	}

	public int getDecimals() {
		return primTypeBinding.getDecimals();
	}

	public String getTimeStampOrIntervalPattern() {
		return primTypeBinding.getTimeStampOrIntervalPattern();
	}

	public int getKind() {
		return primTypeBinding.getKind();
	}

	public int getBytes() {
		return primTypeBinding.getBytes();
	}

	public ITypeBinding copyTypeBinding() {
		return primTypeBinding.copyTypeBinding().getNullableInstance();
	}
	
	public boolean isDynamic() {
		return primTypeBinding.isDynamic();
	}
	
    public ITypeBinding getBaseType() {
        return primTypeBinding.getBaseType();
    }
    
    private Object readResolve() {
    	return primTypeBinding.getNullableInstance();
    }
}
