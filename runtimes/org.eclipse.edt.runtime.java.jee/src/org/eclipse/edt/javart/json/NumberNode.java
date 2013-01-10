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

public abstract class NumberNode extends ValueNode {
	java.lang.String stringValue;

	public NumberNode(java.lang.String stringValue) {
		super();
		this.stringValue = stringValue;
	}

	public java.lang.String getStringValue() {
		return stringValue;
	}

	public void setStringValue(java.lang.String stringValue) {
		this.stringValue = stringValue;
	}
	
	public java.lang.String toJson() {
		return stringValue;
	}
	
	public String toJava() {
		return toJson();
	}
	
}
