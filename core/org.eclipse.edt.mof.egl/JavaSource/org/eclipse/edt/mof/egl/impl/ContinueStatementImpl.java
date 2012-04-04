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

import org.eclipse.edt.mof.egl.ContinueStatement;

public class ContinueStatementImpl extends LabelStatementImpl implements ContinueStatement {
	private static int Slot_continueType=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + LabelStatementImpl.totalSlots();
	}
	
	static {
		int offset = LabelStatementImpl.totalSlots();
		Slot_continueType += offset;
	}
	@Override
	public Integer getContinueType() {
		return (Integer)slotGet(Slot_continueType);
	}
	
	@Override
	public void setContinueType(Integer value) {
		slotSet(Slot_continueType, value);
	}
	
	
	@Override
	public Boolean isContinueFor() {
		return getContinueType() == CONTINUE_FOR;
	}
	
	@Override
	public Boolean isContinueForeach() {
		return getContinueType() == CONTINUE_FOREACH;
	}
	
	@Override
	public Boolean isContinueWhile() {
		return getContinueType() == CONTINUE_WHILE;
	}
	
	@Override
	public Boolean isContinueOpenUI() {
		return getContinueType() == CONTINUE_OPENUI;
	}
}
