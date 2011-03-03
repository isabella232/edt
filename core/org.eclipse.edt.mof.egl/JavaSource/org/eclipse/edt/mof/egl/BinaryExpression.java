/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.mof.egl;


public interface BinaryExpression extends Expression {
	String Op_PLUS = "+";
	String Op_MINUS = "-";
	String Op_MULTIPLY = "*";
	String Op_POWER = "**";
	String Op_DIVIDE = "/";
	String Op_MODULO = "%";
	String Op_AND = "&&";
	String Op_OR = "||";
	String Op_XOR = "xor";
	String Op_BITAND = "&";
	String Op_BITOR = "|";
	String Op_EQ = "==";
	String Op_NE = "!=";
	String Op_GT = ">";
	String Op_LT = "<";
	String Op_LE = "<=";
	String Op_GE = ">=";
	String Op_CONCAT = "::";
	String Op_NULLCONCAT = "?:";
	String Op_IN = "in";
	String Op_MATCHES = "matches";
	String Op_LIKE = "like";
	
	Expression getLHS();
	
	void setLHS(Expression value);
	
	Expression getRHS();
	
	void setRHS(Expression value);
	
	Operation getOperation();
	
	void setOperation(Operation operation);
	
	String getOperator();
	void setOperator(String opSymbol);
}
