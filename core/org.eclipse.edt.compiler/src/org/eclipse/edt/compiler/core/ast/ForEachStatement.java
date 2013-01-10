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
		FromOrToExpressionClause getResultSet() { return null; }
		
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
		private FromOrToExpressionClause resultSet;

		public ResultSetForEachTarget(FromOrToExpressionClause resultSet) {
			this.resultSet = resultSet;
		}
		
		boolean isResultSetForEachTarget() {
			return true;
		}
		
		FromOrToExpressionClause getResultSet() {
			return resultSet;
		}
		
		void setParent( Node parent ) {
			resultSet.setParent( parent );
		}
		
		void accept( IASTVisitor visitor ) {
			resultSet.accept( visitor );
		}
		
		protected Object clone() throws CloneNotSupportedException {
			return new ResultSetForEachTarget((FromOrToExpressionClause) resultSet.clone());
		}
	}

	private List targets;
	private SimpleName declarationName;
	private Type declarationType;
	private boolean isNullable;
	private FromOrToExpressionClause resultSet;
	private List stmts;	// List of Statements
	private int closingParenOffset;

	public ForEachStatement(List targets, SimpleName declarationName, Type declarationType, Boolean isNullable, FromOrToExpressionClause resultSet, List stmts, int closingParenOffset, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		if (targets != null) {
			this.targets = setParent(targets);
		}
		if (declarationName != null) {
			this.declarationName = declarationName;
			declarationName.setParent(this);
		}
		if (declarationType != null) {
			this.declarationType = declarationType;
			declarationType.setParent(this);
		}
		this.resultSet = resultSet;
		resultSet.setParent(this);
		this.stmts = setParent(stmts);
		this.closingParenOffset = closingParenOffset;
		this.isNullable = isNullable.booleanValue();
	}
	
	public List<Node> getTargets() {
		return targets;
	}
	
	public boolean hasResultSet() {
		return true;
	}
	
	public int getClosingParenOffset() {
		return closingParenOffset;
	}

	public FromOrToExpressionClause getResultSet() {
		return resultSet;
	}
	
	public boolean hasSQLRecord() {
		return true;
	}
	
	public Expression getSQLRecord() {
		if (targets != null && targets.size() > 0) {
			return (Expression)targets.get(0);
		}
		return null;
	}
		
	public List<Node> getStmts() {
		return stmts;
	}
	
	public boolean hasVariableDeclaration() {
		return declarationName != null;
	}
	
	/**
	 * Returns the declaration name if this is a foreach statement that
	 * include a variable declaration. Otherwise, returns null.
	 */
	public SimpleName getVariableDeclarationName() {
		return declarationName;
	}
	
	/**
	 * Returns the declaration type if this is a foreach statement that
	 * include a variable declaration. Otherwise, returns null.
	 */
	public Type getVariableDeclarationType() {
		return declarationType;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			if (targets != null) {
				acceptChildren(visitor, targets);
			}
			else {
				declarationName.accept(visitor);
				declarationType.accept(visitor);
			}
			resultSet.accept(visitor);
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
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder(50);
		buf.append("foreach(");
		
		if (targets != null) {
			boolean comma = false;
			for (Expression e : (List<Expression>)targets) {
				buf.append(e.getCanonicalString());
				if (comma) {
					buf.append(", ");
				}
				else {
					comma = true;
				}
			}
		}
		else {
			buf.append(declarationName.getCanonicalString());
			buf.append(' ');
			buf.append(declarationType.getCanonicalName());
		}
		
		buf.append(" from ");
		buf.append(resultSet.getExpression().getCanonicalString());
		buf.append(')');
		return buf.toString();
	}
	
	public boolean isNullable() {
		return isNullable;
	}
	
	protected Object clone() throws CloneNotSupportedException {		
		return new ForEachStatement(cloneList(targets),
				declarationName == null ? null : (SimpleName)declarationName.clone(),
				declarationType == null ? null : (Type)declarationType.clone(),
				Boolean.valueOf(isNullable),
				(FromOrToExpressionClause) resultSet.clone(), cloneList(stmts), closingParenOffset, getOffset(), getOffset() + getLength());
	}
}
