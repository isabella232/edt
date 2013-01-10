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

import org.eclipse.edt.mof.egl.DynamicAccess;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.LHSExpr;
import org.eclipse.edt.mof.egl.NoSuchFunctionError;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.ThisExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;


public class DynamicAccessImpl extends ExpressionImpl implements DynamicAccess {
	private static int Slot_access=0;
	private static int Slot_expression=1;
	private static int Slot_operation=2;
	private static int totalSlots = 3;
	
	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}
	
	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_access += offset;
		Slot_expression += offset;
		Slot_operation += offset;
	}
	@Override
	public Expression getAccess() {
		return (Expression)slotGet(Slot_access);
	}
	
	@Override
	public void setAccess(Expression value) {
		slotSet(Slot_access, value);
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
		return TypeUtils.Type_ANY;
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
		Operation op = IRUtils.getMyOperation(getExpression().getType().getClassifier(), "['");
		if (op == null) throw new NoSuchFunctionError();
		return op;
	}

	@Override
	public LHSExpr addQualifier(Expression expr) {
		
		DynamicAccess newDA = IrFactory.INSTANCE.createDynamicAccess();
		newDA.setAccess(getAccess());
		newDA.setExpression(getExpression());
		newDA.getAnnotations().addAll(getAnnotations());
		
		if (getExpression() instanceof LHSExpr) {
			newDA.setExpression(((LHSExpr)getExpression()).addQualifier(expr));
		}		
		else if (getExpression() instanceof InvocationExpression) {
			newDA.setExpression(((InvocationExpression)getExpression()).addQualifier(expr));
		}
		else if (getExpression() instanceof ThisExpression) {
			newDA.setExpression(expr);
		}
		return newDA;
	}
	
	@Override
	public boolean isNullable() {
		// there is no way of knowing whether the dynamically accessed member is nullable or not, so set to true
		return true;
	}
	
}
