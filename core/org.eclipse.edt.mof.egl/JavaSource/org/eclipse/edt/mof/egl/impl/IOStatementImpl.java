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

import java.util.List;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.IOStatement;


public abstract class IOStatementImpl extends StatementImpl implements IOStatement {
	private static int Slot_targets=0;
	private static int Slot_dataSource=1;
	private static int Slot_usingExpressions=2;
	private static int Slot_usingKeyExpressions=3;
	private static int totalSlots = 4;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_targets += offset;
		Slot_dataSource += offset;
		Slot_usingExpressions += offset;
		Slot_usingKeyExpressions += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Expression> getTargets() {
		return (List<Expression>)slotGet(Slot_targets);
	}
	
	@Override
	public Expression getDataSource() {
		return (Expression)slotGet(Slot_dataSource);
	}
	
	@Override
	public void setDataSource(Expression value) {
		slotSet(Slot_dataSource, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Expression> getUsingExpressions() {
		return (List<Expression>)slotGet(Slot_usingExpressions);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Expression> getUsingKeyExpressions() {
		return (List<Expression>)slotGet(Slot_usingKeyExpressions);
	}
	
	
	@Override
	public Expression getTarget() {
		return getTargets().isEmpty() ? null : getTargets().get(0);
	}
}
