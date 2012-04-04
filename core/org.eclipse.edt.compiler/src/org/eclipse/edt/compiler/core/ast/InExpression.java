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

/**
 * INExpression AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class InExpression extends Expression {

	private Expression expr;
	private Expression expr2;
	private Expression fromExpr;

	public InExpression(Expression expr, Expression expr2, Expression fromExprOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
		this.expr2 = expr2;
		expr2.setParent(this);
		if(fromExprOpt != null) {
			fromExpr = fromExprOpt;
			fromExpr.setParent(this);
		}
	}
	
	public Expression getFirstExpression() {
		return expr;
	}
	
	public Expression getSecondExpression() {
		return expr2;
	}
	
	public boolean hasFromExpression() {
		return fromExpr != null;
	}
	
	public Expression getFromExpression() {
		return fromExpr;
	}
	
    public String getCanonicalString() {
  		return "";
    }
    
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			expr2.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Expression fromExpression = fromExpr == null ? null : (Expression) fromExpr.clone();
		return new InExpression((Expression)expr.clone(), (Expression)expr2.clone(), fromExpression, getOffset(), getOffset() + getLength());
	}
}
