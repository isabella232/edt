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

/**
 * IsAExpression AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class IsAExpression extends Expression {

	private Expression expr;
	private Type type;

	public IsAExpression(Expression expr, Type type, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
		this.type = type;
		type.setParent(this);
	}
	
	public Expression getExpression() {
		return expr;
	}
	
	public Type getType() {
		return type;
	}
	
    public String getCanonicalString() {
  		return expr.getCanonicalString();
    }
    
    
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			type.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new IsAExpression((Expression)expr.clone(), (Type)type.clone(), getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		return expr.toString() + " isa " + type.toString();
	}
}
