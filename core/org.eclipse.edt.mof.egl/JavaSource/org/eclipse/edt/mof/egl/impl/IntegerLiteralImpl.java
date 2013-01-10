/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public class IntegerLiteralImpl extends NumericLiteralImpl implements IntegerLiteral {
	
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
			slotSet(Slot_type, IRUtils.getEGLPrimitiveType(Type_Int));
		}
		return (Type)slotGet(Slot_type);
	}
	
	@Override
	public void setType(Type value) {
		slotSet(Slot_type, value);
	}
	
	@Override
	public Object getObjectValue() {
		Type type = getType();
		if (type.equals(TypeUtils.Type_SMALLINT)) {
			return new Short( getValue() );
		} else if (type.equals(TypeUtils.Type_INT)) {
			return new Integer( getValue() );
		} else {
			return new Long( getValue() );
		}
	}
}
