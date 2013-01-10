/*******************************************************************************
 * Copyright © 2008, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.json;

import java.math.BigDecimal;

import eglx.lang.AnyException;

public class DecimalNode extends NumberNode {
	
	public DecimalNode(java.lang.String stringValue) {
		super(stringValue);
	}
	
	public DecimalNode(java.math.BigDecimal decimalValue) {
		super(decimalValue.toString());
	}

	BigDecimal decimalValue;

	public BigDecimal getDecimalValue() {
		if (decimalValue == null) {
			decimalValue = new java.math.BigDecimal(getStringValue());
		}
		return decimalValue;
	}
	
	public void accept(JsonVisitor visitor)  throws AnyException{
		boolean visitChildren = visitor.visit(this);
		if (visitChildren) {
			visitChildren(visitor);
		}
		visitor.endVisit(this);
	}
	
	public void visitChildren(JsonVisitor visitor) {
	}
}
