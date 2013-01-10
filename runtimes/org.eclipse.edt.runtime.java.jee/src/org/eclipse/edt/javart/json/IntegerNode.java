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

import java.math.BigInteger;

import eglx.lang.AnyException;

public class IntegerNode extends NumberNode {
	
	public IntegerNode(java.lang.String stringValue) {
		super(stringValue);
	}

	public IntegerNode(int intValue) {
		super(Integer.toString(intValue));
	}
	
	public IntegerNode(short shortValue) {
		super(Short.toString(shortValue));
	}
	
	public IntegerNode(long longValue) {
		super(Long.toString(longValue));
	}

	public IntegerNode(Integer intValue) {
		super(intValue.toString());
	}
	
	public IntegerNode(Short shortValue) {
		super(shortValue.toString());
	}
	
	public IntegerNode(Long longValue) {
		super(longValue.toString());
	}
	
	public IntegerNode(java.math.BigInteger integerValue) {
		super(integerValue.toString());
	}

	BigInteger bigIntegerValue;

	public BigInteger getBigIntegerValue() {
		if (bigIntegerValue == null) {
			bigIntegerValue = new BigInteger(getStringValue());
		}
		return bigIntegerValue;
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
