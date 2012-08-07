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
package org.eclipse.edt.mof.egl;


public interface UnaryExpression extends Expression {
	String Op_NOT = "!";
	String Op_BITWISENOT = "~";
	String Op_NEGATE = "-";

	Expression getExpression();
	
	void setExpression(Expression value);
	
	String getOperator();
	
	void setOperator(String value);
	
	Operation getOperation();
	
	void setOperation(Operation operation);
	
}
