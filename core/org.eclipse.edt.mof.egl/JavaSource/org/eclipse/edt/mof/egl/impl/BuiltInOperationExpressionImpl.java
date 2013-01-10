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

import org.eclipse.edt.mof.egl.BuiltInOperation;
import org.eclipse.edt.mof.egl.BuiltInOperationExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Type;

public class BuiltInOperationExpressionImpl extends InvocationExpressionImpl implements BuiltInOperationExpression {
	private static int Slot_Operation=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + InvocationExpressionImpl.totalSlots();
	}
	
	static {
		int offset = InvocationExpressionImpl.totalSlots();
		Slot_Operation += offset;
	}
	@Override
	public BuiltInOperation getOperation() {
		return (BuiltInOperation)slotGet(Slot_Operation);
	}
	
	@Override
	public void setOperation(BuiltInOperation value) {
		slotSet(Slot_Operation, value);
	}

	@Override
	public Type getType() {
		return getOperation().getType();
	}
	
	@Override
	public FunctionMember getTarget() {
		return getOperation();
	}

	@Override
	public QualifiedFunctionInvocation addQualifier(Expression expr) {
		throw new UnsupportedOperationException();
	}
}
