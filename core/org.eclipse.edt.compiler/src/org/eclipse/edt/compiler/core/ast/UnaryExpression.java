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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dave Murray
 */
public class UnaryExpression extends Expression {

	/**
 	 * Prefix operators (typesafe enumeration).
 	 */
	public static class Operator {	
		
		private String token;
		private Operator(String token) { this.token = token; }
		public String toString() { return token; }
		
		public static final Operator PLUS = new Operator("+");//$NON-NLS-1$
		public static final Operator MINUS = new Operator("-");//$NON-NLS-1$
		public static final Operator BANG = new Operator("!");//$NON-NLS-1$
		public static final Operator NEGATE = new Operator("~");//$NON-NLS-1$
		
		private static final Map CODES;
		static {
			CODES = new HashMap(4);
			Operator[] ops = {
					PLUS,
					MINUS,
					BANG,
					NEGATE
				};
			for (int i = 0; i < ops.length; i++) {
				CODES.put(ops[i].toString(), ops[i]);
			}
		}

		public static Operator toOperator(String token) {
			return (Operator) CODES.get(token);
		}		
	}
	
	private Operator operator;
	private Expression expr;
		
	public UnaryExpression( Operator operator, Expression expr, int startOffset, int endOffset ) {
		super( startOffset, endOffset );
		
		this.operator = operator;
		this.expr = expr;
		expr.setParent(this);
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public Expression getExpression() {
		return expr;
	}
	
	public String getCanonicalString() {
		StringBuffer result = new StringBuffer();
		if(operator != Operator.PLUS) {
			result.append(operator.token);
		}
		result.append(expr.getCanonicalString());
		return result.toString();
    }
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new UnaryExpression(operator, (Expression)expr.clone(), getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		return operator.toString() + expr.toString();
	}
}
