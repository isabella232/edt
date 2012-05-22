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

import org.eclipse.edt.mof.egl.FreeSqlStatement;

public class FreeSqlStatementImpl extends StatementImpl implements FreeSqlStatement {
	private static int Slot_preparedStatementID=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_preparedStatementID += offset;
	}
	@Override
	public String getPreparedStatementID() {
		return (String)slotGet(Slot_preparedStatementID);
	}
	
	@Override
	public void setPreparedStatementID(String value) {
		slotSet(Slot_preparedStatementID, value);
	}
	
}
