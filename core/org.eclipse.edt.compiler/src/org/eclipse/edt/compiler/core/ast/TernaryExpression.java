/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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



public class TernaryExpression extends Expression {
	
	private Expression firstExpr;
	private Expression secondExpr;
	private Expression thirdExpr;
	
	public TernaryExpression(Expression firstExpr, Expression secondExpr, Expression thirdExpr, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.firstExpr = firstExpr;
		this.secondExpr = secondExpr;
		this.thirdExpr = thirdExpr;
	}
	
	public Expression getFirstExpr() {
		return firstExpr;
	}

	public void setFirstExpr(Expression firstExpr) {
		this.firstExpr = firstExpr;
	}

	public Expression getSecondExpr() {
		return secondExpr;
	}

	public void setSecondExpr(Expression secondExpr) {
		this.secondExpr = secondExpr;
	}

	public Expression getThirdExpr() {
		return thirdExpr;
	}

	public void setThirdExpr(Expression thirdExpr) {
		this.thirdExpr = thirdExpr;
	}
	
	@Override
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			firstExpr.accept(visitor);
			secondExpr.accept(visitor);
			thirdExpr.accept(visitor);
		}
		visitor.endVisit(this);
	}

	@Override
	public String getCanonicalString() {
		StringBuilder buf = new StringBuilder(100);
		buf.append(firstExpr.getCanonicalString());
		buf.append(" ? ");
		buf.append(secondExpr.getCanonicalString());
		buf.append(" : ");
		buf.append(thirdExpr.getCanonicalString());
		return buf.toString();
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new TernaryExpression(firstExpr, secondExpr, thirdExpr, getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		return firstExpr.toString() + " ? " + secondExpr.toString() + " : " + thirdExpr.toString();
	}
}
