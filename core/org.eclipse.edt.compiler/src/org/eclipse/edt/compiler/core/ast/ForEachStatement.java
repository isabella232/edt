/*******************************************************************************
 * Copyright © 2005, 2010 IBM Corporation and others.
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
 * ForEachStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ForEachStatement extends Statement {
	
	public static abstract class ForEachTarget implements Cloneable {
		boolean isExpressionForEachTarget() { return false; }
		Expression getExpression() { return null; }
		
		boolean isResultSetForEachTarget() { return false; }
		String getResultSetID() { return null; }
		
		void setParent( Node parent ) {}
		void accept( IASTVisitor visitor ) {}
		
		protected abstract Object clone() throws CloneNotSupportedException;
	}
	
	public static class ExpressionForEachTarget extends ForEachTarget {
		private Expression expression;
		
		public ExpressionForEachTarget( Expression expression ) {
			this.expression = expression;
		}
		
		boolean isExpressionForEachTarget() {
			return true;
		}
		
		Expression getExpression() {
			return expression;
		}
		
		void setParent( Node parent ) {
			expression.setParent( parent );
		}
		
		void accept( IASTVisitor visitor ) {
			expression.accept( visitor );
		}
		
		protected Object clone() throws CloneNotSupportedException {
			return new ExpressionForEachTarget((Expression)expression.clone());
		}
	}
	
	public static class ResultSetForEachTarget extends ForEachTarget {
		private String ID;

		public ResultSetForEachTarget(String ID) {
			this.ID = ID;
		}
		
		boolean isResultSetForEachTarget() {
			return true;
		}
		
		String getResultSetID() {
			return ID;
		}
		
		protected Object clone() throws CloneNotSupportedException {
			return new ResultSetForEachTarget(new String(ID));
		}
	}

	private ForEachTarget foreachTarget;
	private IntoClause intoClauseOpt;
	private List stmts;	// List of Statements

	public ForEachStatement(ForEachTarget foreachTarget, IntoClause intoClauseOpt, List stmts, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.foreachTarget = foreachTarget;
		foreachTarget.setParent(this);
		if(intoClauseOpt != null) {
			this.intoClauseOpt = intoClauseOpt;
			intoClauseOpt.setParent(this);
		}
		this.stmts = setParent(stmts);
	}
	
	public boolean hasResultSetID() {
		return foreachTarget.isResultSetForEachTarget();
	}
	
	public String getResultSetID() {
		return foreachTarget.getResultSetID();
	}
	
	public boolean hasSQLRecord() {
		return foreachTarget.isExpressionForEachTarget();
	}
	
	public Expression getSQLRecord() {
		return foreachTarget.getExpression();
	}
	
	public boolean hasIntoClause() {
		return intoClauseOpt != null;
	}
	
	public IntoClause getIntoClause() {
		return intoClauseOpt;
	}
	
	public List getIntoItems() {
		return intoClauseOpt.getExpressions();
	}
	
	public List getStmts() {
		return stmts;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			foreachTarget.accept(visitor);
			if(intoClauseOpt != null) intoClauseOpt.accept(visitor);
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
		IntoClause newIntoClauseOpt = intoClauseOpt != null ? (IntoClause)intoClauseOpt.clone() : null;
		
		return new ForEachStatement((ForEachTarget)foreachTarget.clone(), newIntoClauseOpt, cloneList(stmts), getOffset(), getOffset() + getLength());
	}
}
