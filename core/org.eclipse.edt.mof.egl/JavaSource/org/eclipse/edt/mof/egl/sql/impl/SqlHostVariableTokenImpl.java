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

import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.sql.SqlHostVariableToken;


public class SqlHostVariableTokenImpl extends SqlTokenImpl implements SqlHostVariableToken {
	private static int Slot_hostVarExpression=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + SqlTokenImpl.totalSlots();
	}
	
	static {
		int offset = SqlTokenImpl.totalSlots();
		Slot_hostVarExpression += offset;
	}
	@Override
	public LHSExpr getHostVarExpression() {
		return (LHSExpr)slotGet(Slot_hostVarExpression);
	}
	
	@Override
	public void setHostVarExpression(LHSExpr value) {
		slotSet(Slot_hostVarExpression, value);
	}
	
	@Override
	public Member getMember() {
		return (Member)((Name)getHostVarExpression()).getNamedElement();
	}
	

}
