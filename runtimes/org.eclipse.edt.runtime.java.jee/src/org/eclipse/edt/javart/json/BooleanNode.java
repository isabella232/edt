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

public class BooleanNode extends ValueNode {
	
	public static final BooleanNode TRUE = new BooleanNode(true);
	public static final BooleanNode FALSE = new BooleanNode(false);
	
	private boolean value;

	public BooleanNode(boolean value) {
		super();
		this.value = value;
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
	
	public java.lang.String toJson() {
		if (value) {
			return "true";
		}
		else {
			return "false";
		}
	}
	
	public String toJava() {
		return toJson();
	}

	public boolean getValue() {
		return value;
	}
}
