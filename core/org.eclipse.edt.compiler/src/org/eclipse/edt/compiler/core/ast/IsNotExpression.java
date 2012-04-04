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
 * IsNotExpression AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class IsNotExpression extends Expression {
	
	/**
 	 * Is/Not operators (typesafe enumeration).
 	 */
	public static class Operator {	
		
		private String token;
		private Operator(String token) { this.token = token; }
		public String toString() { return token; }
		
		public static final Operator IS = new Operator("is");//$NON-NLS-1$
		public static final Operator NOT = new Operator("not");//$NON-NLS-1$
		
		private static final Map CODES;
		static {
			CODES = new HashMap(2);
			Operator[] ops = {
					IS,
					NOT
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

	public IsNotExpression(Operator operator, Expression expr, Expression expr2, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.operator = operator;
		
		this.expr = expr;
		expr.setParent(this);
		this.expr2 = expr2;
		expr2.setParent(this);
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
    
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			expr2.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new IsNotExpression(operator, (Expression)expr.clone(), (Expression)expr2.clone(), getOffset(), getOffset() + getLength());
	}
}
