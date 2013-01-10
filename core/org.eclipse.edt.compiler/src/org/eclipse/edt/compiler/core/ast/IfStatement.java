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

import java.util.ArrayList;
import java.util.List;

/**
 * IfStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class IfStatement extends Statement {

	private Expression expr;
	private List stmts;	// List of Nodes
	private ElseBlock elseOpt;

	public IfStatement(Expression expr, List stmts, ElseBlock elseOpt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.expr = expr;
		expr.setParent(this);
		this.stmts = setParent(stmts);
		if(elseOpt != null) {
			this.elseOpt = elseOpt;
			elseOpt.setParent(this);
		}
	}
	
	public Expression getCondition() {
		return expr;
	}
	
	public List getStmts() {
		return stmts;
	}
	
	public boolean hasElse() {
		return elseOpt != null;
	}
	
	public ElseBlock getElse() {
		return elseOpt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			expr.accept(visitor);
			acceptChildren(visitor, stmts);
			if(elseOpt != null) elseOpt.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	public boolean canIncludeOtherStatements() {
		return true;
	}
	
	public List getStatementBlocks() {
		List result = new ArrayList();
		result.add(stmts);
		if(elseOpt != null) {
			result.add(elseOpt.getStmts());
		}
		return result;
	}

	protected Object clone() throws CloneNotSupportedException {
		ElseBlock newElseOpt = elseOpt != null ? (ElseBlock)elseOpt.clone() : null;
		
		return new IfStatement((Expression)expr.clone(), cloneList(stmts), newElseOpt, getOffset(), getOffset() + getLength());
	}
}
