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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eglx.lang.AnyException;

public class ArrayNode extends ValueNode {
	List values = new ArrayList();

	//Return a list of Value objects
	public List getValues() {
		return values;
	}
	
	public void addValue(ValueNode value) {
		values.add(value);
	}
	
	public void accept(JsonVisitor visitor) throws AnyException{
		boolean visitChildren = visitor.visit(this);
		if (visitChildren) {
			visitChildren(visitor);
		}
		visitor.endVisit(this);
	}
	
	public void visitChildren(JsonVisitor visitor)  throws AnyException{
		Iterator i = values.iterator();
		while (i.hasNext()) {
			((ValueNode)i.next()).accept(visitor);
		}
	}
	
	public java.lang.String toJson() {
		StringBuilder buff = new StringBuilder();
		buff.append("[");
		boolean first = true;
		Iterator i = values.iterator();
		while (i.hasNext()) {
			if (first) {
				first = false;
			}
			else {
				buff.append(", ");
			}
			buff.append(((ValueNode)i.next()).toJson());
		}
		buff.append("]");
		return buff.toString();
	}

	public java.lang.String toJava() {
		StringBuilder buff = new StringBuilder();
		buff.append("[");
		boolean first = true;
		Iterator i = values.iterator();
		while (i.hasNext()) {
			if (first) {
				first = false;
			}
			else {
				buff.append(", ");
			}
			buff.append(((ValueNode)i.next()).toJava());
		}
		buff.append("]");
		return buff.toString();
	}
	
	
}
