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
package org.eclipse.edt.mof.impl;

import org.eclipse.edt.mof.EEnumLiteral;

public class EEnumLiteralImpl extends EMemberImpl implements EEnumLiteral {
	private static int Slot_value=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + EMemberImpl.totalSlots();
	}
	
	static {
		int offset = EMemberImpl.totalSlots();
		Slot_value += offset;
	}
	@Override
	public int getValue() {
		return (Integer)slotGet(Slot_value);
	}
	
	@Override
	public void setValue(int value) {
		slotSet(Slot_value, value);
	}
	
}
