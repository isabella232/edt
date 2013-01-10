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

import org.eclipse.edt.compiler.core.IEGLConstants;

/**
 * AsExpression AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class AsExpression extends Expression {

	private Expression expr;
	private Type type;
	private Expression stringLiteral;

	public AsExpression(Expression expr, Type type, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
		this.type = type;
		type.setParent(this);
	}
	
	public AsExpression(Expression expr, Expression stringLiteral, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
		this.stringLiteral = stringLiteral;
		stringLiteral.setParent(this);
	}
	
	public Expression getExpression() {
		return expr;
	}
	
	public boolean hasType() {
		return type != null;
	}
	
	public Type getType() {
		return type;		
	}
	
	public boolean hasStringLiteral() {
		return stringLiteral != null;
	}
	
	public Expression getStringLiteral() {
		return stringLiteral;
	}
	
    public String getCanonicalString() {
    	StringBuilder result = new StringBuilder();
    	result.append(expr.getCanonicalString());
    	result.append(" ");
    	result.append(IEGLConstants.KEYWORD_AS);
    	result.append(" ");
    	result.append(type == null ? stringLiteral.getCanonicalString() : type.getCanonicalName());
    	return result.toString();
    }
    
    
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			if(type != null) {
				type.accept(visitor);
			}
			else {
				stringLiteral.accept(visitor);
			}
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return type == null ?
			new AsExpression((Expression)expr.clone(), (Expression)stringLiteral.clone(), getOffset(), getOffset() + getLength()) :
			new AsExpression((Expression)expr.clone(), (Type)type.clone(), getOffset(), getOffset() + getLength());
	}
	
	@Override
	public String toString() {
		return getCanonicalString();
	}
}
