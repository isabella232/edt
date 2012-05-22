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
import org.eclipse.edt.mof.egl.NoSuchFunctionError;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.TernaryExpression;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class TernaryExpressionImpl extends MultiOperandExpressionImpl implements TernaryExpression{

	private boolean initialized = false;

	private void ensureInitialized() {
		if (initialized) {
			return;
		}
		initialized = true;
		if (getOperands().isEmpty()) {
			getOperands().add(null);
			getOperands().add(null);
			getOperands().add(null);
		}
	}

	@Override
	protected Operation resolveOperation() {
		Operation op = IRUtils.getBinaryOperation(getOperandType(getFirst()), getOperandType(getSecond()), getOperator());
		if (op == null) throw new NoSuchFunctionError();
		return op;
	}


	@Override
	public Expression getFirst() {
		ensureInitialized();
		return getOperands().get(0);
	}


	@Override
	public Expression getSecond() {
		ensureInitialized();
		return getOperands().get(1);
	}


	@Override
	public Expression getThird() {
		ensureInitialized();
		return getOperands().get(2);
	}


	@Override
	public void setFirst(Expression expr) {
		ensureInitialized();
		getOperands().set(0, expr);		
	}


	@Override
	public void setSecond(Expression expr) {
		ensureInitialized();
		getOperands().set(1, expr);		
	}


	@Override
	public void setThird(Expression expr) {
		ensureInitialized();
		getOperands().set(2, expr);		
	}

}
