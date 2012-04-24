/*******************************************************************************
 * Copyright © 2008, 2012 IBM Corporation and others.
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

public class NullNode extends ValueNode {
	public static final NullNode NULL = new NullNode();


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
		return "null";
	}
	
	public String toJava() {
		return toJson();
	}

}
