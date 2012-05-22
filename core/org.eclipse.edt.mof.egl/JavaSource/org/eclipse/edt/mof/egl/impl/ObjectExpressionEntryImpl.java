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
import org.eclipse.edt.mof.egl.ObjectExpressionEntry;
import org.eclipse.edt.mof.egl.Type;


public class ObjectExpressionEntryImpl extends ElementImpl implements ObjectExpressionEntry{
	private static int Slot_id=0;
	private static int Slot_expression=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + ElementImpl.totalSlots();
	}
	
	static {
		int offset = ElementImpl.totalSlots();
		Slot_id += offset;
		Slot_expression += offset;
	}

	@Override
	public String getId() {
		return (String)slotGet(Slot_id);
	}

	@Override
	public Expression getExpression() {
		return (Expression)slotGet(Slot_expression);
	}
	
	@Override
	public void setId(String value) {
		slotSet(Slot_id, value);
	}
	
	@Override
	public void setExpression(Expression value) {
		slotSet(Slot_expression, value);
	}


	
}
