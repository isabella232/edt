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

import org.eclipse.edt.mof.egl.Operation;

public class OperationImpl extends FunctionImpl implements Operation {
	private static int Slot_opSymbol=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + FunctionImpl.totalSlots();
	}
	
	static {
		int offset = FunctionImpl.totalSlots();
		Slot_opSymbol += offset;
	}
	
	
	@Override
	public String getOpSymbol() {
		return (String)slotGet(Slot_opSymbol);
	}

	@Override
	public void setOpSymbol(String opSymbol) {
		slotSet(Slot_opSymbol, opSymbol);
	}

	@Override
	public boolean isWidenConversion() {
		return getOpSymbol().equalsIgnoreCase("widen");
	}
	
	@Override
	public boolean isNarrowConversion() {
		return getOpSymbol().equalsIgnoreCase("narrow");
	}
}
