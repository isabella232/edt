/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
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
import java.util.Collections;
import java.util.List;

import org.eclipse.edt.compiler.internal.sql.SQLInfo;


/**
 * ExecuteStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class ExecuteStatement extends Statement {
	
	public static class ExecuteTarget implements Cloneable {
		int startOffset;
		int endOffset;
		
		public ExecuteTarget(int startOffset, int endOffset) {
			this.startOffset = startOffset;
			this.endOffset = endOffset;
		}
		
		boolean isUpdate() { return false; }
		boolean isDelete() { return false; }
		boolean isInsert() { return false; }
		
		boolean hasSQLStatement() { return false; }
		boolean hasPreparedStatementID() { return false; }
		
		InlineSQLStatement getSQLStatement() { return null; }
		String getPreparedStatementID() { return null; }
		
		protected Object clone() throws CloneNotSupportedException {
			throw new CloneNotSupportedException();
		}
		
		public int getOffset() {
			return startOffset;
		}
		
		public int getLength() {
			return endOffset - startOffset;
		}
		
	    public void setParent(Node parent) {
	    	//default is to do nothing
	    }
	    
		public void accept(IASTVisitor visitor) {
	    	//default is to do nothing
		}


	}
	
	public static class SQLStatementExecuteTarget extends ExecuteTarget {
		InlineSQLStatement SQLStatement = null;
		
		public SQLStatementExecuteTarget(InlineSQLStatement SQLStatement, int startOffset, int endOffset) {
			super(startOffset, endOffset);
			this.SQLStatement = SQLStatement;
		}
		
		boolean hasSQLStatement() {
			return SQLStatement != null;
		}
		
		InlineSQLStatement getSQLStatement() {
			return SQLStatement;
		}
		
		protected Object clone() throws CloneNotSupportedException {
			throw new CloneNotSupportedException();
		}
		
		@Override
		public void setParent(Node parent) {
			if (hasSQLStatement()) {
				getSQLStatement().setParent(parent);
			}
			super.setParent(parent);
		}
		
		@Override
		public void accept(IASTVisitor visitor) {
			if (hasSQLStatement()) {
				getSQLStatement().accept(visitor);
			}
			super.accept(visitor);
		}
	}
	
	public static class DefaultSQLStatementExecuteTarget extends SQLStatementExecuteTarget {
		public DefaultSQLStatementExecuteTarget(InlineSQLStatement SQLStatement, int startOffset, int endOffset) {
			super(SQLStatement, startOffset, endOffset);
		}
		
		protected Object clone() throws CloneNotSupportedException {
			return new DefaultSQLStatementExecuteTarget(SQLStatement, startOffset, endOffset);
		}
	}
	
	public static class UpdateExecuteTarget extends SQLStatementExecuteTarget {
		public UpdateExecuteTarget(InlineSQLStatement SQLStatement, int startOffset, int endOffset) {
			super(SQLStatement, startOffset, endOffset);
		}
		
		boolean isUpdate() {
			return true;
		}
		
		protected Object clone() throws CloneNotSupportedException {
			return new UpdateExecuteTarget(SQLStatement, startOffset, endOffset);
		}
	}
	
	public static class InsertExecuteTarget extends SQLStatementExecuteTarget {
		public InsertExecuteTarget(InlineSQLStatement SQLStatement, int startOffset, int endOffset) {
			super(SQLStatement, startOffset, endOffset);
		}
		
		boolean isInsert() {
			return true;
		}
		
		protected Object clone() throws CloneNotSupportedException {
			return new InsertExecuteTarget(SQLStatement, startOffset, endOffset);
		}
	}
	
	public static class DeleteExecuteTarget extends SQLStatementExecuteTarget {
		public DeleteExecuteTarget(InlineSQLStatement SQLStatement, int startOffset, int endOffset) {
			super(SQLStatement, startOffset, endOffset);
		}
		
		boolean isDelete() {
			return true;
		}
		
		protected Object clone() throws CloneNotSupportedException {
			return new DeleteExecuteTarget(SQLStatement, startOffset, endOffset);
		}
	}
	
	public static class PreparedStatementExecuteTarget extends ExecuteTarget {
		Expression sqlStmt = null;
		
		public PreparedStatementExecuteTarget(Expression expr, int startOffset, int endOffset) {
			super(startOffset, endOffset);
			this.sqlStmt = expr;
		}
		
		boolean hasPreparedStatementID() {
			return true;
		}
		
		Expression getSqlStmt() {
			return sqlStmt;
		}
		
		protected Object clone() throws CloneNotSupportedException {
			return new PreparedStatementExecuteTarget((Expression)sqlStmt.clone(), startOffset, endOffset);
		}
		
		@Override
		public void setParent(Node parent) {
			sqlStmt.setParent(parent);
			super.setParent(parent);
		}
		
		@Override
		public void accept(IASTVisitor visitor) {
			sqlStmt.accept(visitor);
			super.accept(visitor);
		}
	}

	private ExecuteTarget executeTarget;
	private List executeOptions;	// List of Symbols
	
	private List ioObjects;
	private SQLInfo sqlInfo;

	public ExecuteStatement(ExecuteTarget executeTarget, List executeOptions, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.executeTarget = executeTarget;
		executeTarget.setParent(this);
		
		this.executeOptions = setParent(executeOptions);
	}
	
	public boolean isUpdate() {
		return executeTarget.isUpdate();
	}
	
	public boolean isDelete() {
		return executeTarget.isDelete();
	}
	
	public boolean isInsert() {
		return executeTarget.isInsert();
	}

	public boolean isPreparedStatement() {
		return executeTarget.hasPreparedStatementID();
	}
	
	public String getPreparedStatementID() {
		return executeTarget.getPreparedStatementID();
	}
	
	public boolean hasInlineSQLStatement() {
		return executeTarget.hasSQLStatement();
	}
	
	public InlineSQLStatement getInlineSQLStatement() {
		return executeTarget.getSQLStatement();
	}	
		
	public List getExecuteOptions() {
		return executeOptions;
	}
	
	public ExecuteTarget getExecuteTarget() {
		return executeTarget;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {			
			executeTarget.accept(visitor);
			acceptChildren(visitor, executeOptions);			
		}
		visitor.endVisit(this);
	}
	
	public List getIOObjects() {
		if(ioObjects == null) {
			ioObjects = Collections.EMPTY_LIST;
			acceptChildren(new DefaultASTVisitor() {
				public boolean visit(ForExpressionClause forExpressionClause) {
					if(ioObjects == Collections.EMPTY_LIST) {
						ioObjects = new ArrayList();
					}
					ioObjects.add(forExpressionClause.getExpression());
					return false;
				}
			}, executeOptions);
		}
		return ioObjects;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new ExecuteStatement((ExecuteTarget)executeTarget.clone(), cloneList(executeOptions), getOffset(), getOffset() + getLength());
	}
    public SQLInfo getSqlInfo() {
        return sqlInfo;
    }
    public void setSqlInfo(SQLInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
    }
    
}
