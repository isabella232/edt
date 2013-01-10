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

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.PrintStatement;

public class PrintStatementImpl extends StatementImpl implements PrintStatement {
	private static int Slot_target=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_target += offset;
	}
	@Override
	public Expression getTarget() {
		return (Expression)slotGet(Slot_target);
	}
	
	@Override
	public void setTarget(Expression value) {
		slotSet(Slot_target, value);
	}
	
}
