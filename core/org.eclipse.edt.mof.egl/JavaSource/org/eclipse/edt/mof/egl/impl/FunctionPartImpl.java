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

import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionPart;

public class FunctionPartImpl extends LogicAndDataPartImpl implements FunctionPart {
	private static int Slot_function=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + LogicAndDataPartImpl.totalSlots();
	}
	
	static {
		int offset = LogicAndDataPartImpl.totalSlots();
		Slot_function += offset;
	}
	@Override
	public Function getFunction() {
		return (Function)slotGet(Slot_function);
	}
	
	@Override
	public void setFunction(Function value) {
		slotSet(Slot_function, value);
	}
	
}
