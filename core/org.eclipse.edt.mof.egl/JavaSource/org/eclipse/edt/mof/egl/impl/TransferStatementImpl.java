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

import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.TransferStatement;

public class TransferStatementImpl extends StatementImpl implements TransferStatement {
	private static int Slot_invocationTarget=0;
	private static int Slot_passingRecord=1;
	private static int Slot_targetType=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_invocationTarget += offset;
		Slot_passingRecord += offset;
		Slot_targetType += offset;
	}
	@Override
	public Expression getInvocationTarget() {
		return (Expression)slotGet(Slot_invocationTarget);
	}
	
	@Override
	public void setInvocationTarget(Expression value) {
		slotSet(Slot_invocationTarget, value);
	}
	
	@Override
	public Expression getPassingRecord() {
		return (Expression)slotGet(Slot_passingRecord);
	}
	
	@Override
	public void setPassingRecord(Expression value) {
		slotSet(Slot_passingRecord, value);
	}
	
	@Override
	public Integer getTargetType() {
		return (Integer)slotGet(Slot_targetType);
	}
	
	@Override
	public void setTargetType(Integer value) {
		slotSet(Slot_targetType, value);
	}
	
	@Override
	public String getLinkageKey() {
		Annotation linkagekey = getAnnotation("linkageKey");
		return linkagekey != null ? (String)linkagekey.getValue() : null;
	}
	
	@Override
	public Boolean isExternal() {
		Annotation isExternal = getAnnotation("isExternal");
		return isExternal != null ? (Boolean)isExternal.getValue() : false;
	}


}
