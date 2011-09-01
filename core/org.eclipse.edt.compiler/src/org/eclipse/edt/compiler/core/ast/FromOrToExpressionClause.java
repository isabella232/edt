/*
 * Licensed Materials - Property of IBM
 *
 * Copyright IBM Corporation 2005, 2010. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA DP Schedule Contract with IBM Corp.
 */
package org.eclipse.edt.compiler.core.ast;

public class FromOrToExpressionClause extends Node {
	
	private Expression expr;

	public FromOrToExpressionClause(Expression expr, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
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
	
	protected Object clone() throws CloneNotSupportedException {
		return new FromOrToExpressionClause((Expression)expr.clone(), getOffset(), getOffset() + getLength());
	}
}
