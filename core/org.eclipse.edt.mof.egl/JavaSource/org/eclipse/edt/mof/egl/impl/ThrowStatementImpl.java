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
import org.eclipse.edt.mof.egl.ThrowStatement;

public class ThrowStatementImpl extends StatementImpl implements ThrowStatement {
	private static int Slot_exception=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_exception += offset;
	}
	@Override
	public Expression getException() {
		return (Expression)slotGet(Slot_exception);
	}
	
	@Override
	public void setException(Expression value) {
		slotSet(Slot_exception, value);
	}
	
}
