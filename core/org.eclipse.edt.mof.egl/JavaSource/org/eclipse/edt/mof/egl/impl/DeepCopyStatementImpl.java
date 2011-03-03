/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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

import org.eclipse.edt.mof.egl.DeepCopyStatement;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.LHSExpr;

public class DeepCopyStatementImpl extends StatementImpl implements DeepCopyStatement {
	private static int Slot_source=0;
	private static int Slot_target=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_source += offset;
		Slot_target += offset;
	}
	@Override
	public Expression getSource() {
		return (Expression)slotGet(Slot_source);
	}
	
	@Override
	public void setSource(Expression value) {
		slotSet(Slot_source, value);
	}
	
	@Override
	public LHSExpr getTarget() {
		return (LHSExpr)slotGet(Slot_target);
	}
	
	@Override
	public void setTarget(LHSExpr value) {
		slotSet(Slot_target, value);
	}
	
}
