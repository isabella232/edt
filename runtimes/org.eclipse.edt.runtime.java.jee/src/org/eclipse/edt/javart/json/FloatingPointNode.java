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

import eglx.lang.AnyException;

public class FloatingPointNode extends NumberNode {

	public FloatingPointNode(java.lang.String stringValue) {
		super(stringValue);
	}
	
	public FloatingPointNode(float floatValue) {
		super(Float.toString(floatValue));
	}
	
	public FloatingPointNode(double doubleValue) {
		super(Double.toString(doubleValue));
	}
	
	public FloatingPointNode(Float floatValue) {
		super(floatValue.toString());
	}
	
	public FloatingPointNode(Double doubleValue) {
		super(doubleValue.toString());
	}

	Double doubleValue;

	public Double getDoubleValue() {
		if (doubleValue == null) {
			doubleValue = new Double(getStringValue());
		}
		return doubleValue;
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
