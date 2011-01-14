/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl.sql.impl;

import org.eclipse.edt.mof.egl.sql.SqlOpenStatement;

public class SqlOpenStatementImpl extends SqlGetByKeyStatementImpl implements SqlOpenStatement {
	private static int Slot_isHold=0;
	private static int Slot_isScroll=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + SqlGetByKeyStatementImpl.totalSlots();
	}
	
	static {
		int offset = SqlGetByKeyStatementImpl.totalSlots();
		Slot_isHold += offset;
		Slot_isScroll += offset;
	}
	@Override
	public Boolean isHold() {
		return (Boolean)slotGet(Slot_isHold);
	}
	
	@Override
	public void setIsHold(Boolean value) {
		slotSet(Slot_isHold, value);
	}
	
	@Override
	public Boolean isScroll() {
		return (Boolean)slotGet(Slot_isScroll);
	}
	
	@Override
	public void setIsScroll(Boolean value) {
		slotSet(Slot_isScroll, value);
	}
	
}