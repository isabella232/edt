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
import org.eclipse.edt.mof.egl.SizeOfExpression;

public class SizeOfExpressionImpl extends BuiltInOperationExpressionImpl implements SizeOfExpression {
	private static int Slot_Expression=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + BuiltInOperationExpressionImpl.totalSlots();
	}
	
	static {
		int offset = BuiltInOperationExpressionImpl.totalSlots();
		Slot_Expression += offset;
	}
	@Override
	public Expression getExpression() {
		return (Expression)slotGet(Slot_Expression);
	}
	
	@Override
	public void setExpression(Expression value) {
		slotSet(Slot_Expression, value);
	}
	
}
