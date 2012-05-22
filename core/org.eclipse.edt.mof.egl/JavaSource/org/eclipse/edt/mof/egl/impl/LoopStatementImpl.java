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

import org.eclipse.edt.mof.egl.LoopStatement;
import org.eclipse.edt.mof.egl.Statement;

public abstract class LoopStatementImpl extends ConditionalStatementImpl implements LoopStatement {
	private static int Slot_body=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + ConditionalStatementImpl.totalSlots();
	}
	
	static {
		int offset = ConditionalStatementImpl.totalSlots();
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
