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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eglx.lang.AnyException;

public class ObjectNode extends ValueNode {
	List pairs = new ArrayList();

	
	//Returns a List of NameValuePair objects
	public List getPairs() {
		return pairs;
	}

	//Add a NameValuePair object to the collection
	public void addPair(NameValuePairNode pair) {
		pairs.add(pair);
	}
	
	public void accept(JsonVisitor visitor)  throws AnyException{
		boolean visitChildren = visitor.visit(this);
		if (visitChildren) {
			visitChildren(visitor);
		}
		visitor.endVisit(this);
	}
	
	public void visitChildren(JsonVisitor visitor)  throws AnyException{
		Iterator i = getPairs().iterator();
		while (i.hasNext()) {
			((NameValuePairNode)i.next()).accept(visitor);
		}
	}

	public void remove(String key){
		NameValuePairNode pair;
		for( int idx = 0; idx < pairs.size(); idx++ )
		{
			pair = (NameValuePairNode)pairs.get(idx);
			if( pair.getName().getJavaValue().equalsIgnoreCase(key) )
			{
				pairs.remove(idx);
			}
		}
	}
	public java.lang.String toJson() {
		StringBuilder buff = new StringBuilder();
		buff.append("{");
		Iterator i = getPairs().iterator();
		boolean first = true;
		while (i.hasNext()) {
			if (first) {
				first = false;
			}
			else {
				buff.append(", ");
			}
			buff.append(((NameValuePairNode)i.next()).toJson());
		}	
		
		buff.append("}");
		return buff.toString();
	}
	
	public java.lang.String toJava() {
		StringBuilder buff = new StringBuilder();
		buff.append("{");
		Iterator i = getPairs().iterator();
		boolean first = true;
		while (i.hasNext()) {
			if (first) {
				first = false;
			}
			else {
				buff.append(", ");
			}
			buff.append(((NameValuePairNode)i.next()).toJava());
		}	
		
		buff.append("}");
		return buff.toString();
	}
}
