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

import org.eclipse.edt.mof.egl.IfStatement;
import org.eclipse.edt.mof.egl.Statement;

public class IfStatementImpl extends ConditionalStatementImpl implements IfStatement {
	private static int Slot_trueBranch=0;
	private static int Slot_falseBranch=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + ConditionalStatementImpl.totalSlots();
	}
	
	static {
		int offset = ConditionalStatementImpl.totalSlots();
		Slot_trueBranch += offset;
		Slot_falseBranch += offset;
	}
	@Override
	public Statement getTrueBranch() {
		return (Statement)slotGet(Slot_trueBranch);
	}
	
	@Override
	public void setTrueBranch(Statement value) {
		slotSet(Slot_trueBranch, value);
	}
	
	@Override
	public Statement getFalseBranch() {
		return (Statement)slotGet(Slot_falseBranch);
	}
	
	@Override
	public void setFalseBranch(Statement value) {
		slotSet(Slot_falseBranch, value);
	}
	
}
