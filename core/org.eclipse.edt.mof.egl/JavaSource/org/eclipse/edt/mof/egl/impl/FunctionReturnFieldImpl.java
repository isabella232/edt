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

import org.eclipse.edt.mof.egl.FunctionReturnField;

public class FunctionReturnFieldImpl extends FieldImpl implements FunctionReturnField {
	private static int Slot_isDefinedSqlNullable=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + FieldImpl.totalSlots();
	}
	
	static {
		int offset = FieldImpl.totalSlots();
		Slot_isDefinedSqlNullable += offset;
	}
	@Override
	public Boolean isDefinedSqlNullable() {
		return (Boolean)slotGet(Slot_isDefinedSqlNullable);
	}
	
	@Override
	public void setIsDefinedSqlNullable(Boolean value) {
		slotSet(Slot_isDefinedSqlNullable, value);
	}
	
}
