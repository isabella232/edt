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
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public class FloatingPointLiteralImpl extends NumericLiteralImpl implements FloatingPointLiteral {
	private static int Slot_type=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + NumericLiteralImpl.totalSlots();
	}
	
	static {
		int offset = NumericLiteralImpl.totalSlots();
		Slot_type += offset;
	}
	
	@Override
	public Type getType() {
		if (slotGet(Slot_type) == null) {
			slotSet(Slot_type, IRUtils.getEGLPrimitiveType(Type_Float));
		}
		return (Type)slotGet(Slot_type);
	}
	
	@Override
	public void setType(Type value) {
		slotSet(Slot_type, value);
	}
	
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
	public Object getObjectValue() {
		Type type = getType();
		if (type.equals(TypeUtils.Type_SMALLFLOAT)) {
			return Float.valueOf(getValue());
		}
		return getFloatValue();
	}
}
