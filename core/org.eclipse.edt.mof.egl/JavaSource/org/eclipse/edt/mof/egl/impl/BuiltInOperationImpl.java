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

import org.eclipse.edt.mof.egl.BuiltInOperation;

public class BuiltInOperationImpl extends OperationImpl implements BuiltInOperation {
	private static int Slot_operator=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + OperationImpl.totalSlots();
	}
	
	static {
		int offset = OperationImpl.totalSlots();
		Slot_operator += offset;
	}
	@Override
	public String getOperator() {
		return (String)slotGet(Slot_operator);
	}
	
	@Override
	public void setOperator(String value) {
		slotSet(Slot_operator, value);
	}
	
}
