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

import org.eclipse.edt.mof.egl.ForEachStatement;
import org.eclipse.edt.mof.egl.Statement;

public class ForEachStatementImpl extends StatementImpl implements ForEachStatement {
	private static int Slot_body=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_body += offset;
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
