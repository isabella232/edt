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

import java.util.HashMap;
import java.util.Map;

/**
 * LikeMatchesExpression AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class LikeMatchesExpression extends Expression {
	
	/**
 	 * Like/Matches operators (typesafe enumeration).
 	 */
	public static class Operator {	
		
		private String token;
		private Operator(String token) { this.token = token; }
		public String toString() { return token; }
		
		public static final Operator LIKE = new Operator("like");//$NON-NLS-1$
		public static final Operator MATCHES = new Operator("matches");//$NON-NLS-1$
		
		private static final Map CODES;
		static {
			CODES = new HashMap(2);
			Operator[] ops = {
					LIKE,
					MATCHES
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
	private Expression expr2;
	private String escapeOpt;

	public LikeMatchesExpression(Operator operator, Expression expr, Expression expr2, String escapeOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.operator = operator;		
		this.expr = expr;
		expr.setParent(this);
		this.expr2 = expr2;
		expr2.setParent(this);
		this.escapeOpt = escapeOpt;
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public Expression getFirstExpression() {
		return expr;
	}
	
	public Expression getSecondExpression() {
		return expr2;
	}
	
    public String getCanonicalString() {
  		return "";
    }
    
	public boolean hasEscapeString() {
		return escapeOpt != null;
	}
	
	public String getEscapeString() {
		return escapeOpt;
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
		String newEscapeOpt = escapeOpt != null ? new String(escapeOpt) : null;
		
		return new LikeMatchesExpression(operator, (Expression)expr.clone(), (Expression)expr2.clone(), newEscapeOpt, getOffset(), getOffset() + getLength());
	}
}
