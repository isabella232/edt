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

import java.util.List;

import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.CallStatement;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.MemberAccess;


public class CallStatementImpl extends StatementImpl implements CallStatement {
	private static int Slot_invocationTarget=0;
	private static int Slot_arguments=1;
	private static int Slot_using=2;
	private static int Slot_callback=3;
	private static int Slot_errorCallback=4;
	private static int Slot_returns=5;
	private static int totalSlots = 6;
	
	public static int totalSlots() {
		return totalSlots + StatementImpl.totalSlots();
	}
	
	static {
		int offset = StatementImpl.totalSlots();
		Slot_invocationTarget += offset;
		Slot_arguments += offset;
		Slot_using += offset;
		Slot_callback += offset;
		Slot_errorCallback += offset;
		Slot_returns += offset;
	}
	@Override
	public Expression getInvocationTarget() {
		return (Expression)slotGet(Slot_invocationTarget);
	}
	
	@Override
	public void setInvocationTarget(Expression value) {
		slotSet(Slot_invocationTarget, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Expression> getArguments() {
		return (List<Expression>)slotGet(Slot_arguments);
	}
	
	@Override
	public Expression getCallback() {
		return (Expression)slotGet(Slot_callback);
	}
	
	@Override
	public void setCallback(Expression value) {
		slotSet(Slot_callback, value);
	}
	
	@Override
	public Expression getErrorCallback() {
		return (Expression)slotGet(Slot_errorCallback);
	}
	
	@Override
	public void setErrorCallback(Expression value) {
		slotSet(Slot_errorCallback, value);
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

	@Override
	public Expression getUsing() {
		return (Expression)slotGet(Slot_using);
	}

	@Override
	public void setUsing(Expression value) {
		slotSet(Slot_using, value);
	}

	@Override
	public LHSExpr getReturns() {
		return (LHSExpr)slotGet(Slot_returns);
	}

	@Override
	public void setReturns(LHSExpr value) {
		slotSet(Slot_returns, value);
	}

}
