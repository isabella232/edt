/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

/**
 * ArrayLiteral AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ArrayLiteral extends LiteralExpression {

	private List exprs;	// List of Expressions

	public ArrayLiteral(List exprs, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.exprs = setParent(exprs);
	}
	
	public List getExpressions() {
		return exprs;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			acceptChildren(visitor, exprs);
		}
		visitor.endVisit(this);
	}
	
	public int getLiteralKind() {
		return ARRAY_LITERAL;
	}
	
	public String getCanonicalString() {
		StringBuffer result = new StringBuffer();
		result.append("[");
		for(Iterator iter = getExpressions().iterator(); iter.hasNext();) {
			result.append(((Expression) iter.next()).getCanonicalString());
			if(iter.hasNext()) {
				result.append(",");
			}
		}
		result.append("]");
		return result.toString();
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new ArrayLiteral(cloneList(exprs), getOffset(), getOffset() + getLength());
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		Iterator i = exprs.iterator();
		boolean first = true;
		while (i.hasNext()) {
			Expression exp = (Expression)i.next();
			if (!first) {
				buffer.append(",");
				
			}
			first = false;
			buffer.append(exp.toString());
		}
		buffer.append("]");
		return buffer.toString();
	}
}
