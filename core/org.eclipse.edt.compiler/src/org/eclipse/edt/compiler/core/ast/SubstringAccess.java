/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

/**
 * SubstringAccess AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class SubstringAccess extends Expression {

	private Expression primary;
	private Expression expr;
	private Expression expr2;

	public SubstringAccess(Expression primary, Expression expr, Expression expr2, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.primary = primary;
		primary.setParent(this);
		this.expr = expr;
		expr.setParent(this);
		this.expr2 = expr2;
		expr2.setParent(this);
	}
	
	public Expression getPrimary() {
		return primary;
	}
	
	public Expression getExpr() {
		return expr;
	}
	
	public Expression getExpr2() {
		return expr2;
	}
	
	@Override
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			primary.accept(visitor);
			expr.accept(visitor);
			expr2.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	@Override
	public String getCanonicalString() {
		StringBuffer sb = new StringBuffer();
		sb.append(primary.getCanonicalString());
		sb.append("[");
		sb.append(expr.getCanonicalString());
		sb.append(":");
		sb.append(expr2.getCanonicalString());
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new SubstringAccess((Expression)primary.clone(), (Expression)expr.clone(), (Expression)expr2.clone(), getOffset(), getOffset() + getLength());
	}
	
	@Override
	public Member resolveMember() {
		return getPrimary().resolveMember();
	}
	
	@Override
	public Type resolveType() {
		return getPrimary().resolveType();
	}
	
	@Override
	public String toString() {
		return getCanonicalString();
	}
}
