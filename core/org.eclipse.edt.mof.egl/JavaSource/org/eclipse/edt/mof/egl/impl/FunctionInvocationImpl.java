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

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionInvocation;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Type;

public class FunctionInvocationImpl extends InvocationExpressionImpl implements FunctionInvocation {
	private static int Slot_target=0;
	private static int totalSlots = 1;
	
	public static int totalSlots() {
		return totalSlots + InvocationExpressionImpl.totalSlots();
	}
	
	static {
		int offset = InvocationExpressionImpl.totalSlots();
		Slot_target += offset;
	}
	@Override
	public FunctionMember getTarget() {
		return (FunctionMember)slotGet(Slot_target);
	}
	
	@Override
	public void setTarget(Member value) {
		slotSet(Slot_target, value);
	}
	
	@Override
	public FunctionMember getFunction() {
		return getTarget();
	}

	@Override
	public Type getType() {
		return getTarget().getType();
	}

	@Override
	public boolean isNullable() {
		return getTarget().isNullable();
	}
	
	@Override
	public QualifiedFunctionInvocation addQualifier(Expression expr) {
		QualifiedFunctionInvocation invoke = IrFactory.INSTANCE.createQualifiedFunctionInvocation();
		invoke.setId(getId());
		invoke.getArguments().addAll(getArguments());
		invoke.setQualifier(expr);
		return invoke;
	}

	
}
