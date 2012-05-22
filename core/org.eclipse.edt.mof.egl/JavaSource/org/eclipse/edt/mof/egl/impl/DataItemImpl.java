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

import org.eclipse.edt.mof.egl.DataItem;
import org.eclipse.edt.mof.egl.Type;

public class DataItemImpl extends PartImpl implements DataItem {
	private static int Slot_baseType=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + PartImpl.totalSlots();
	}
	
	static {
		int offset = PartImpl.totalSlots();
		Slot_baseType += offset;
	}
	@Override
	public Type getBaseType() {
		return (Type)slotGet(Slot_baseType);
	}
	
	@Override
	public void setBaseType(Type value) {
		slotSet(Slot_baseType, value);
	}
	
}
