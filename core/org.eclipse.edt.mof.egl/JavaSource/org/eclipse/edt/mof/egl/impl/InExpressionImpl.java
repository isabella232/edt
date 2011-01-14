/*******************************************************************************
 * Copyright © 2010 IBM Corporation and others.
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
import org.eclipse.edt.mof.egl.InExpression;

public class InExpressionImpl extends BinaryExpressionImpl implements InExpression {
	private static int Slot_from=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + BinaryExpressionImpl.totalSlots();
	}
	
	static {
		int offset = BinaryExpressionImpl.totalSlots();
		Slot_from += offset;
	}
	@Override
	public Expression getFrom() {
		return (Expression)slotGet(Slot_from);
	}
	
	@Override
	public void setFrom(Expression value) {
		slotSet(Slot_from, value);
	}
	
}