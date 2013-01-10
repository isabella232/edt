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

import org.eclipse.edt.mof.egl.ConditionalStatement;
import org.eclipse.edt.mof.egl.Expression;

public abstract class ConditionalStatementImpl extends StatementImpl implements ConditionalStatement {
	private static int Slot_condition=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_condition += offset;
	}
	@Override
	public Expression getCondition() {
		return (Expression)slotGet(Slot_condition);
	}
	
	@Override
	public void setCondition(Expression value) {
		slotSet(Slot_condition, value);
	}
		
}
