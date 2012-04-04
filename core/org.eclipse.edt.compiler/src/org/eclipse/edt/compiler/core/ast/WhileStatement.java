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

import java.util.Arrays;
import java.util.List;

/**
 * WhileStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class WhileStatement extends Statement {

	private Expression expr;
	private List stmts;	// List of Nodes

	public WhileStatement(Expression expr, List stmts, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
		this.stmts = setParent(stmts);
	}
	
	public Expression getExpr() {
		return expr;
	}
	
	public List getStmts() {
		return stmts;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			acceptChildren(visitor, stmts);
		}
		visitor.endVisit(this);
	}
	
	public boolean canIncludeOtherStatements() {
		return true;
	}
	
	public List getStatementBlocks() {
		return Arrays.asList(new List[] {stmts});
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new WhileStatement((Expression)expr.clone(), cloneList(stmts), getOffset(), getOffset() + getLength());
	}
}
