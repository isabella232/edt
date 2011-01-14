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

import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.Statement;

public abstract class StatementImpl extends ElementImpl implements Statement {
	private static int Slot_functionMember=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + ElementImpl.totalSlots();
	}
	
	static {
		int offset = ElementImpl.totalSlots();
		Slot_functionMember += offset;
	}
	@Override
	public FunctionMember getFunctionMember() {
		return (FunctionMember)slotGet(Slot_functionMember);
	}
	
	@Override
	public void setFunctionMember(FunctionMember value) {
		slotSet(Slot_functionMember, value);
	}
	
}