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
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.GenericType;
import org.eclipse.edt.mof.egl.InvocableElement;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.Type;


public abstract class InvocationExpressionImpl extends ExpressionImpl implements InvocationExpression {
	private static int Slot_id=0;
	private static int Slot_arguments=1;
	private static int totalSlots = 2;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_id += offset;
		Slot_arguments += offset;
	}
	@Override
	public String getId() {
		return (String)slotGet(Slot_id);
	}
	
	@Override
	public void setId(String value) {
		slotSet(Slot_id, value);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Expression> getArguments() {
		return (List<Expression>)slotGet(Slot_arguments);
	}
	
	@Override
	public Type getParameterTypeForArg(int index) {
		return getTarget().getParameters().get(index).getType();
	}
	
	public abstract InvocableElement getTarget();
}
