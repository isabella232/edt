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
package org.eclipse.edt.mof.eglx.persistence.sql.impl;

import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlForEachStatement;


public class SqlForEachStatementImpl extends SqlIOStatementImpl implements SqlForEachStatement {
	private static int Slot_label=0;
	private static int Slot_body=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + SqlIOStatementImpl.totalSlots();
	}
	
	static {
		int offset = SqlIOStatementImpl.totalSlots();
		Slot_label += offset;
		Slot_body += offset;
	}
	@Override
	public String getLabel() {
		return (String)slotGet(Slot_label);
	}
	
	@Override
	public void setLabel(String value) {
		slotSet(Slot_label, value);
	}
	
	@Override
	public Statement getBody() {
		return (Statement)slotGet(Slot_body);
	}
	
	@Override
	public void setBody(Statement value) {
		slotSet(Slot_body, value);
	}
	
}
