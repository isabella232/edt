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

import org.eclipse.edt.mof.egl.MofConversion;
import org.eclipse.edt.mof.egl.PrimitiveTypeLiteral;
import org.eclipse.edt.mof.egl.Type;


public abstract class PrimitiveTypeLiteralImpl extends LiteralImpl implements PrimitiveTypeLiteral, MofConversion {
	private static int Slot_value=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + LiteralImpl.totalSlots();
	}
	
	static {
		int offset = LiteralImpl.totalSlots();
		Slot_value += offset;
	}
	@Override
	public String getValue() {
		return (String)slotGet(Slot_value);
	}
	
	@Override
	public void setValue(String value) {
		slotSet(Slot_value, value);
	}
	
	@Override
	public abstract Type getType(); 

}
