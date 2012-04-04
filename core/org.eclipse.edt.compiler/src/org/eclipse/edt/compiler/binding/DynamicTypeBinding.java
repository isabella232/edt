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
public class DynamicTypeBinding extends TypeBinding {
	
	public DynamicTypeBinding( String caseSensitiveInternedName ) {
		super( caseSensitiveInternedName );
	}

	public int getKind() {
		return DYNAMIC_BINDING;
	}
	
	public boolean isDynamic() {
		return true;
	}

	/**
	 *
	 */

	public IDataBinding findData(String simpleName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 *
	 */

	public boolean isReferentiallyEqual(ITypeBinding anotherTypeBinding) {
		// TODO Auto-generated method stub
		return false;
	}
	
    public ITypeBinding getBaseType() {
        return this;
    }

	@Override
	public ITypeBinding primGetNullableInstance() {
		DynamicTypeBinding nullable = new DynamicTypeBinding(getCaseSensitiveName());
		nullable.setNullable(true);
		return nullable;
	}

}
