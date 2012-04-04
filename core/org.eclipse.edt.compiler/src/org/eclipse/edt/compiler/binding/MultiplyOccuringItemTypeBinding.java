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

import org.eclipse.edt.mof.egl.utils.InternUtil;

/**
 * @author Dave Murray
 */
public class MultiplyOccuringItemTypeBinding extends TypeBinding {
	ITypeBinding elementType;
	
	public MultiplyOccuringItemTypeBinding(ITypeBinding elementType) {
		super(InternUtil.internCaseSensitive("occuring item"));
		this.elementType = elementType;
	}

	public int getKind() {
		return MULTIPLY_OCCURING_ITEM;
	}

	public ITypeBinding getBaseType() {
		return elementType;
	}
	
	@Override
	public ITypeBinding primGetNullableInstance() {
		MultiplyOccuringItemTypeBinding nullable = new MultiplyOccuringItemTypeBinding(elementType);
		nullable.setNullable(true);
		return nullable;
	}

}
