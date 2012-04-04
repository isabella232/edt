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

import org.eclipse.edt.mof.egl.TextTypeLiteral;

public abstract class TextTypeLiteralImpl extends PrimitiveTypeLiteralImpl implements TextTypeLiteral {
	private static int Slot_isHex=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + PrimitiveTypeLiteralImpl.totalSlots();
	}
	
	static {
		int offset = PrimitiveTypeLiteralImpl.totalSlots();
		Slot_isHex += offset;
	}
	@Override
	public Boolean isHex() {
		return (Boolean)slotGet(Slot_isHex);
	}
	
	@Override
	public void setIsHex(Boolean value) {
		slotSet(Slot_isHex, value);
	}
	
	@Override
	public Object getObjectValue() {
		return getValue();
	}
}
