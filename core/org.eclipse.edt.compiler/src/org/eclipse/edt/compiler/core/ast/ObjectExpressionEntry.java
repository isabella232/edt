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


public class ObjectExpressionEntry extends Node {

	private String id;
	private Expression expr;

	public ObjectExpressionEntry(String id, Expression expr, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.id = id;
		this.expr = expr;
		expr.setParent(this);
	}
	
	public String getId() {
		return id;
	}
	
	public Expression getExpression() {
		return expr;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
		}
		visitor.endVisit(this);
	}
		
	public String getCanonicalString() {
		StringBuffer result = new StringBuffer();
		result.append(id);
		result.append(" : ");
		result.append(expr.getCanonicalString());
		return result.toString();
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new ObjectExpressionEntry(new String(id), (Expression)expr.clone(), getOffset(), getOffset() + getLength());
	}
	
	public String toString() {
		return getCanonicalString();
	}
}
