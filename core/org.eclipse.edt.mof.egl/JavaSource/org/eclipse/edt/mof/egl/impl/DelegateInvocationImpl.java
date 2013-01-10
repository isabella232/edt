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

import org.eclipse.edt.mof.egl.Delegate;
import org.eclipse.edt.mof.egl.DelegateInvocation;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.InvocableElement;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;

public class DelegateInvocationImpl extends InvocationExpressionImpl implements DelegateInvocation {
	private static int Slot_expression=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + InvocationExpressionImpl.totalSlots();
	}
	
	static {
		int offset = InvocationExpressionImpl.totalSlots();
		Slot_expression += offset;
	}
	@Override
	public Expression getExpression() {
		return (Expression)slotGet(Slot_expression);
	}
	
	@Override
	public void setExpression(Expression value) {
		slotSet(Slot_expression, value);
	}

	@Override
	public Type getType() {
		return getTarget().getReturnType();
	}

	@Override
	public InvocableElement getTarget() {
		return (InvocableElement)getExpression().getType();
	}

	@Override
	public QualifiedFunctionInvocation addQualifier(Expression expr) {
		// TODO not sure what to put here
		return null;
	}
	
	@Override
	public boolean isNullable() {
		if (getTarget() instanceof Delegate) {
			return ((Delegate)getTarget()).isNullable();
		}
		return super.isNullable();
	}
	
}
