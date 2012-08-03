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

import org.eclipse.edt.mof.egl.Annotation;


/**
 * NamedSetting AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class Assignment extends Node {
	
public static class Operator {	
		
		private String token;
		private Operator(String token) { this.token = token; }
		public String toString() { return token; }
		
		public static final Operator ASSIGN = new Operator("=");//$NON-NLS-1$
		public static final Operator TIMES = new Operator("*=");//$NON-NLS-1$
		public static final Operator TIMESTIMES = new Operator("**=");//$NON-NLS-1$
		public static final Operator DIVIDE = new Operator("/=");//$NON-NLS-1$
		public static final Operator MODULO = new Operator("%=");//$NON-NLS-1$
		public static final Operator PLUS = new Operator("+=");//$NON-NLS-1$
		public static final Operator MINUS = new Operator("-=");//$NON-NLS-1$
		public static final Operator OR = new Operator("|=");//$NON-NLS-1$
		public static final Operator AND = new Operator("&=");//$NON-NLS-1$
		public static final Operator XOR = new Operator("xor=");//$NON-NLS-1$
		public static final Operator CONCAT = new Operator("::=");//$NON-NLS-1$
		public static final Operator NULLCONCAT = new Operator("?:=");//$NON-NLS-1$
		public static final Operator LEFT_SHIFT = new Operator("<<=");//$NON-NLS-1$
		public static final Operator RIGHT_SHIFT_ARITHMETIC = new Operator(">>=");//$NON-NLS-1$
		public static final Operator RIGHT_SHIFT_LOGICAL = new Operator(">>>=");//$NON-NLS-1$
		
		private static final Map CODES;
		static {
			CODES = new HashMap(16);
			Operator[] ops = {
					ASSIGN,
					TIMES,
					TIMESTIMES,
					DIVIDE,
					MODULO,
					PLUS,
					MINUS,
					OR,
					AND,
					XOR,
					CONCAT,
					NULLCONCAT,
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
	private Expression lhs;
	private Expression rhs;
	private Annotation annotationBinding;

	public Assignment(Operator operator, Expression lhs, Expression rhs, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.operator = operator;
		this.lhs = lhs;
		lhs.setParent(this);
		this.rhs = rhs;		
		rhs.setParent(this);
	}
	
    public Annotation resolveBinding() {
        return annotationBinding;
    }
    
    public void setBinding(Annotation binding) {
        this.annotationBinding = binding;
    }

	
	public Expression getLeftHandSide() {
		return lhs;
	}
	
	public Expression getRightHandSide() {
		return rhs;
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			lhs.accept(visitor);
			rhs.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new Assignment(operator, (Expression)lhs.clone(), (Expression)rhs.clone(), getOffset(), getOffset() + getLength());
	}
}
