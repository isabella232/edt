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

import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.NoSuchFunctionError;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class BinaryExpressionImpl extends MultiOperandExpressionImpl implements BinaryExpression {

	private boolean initialized = false;
	
	private void ensureInitialized() {
		if (initialized) {
			return;
		}
		initialized = true;
		if (getOperands().isEmpty()) {
			getOperands().add(null);
			getOperands().add(null);
		}
	}
	
	@Override
	public Expression getLHS() {
		ensureInitialized();
		return getOperands().get(0);
	}
	
	@Override
	public void setLHS(Expression value) {
		ensureInitialized();
		getOperands().set(0, value);
	}
	
	@Override
	public Expression getRHS() {
		ensureInitialized();
		return getOperands().get(1);
	}
	
	@Override
	public void setRHS(Expression value) {
		ensureInitialized();
		getOperands().set(1, value);
	}
	
	protected Operation resolveOperation() {
		Operation op = IRUtils.getBinaryOperation(getOperandType(getLHS()), getOperandType(getRHS()), getOperator());
		if (op == null) throw new NoSuchFunctionError();
		return op;
	}
}
