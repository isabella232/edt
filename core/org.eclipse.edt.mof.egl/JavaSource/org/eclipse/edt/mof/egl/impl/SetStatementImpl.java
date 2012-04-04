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

import java.util.List;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.SetStatement;


public class SetStatementImpl extends StatementImpl implements SetStatement {
	private static int Slot_targets=0;
	private static int Slot_states=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_targets += offset;
		Slot_states += offset;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Expression> getTargets() {
		return (List<Expression>)slotGet(Slot_targets);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getStates() {
		return (List<String>)slotGet(Slot_states);
	}
	
}
