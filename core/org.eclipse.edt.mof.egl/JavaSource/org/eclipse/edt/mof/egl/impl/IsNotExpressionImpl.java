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
import org.eclipse.edt.mof.egl.IsNotExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public class IsNotExpressionImpl extends ExpressionImpl implements IsNotExpression {
	private static int Slot_operation=0;
	private static int Slot_expr=1;
	private static int Slot_mnemonic=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_operation += offset;
		Slot_expr += offset;
		Slot_mnemonic += offset;
	}
	@Override
	public String getOperation() {
		return (String)slotGet(Slot_operation);
	}
	
	@Override
	public void setOperation(String value) {
		slotSet(Slot_operation, value);
	}
	
	@Override
	public Expression getExpr() {
		return (Expression)slotGet(Slot_expr);
	}
	
	@Override
	public void setExpr(Expression value) {
		slotSet(Slot_expr, value);
	}
	
	@Override
	public String getMnemonic() {
		return (String)slotGet(Slot_mnemonic);
	}
	
	@Override
	public void setMnemonic(String value) {
		slotSet(Slot_mnemonic, value);
	}

	@Override
	public Type getType() {
		return TypeUtils.Type_BOOLEAN;
	}
	
}
