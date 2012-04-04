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
package org.eclipse.edt.mof.egl.sql.impl;

import org.eclipse.edt.mof.egl.impl.ElementImpl;
import org.eclipse.edt.mof.egl.sql.SqlToken;


public abstract class SqlTokenImpl extends ElementImpl implements SqlToken {
	private static int Slot_sqlString=0;
	private static int Slot_string=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + ElementImpl.totalSlots();
	}
	
	static {
		int offset = ElementImpl.totalSlots();
		Slot_sqlString += offset;
		Slot_string += offset;
	}
	@Override
	public String getSqlString() {
		return (String)slotGet(Slot_sqlString);
	}
	
	@Override
	public void setSqlString(String value) {
		slotSet(Slot_sqlString, value);
	}
	
	@Override
	public String getString() {
		return (String)slotGet(Slot_string);
	}
	
	@Override
	public void setString(String value) {
		slotSet(Slot_string, value);
	}
	
}
