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

public class NameValuePairNode extends Node {

		StringNode name;
		ValueNode value;
		
		
		public NameValuePairNode(StringNode name, ValueNode value) {
			super();
			this.name = name;
			this.value = value;
		}
		
		
		public StringNode getName() {
			return name;
		}
		public void setName(StringNode name) {
			this.name = name;
		}
		public ValueNode getValue() {
			return value;
		}
		public void setValue(ValueNode value) {
			this.value = value;
		}
		
		public void accept(JsonVisitor visitor)  throws AnyException{
			boolean visitChildren = visitor.visit(this);
			if (visitChildren) {
				visitChildren(visitor);
			}
			visitor.endVisit(this);
		}
		
		public void visitChildren(JsonVisitor visitor) throws AnyException {
			name.accept(visitor);
			value.accept(visitor);
		}


		public java.lang.String toJson() {
			return name.toJson() + " : " + value.toJson();
		}
		
		public String toJava() {
			return name.toJava() + " : " + value.toJava();
		}
		
}
