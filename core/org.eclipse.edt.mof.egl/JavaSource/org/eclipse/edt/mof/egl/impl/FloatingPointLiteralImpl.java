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
package org.eclipse.edt.mof.egl.impl;

import org.eclipse.edt.mof.egl.FloatingPointLiteral;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;


public class FloatingPointLiteralImpl extends NumericLiteralImpl implements FloatingPointLiteral {
	@Override
	public Double getFloatValue() {
		// TODO EGL syntax for float is not the same as java...
		return Double.valueOf(getValue());
	}
	
	@Override
	public void setFloatValue(Double value) {
		// TODO EGL syntax for float is not the same as java...
		if (value < 0) {
			setIsNegated(true);
			value = -value;
		}
		setValue(String.valueOf(value));
	}
	
	@Override
	public Type getType() {
		return IRUtils.getEGLPrimitiveType(Type_Float);
	}

	@Override
	public Object getObjectValue() {
		return getFloatValue();
	}
}
