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

import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.MultiOperandExpression;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.NamedElement;
import org.eclipse.edt.mof.egl.NoSuchFunctionError;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;

public abstract class MultiOperandExpressionImpl extends ExpressionImpl implements MultiOperandExpression{
	private static int Slot_operands=0;
	private static int Slot_operator=1;
	private static int Slot_operation=2;
	private static int totalSlots = 3;

	public static int totalSlots() {
		return totalSlots + ExpressionImpl.totalSlots();
	}

	static {
		int offset = ExpressionImpl.totalSlots();
		Slot_operands += offset;
		Slot_operator += offset;
		Slot_operation += offset;
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
	
	@Override
	public Type getType() {
		return getOperation().getType();
	}

	@Override
	public String getOperator() {
		return (String)slotGet(Slot_operator);
	}
	
	@Override
	public void setOperator(String opSymbol) {
		slotSet(Slot_operator, opSymbol);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Expression> getOperands() {
		return (List<Expression>)slotGet(Slot_operands);
	}

	protected abstract Operation resolveOperation();
	
	protected NamedElement getOperandType(Expression expr) {
		if (expr instanceof Name && ((Name)expr).getNamedElement() instanceof Function) {
			return (Function) ((Name)expr).getNamedElement();
		}
		else {			
			return (Classifier)expr.getType().getClassifier();
		}			
	}
	
}
