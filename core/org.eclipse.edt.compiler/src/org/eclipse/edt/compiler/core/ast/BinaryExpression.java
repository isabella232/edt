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
public class BinaryExpression extends Expression {

	/**
 	 * Infix operators (typesafe enumeration).
 	 */
	public static class Operator {	
		
		private String token;
		private Operator(String token) { this.token = token; }
		public String toString() { return token; }
		
		public static final Operator TIMES = new Operator("*");//$NON-NLS-1$
		public static final Operator TIMESTIMES = new Operator("**");//$NON-NLS-1$
		public static final Operator DIVIDE = new Operator("/");//$NON-NLS-1$
		public static final Operator MODULO = new Operator("%");//$NON-NLS-1$
		public static final Operator PLUS = new Operator("+");//$NON-NLS-1$
		public static final Operator MINUS = new Operator("-");//$NON-NLS-1$
		public static final Operator LESS = new Operator("<");//$NON-NLS-1$
		public static final Operator GREATER = new Operator(">");//$NON-NLS-1$
		public static final Operator LESS_EQUALS = new Operator("<=");//$NON-NLS-1$
		public static final Operator GREATER_EQUALS = new Operator(">=");//$NON-NLS-1$
		public static final Operator EQUALS = new Operator("==");//$NON-NLS-1$
		public static final Operator NOT_EQUALS = new Operator("!=");//$NON-NLS-1$
		public static final Operator OR = new Operator("||");//$NON-NLS-1$
		public static final Operator AND = new Operator("&&");//$NON-NLS-1$
		public static final Operator CONCAT = new Operator("::");//$NON-NLS-1$
		public static final Operator NULLCONCAT = new Operator("?:");//$NON-NLS-1$
		public static final Operator BITAND = new Operator("&");//$NON-NLS-1$
		public static final Operator BITOR = new Operator("|");//$NON-NLS-1$
		public static final Operator XOR = new Operator("xor");//$NON-NLS-1$
		public static final Operator LEFT_SHIFT = new Operator("<<");//$NON-NLS-1$
		public static final Operator RIGHT_SHIFT_ARITHMETIC = new Operator(">>");//$NON-NLS-1$
		public static final Operator RIGHT_SHIFT_LOGICAL = new Operator(">>>");//$NON-NLS-1$
		
		private static final Map CODES;
		static {
			CODES = new HashMap(16);
			Operator[] ops = {
					TIMES,
					TIMESTIMES,
					DIVIDE,
					MODULO,
					PLUS,
					MINUS,
					LESS,
					GREATER,
					LESS_EQUALS,
					GREATER_EQUALS,
					EQUALS,
					NOT_EQUALS,
					OR,
					AND,
					CONCAT,
					NULLCONCAT,
					BITAND,
					BITOR,
					XOR,
					LEFT_SHIFT,
					RIGHT_SHIFT_ARITHMETIC,
					RIGHT_SHIFT_LOGICAL
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
	private Expression expr1;
	private Expression expr2;
	
	public BinaryExpression( Operator operator, Expression expr1, Expression expr2, int startOffset, int endOffset ) {
		super( startOffset, endOffset );
		
		this.operator = operator;
		this.expr1 = expr1;
		expr1.setParent(this);
		this.expr2 = expr2;
		expr2.setParent(this);
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public Expression getFirstExpression() {
		return expr1;
	}
	
	public Expression getSecondExpression() {
		return expr2;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr1.accept(visitor);
			expr2.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new BinaryExpression(operator, (Expression)expr1.clone(), (Expression)expr2.clone(), getOffset(), getOffset() + getLength());
	}
	
	public String getCanonicalString() {
		return expr1.getCanonicalString() + operator.toString() + expr2.getCanonicalString();
	}
	
	@Override
	public String toString() {
		return getCanonicalString();
	}
}
