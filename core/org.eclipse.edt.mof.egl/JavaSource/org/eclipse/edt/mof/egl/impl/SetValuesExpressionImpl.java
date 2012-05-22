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

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.SetValuesExpression;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.Type;

public class SetValuesExpressionImpl extends ExpressionImpl implements SetValuesExpression {
	private static int Slot_target=0;
	private static int Slot_settings=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_target += offset;
		Slot_settings += offset;
	}
	@Override
	public Expression getTarget() {
		return (Expression)slotGet(Slot_target);
	}
	
	@Override
	public void setTarget(Expression value) {
		slotSet(Slot_target, value);
	}
	
	@Override
	public StatementBlock getSettings() {
		return (StatementBlock)slotGet(Slot_settings);
	}
	
	@Override
	public void setSettings(StatementBlock value) {
		slotSet(Slot_settings, value);
	}

	@Override
	public Type getType() {
		return getTarget().getType();
	}
	
}
