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

import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.PrimitiveTypeLiteral;

public class ConstantFieldImpl extends FieldImpl implements ConstantField {
	private static int Slot_value=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + FieldImpl.totalSlots();
	}
	
	static {
		int offset = FieldImpl.totalSlots();
		Slot_value += offset;
	}
	@Override
	public PrimitiveTypeLiteral getValue() {
		return (PrimitiveTypeLiteral)slotGet(Slot_value);
	}
	
	@Override
	public void setValue(PrimitiveTypeLiteral value) {
		slotSet(Slot_value, value);
	}
	
}
