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

import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Type;

/**
 * ParenthesizedExpression AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ParenthesizedExpression extends Expression {

	private Expression expr;

	public ParenthesizedExpression(Expression expr, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
	}
	
	public Expression getExpression() {
		return expr;
	}
	
	public String getCanonicalString() {
  		return expr.getCanonicalString();
    }
	   
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new ParenthesizedExpression((Expression)expr.clone(), getOffset(), getOffset() + getLength());
	}
	
	public Member resolveMember() {
		return expr.resolveMember();
	}
	
	public Type resolveType() {
		return expr.resolveType();
	}
	
	@Override
	public String toString() {
		return getCanonicalString();
	}
}
