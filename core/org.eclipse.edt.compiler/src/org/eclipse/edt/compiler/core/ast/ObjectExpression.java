/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.compiler.core.ast;

import java.util.Iterator;
import java.util.List;

public class ObjectExpression extends Expression {

	private List entries;	// List of ObjExprEntries

	public ObjectExpression(List entries, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.entries = setParent(entries);
	}
	
	public List getEntries() {
		return entries;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, entries);
		}
		visitor.endVisit(this);
	}
		
	public String getCanonicalString() {
		StringBuffer result = new StringBuffer();
		result.append("{");
		for(Iterator iter = getEntries().iterator(); iter.hasNext();) {
			result.append(((ObjectExpressionEntry) iter.next()).getCanonicalString());
			if(iter.hasNext()) {
				result.append(",");
			}
		}
		result.append("}");
		return result.toString();
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new ObjectExpression(cloneList(entries), getOffset(), getOffset() + getLength());
	}
	
	public String toString() {
		return getCanonicalString();
	}
}
