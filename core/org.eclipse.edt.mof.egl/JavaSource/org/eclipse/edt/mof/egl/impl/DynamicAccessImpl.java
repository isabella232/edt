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

import org.eclipse.edt.mof.egl.DynamicAccess;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public class DynamicAccessImpl extends ExpressionImpl implements DynamicAccess {
	private static int Slot_access=0;
	private static int Slot_expression=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_access += offset;
		Slot_expression += offset;
	}
	@Override
	public Expression getAccess() {
		return (Expression)slotGet(Slot_access);
	}
	
	@Override
	public void setAccess(Expression value) {
		slotSet(Slot_access, value);
	}
	
	@Override
	public Expression getExpression() {
		return (Expression)slotGet(Slot_expression);
	}
	
	@Override
	public void setExpression(Expression value) {
		slotSet(Slot_expression, value);
	}

	@Override
	public Type getType() {
		return TypeUtils.Type_ANY;
	}
	
}