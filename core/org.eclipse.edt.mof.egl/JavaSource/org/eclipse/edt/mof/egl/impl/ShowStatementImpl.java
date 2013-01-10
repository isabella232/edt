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
import org.eclipse.edt.mof.egl.ShowStatement;

public class ShowStatementImpl extends IOStatementImpl implements ShowStatement {
	private static int Slot_passing=0;
	private static int Slot_returnTo=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + IOStatementImpl.totalSlots();
	}
	
	static {
		int offset = IOStatementImpl.totalSlots();
		Slot_passing += offset;
		Slot_returnTo += offset;
	}
	@Override
	public Expression getPassing() {
		return (Expression)slotGet(Slot_passing);
	}
	
	@Override
	public void setPassing(Expression value) {
		slotSet(Slot_passing, value);
	}
	
	@Override
	public Expression getReturnTo() {
		return (Expression)slotGet(Slot_returnTo);
	}
	
	@Override
	public void setReturnTo(Expression value) {
		slotSet(Slot_returnTo, value);
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
