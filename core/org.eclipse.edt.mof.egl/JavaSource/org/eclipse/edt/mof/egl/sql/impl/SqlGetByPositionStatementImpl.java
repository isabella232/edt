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
package org.eclipse.edt.mof.egl.sql.impl;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.GetByPositionKind;
import org.eclipse.edt.mof.egl.sql.SqlGetByPositionStatement;


public class SqlGetByPositionStatementImpl extends SqlIOStatementImpl implements SqlGetByPositionStatement {
	private static int Slot_directive=0;
	private static int Slot_position=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + SqlIOStatementImpl.totalSlots();
	}
	
	static {
		int offset = SqlIOStatementImpl.totalSlots();
		Slot_directive += offset;
		Slot_position += offset;
	}
	@Override
	public GetByPositionKind getDirective() {
		return (GetByPositionKind)slotGet(Slot_directive);
	}
	
	@Override
	public void setDirective(GetByPositionKind value) {
		slotSet(Slot_directive, value);
	}
	
	@Override
	public Expression getPosition() {
		return (Expression)slotGet(Slot_position);
	}
	
	@Override
	public void setPosition(Expression value) {
		slotSet(Slot_position, value);
	}
	
	@Override 
	public String getDirectiveString() {
		return getDirective().name();
	}
	
}
