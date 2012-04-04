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

/**
 * ReturnStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ReturnStatement extends Statement {

	private Expression parenthesizedExprOpt;

	public ReturnStatement(Expression parenthesizedExprOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		if(parenthesizedExprOpt != null) {
			this.parenthesizedExprOpt = parenthesizedExprOpt;
			parenthesizedExprOpt.setParent(this);
		}
	}
	
	public Expression getParenthesizedExprOpt() {
		return parenthesizedExprOpt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			if(parenthesizedExprOpt != null) parenthesizedExprOpt.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		Expression newParenthesizedExprOpt = parenthesizedExprOpt != null ? (Expression)parenthesizedExprOpt.clone() : null;
		
		return new ReturnStatement(newParenthesizedExprOpt, getOffset(), getOffset() + getLength());
	}
}
