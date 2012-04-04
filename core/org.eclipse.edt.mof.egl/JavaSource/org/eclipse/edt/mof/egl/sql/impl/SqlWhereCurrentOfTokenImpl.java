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

import org.eclipse.edt.mof.egl.sql.SqlWhereCurrentOfToken;

public class SqlWhereCurrentOfTokenImpl extends SqlTokenImpl implements SqlWhereCurrentOfToken {
	private static int Slot_resultSetIdentifier=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + SqlTokenImpl.totalSlots();
	}
	
	static {
		int offset = SqlTokenImpl.totalSlots();
		Slot_resultSetIdentifier += offset;
	}
	@Override
	public String getResultSetIdentifier() {
		return (String)slotGet(Slot_resultSetIdentifier);
	}
	
	@Override
	public void setResultSetIdentifier(String value) {
		slotSet(Slot_resultSetIdentifier, value);
	}
	
}
