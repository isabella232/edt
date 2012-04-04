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
import org.eclipse.edt.mof.egl.ForwardStatement;


public class ForwardStatementImpl extends StatementImpl implements ForwardStatement {
	private static int Slot_forwardToTarget=0;
	private static int Slot_arguments=1;
	private static int Slot_forwardToOption=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_forwardToTarget += offset;
		Slot_arguments += offset;
		Slot_forwardToOption += offset;
	}
	@Override
	public Expression getForwardToTarget() {
		return (Expression)slotGet(Slot_forwardToTarget);
	}
	
	@Override
	public void setForwardToTarget(Expression value) {
		slotSet(Slot_forwardToTarget, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Expression> getArguments() {
		return (List<Expression>)slotGet(Slot_arguments);
	}
	
	@Override
	public Integer getForwardToOption() {
		return (Integer)slotGet(Slot_forwardToOption);
	}
	
	@Override
	public void setForwardToOption(Integer value) {
		slotSet(Slot_forwardToOption, value);
	}
	
	
	@Override
	public Boolean isForwardToLabel() {
		// TODO: Default generated implementation
		return false;
	}
	
	@Override
	public Boolean isForwardToURL() {
		// TODO: Default generated implementation
		return false;
	}
}
