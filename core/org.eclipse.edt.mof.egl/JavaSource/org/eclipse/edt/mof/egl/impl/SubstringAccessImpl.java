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

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.NoSuchFunctionError;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.SubstringAccess;
import org.eclipse.edt.mof.egl.ThisExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class SubstringAccessImpl extends ExpressionImpl implements SubstringAccess {
	private static int Slot_stringExpression=0;
	private static int Slot_start=1;
	private static int Slot_end=2;
	private static int Slot_operation=3;
	private static int totalSlots = 4;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_stringExpression += offset;
		Slot_start += offset;
		Slot_end += offset;
		Slot_operation += offset;
	}
	@Override
	public Expression getStringExpression() {
		return (Expression)slotGet(Slot_stringExpression);
	}
	
	@Override
	public void setStringExpression(Expression value) {
		slotSet(Slot_stringExpression, value);
	}
	
	@Override
	public Expression getStart() {
		return (Expression)slotGet(Slot_start);
	}
	
	@Override
	public void setStart(Expression value) {
		slotSet(Slot_start, value);
	}
	
	@Override
	public Expression getEnd() {
		return (Expression)slotGet(Slot_end);
	}
	
	@Override
	public void setEnd(Expression value) {
		slotSet(Slot_end, value);
	}

	@Override
	public Type getType() {
		return getStringExpression().getType();
	}
	
	@Override
	public Operation getOperation() {
		if (slotGet(Slot_operation) == null) {
			try {
				setOperation(resolveOperation());
			} catch (NoSuchFunctionError e) {
				throw new RuntimeException(e);
			}
		}
		return (Operation)slotGet(Slot_operation);
	}
	
	@Override
	public void setOperation(Operation value) {
		slotSet(Slot_operation, value);
	}
	
	private Operation resolveOperation() {
		Operation op = IRUtils.getMyOperation(getStringExpression().getType().getClassifier(), "[:");
		if (op == null) throw new NoSuchFunctionError();
		return op;
	}
	
	
	@Override
	public LHSExpr addQualifier(Expression expr) {		
		
		SubstringAccess newSA = IrFactory.INSTANCE.createSubstringAccess();
		newSA.setEnd(getEnd());
		newSA.setStart(getStart());
		newSA.setStringExpression(getStringExpression());
		newSA.getAnnotations().addAll(getAnnotations());
		
		if (getStringExpression() instanceof LHSExpr) {
			newSA.setStringExpression(((LHSExpr)getStringExpression()).addQualifier(expr));
		}		
		else if (getStringExpression() instanceof InvocationExpression) {
			newSA.setStringExpression(((InvocationExpression)getStringExpression()).addQualifier(expr));
		}
		else if (getStringExpression() instanceof ThisExpression) {
			newSA.setStringExpression(expr);
		}
		return newSA;
	}

}
