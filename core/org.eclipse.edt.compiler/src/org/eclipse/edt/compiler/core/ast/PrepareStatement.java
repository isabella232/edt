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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PrepareStatement AST node type.
 * 
 * For prepared statement ID, use getPreparedStatementID().
 * 
 * For from/for options, invoke accept() with an IASTVisitor that
 * overrides visit() for the following types:
 *  - ForExpressionClause
 *  - FromExpressionClause 
 *
 * @author Albert Ho
 * @author David Murray
 */
public class PrepareStatement extends Statement {

	private Expression sqlStmt;
	private FromOrToExpressionClause dataSource;	
	private WithClause withClause;

	public PrepareStatement(Expression sqlStmt, FromOrToExpressionClause dataSource, WithClause withClause, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		
		this.sqlStmt = sqlStmt;
		sqlStmt.setParent(this);
		
		this.dataSource = dataSource;
		dataSource.setParent(this);
		
		this.withClause = withClause;
		withClause.setParent(this);
	}
		
	public Expression getSqlStmt() {
		return sqlStmt;
	}


	public FromOrToExpressionClause getDataSource() {
		return dataSource;
	}


	public WithClause getWithClause() {
		return withClause;
	}


	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			sqlStmt.accept(visitor);
			dataSource.accept(visitor);
			withClause.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	public List getIOObjects() {
		return Collections.EMPTY_LIST;
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new PrepareStatement((Expression)sqlStmt.clone(), (FromOrToExpressionClause)dataSource.clone(), (WithClause)withClause.clone(), getOffset(), getOffset() + getLength());
	}
}
