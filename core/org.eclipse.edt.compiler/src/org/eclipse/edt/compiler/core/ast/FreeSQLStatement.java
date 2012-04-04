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
 * FreeSQLStatement AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class FreeSQLStatement extends Statement {

	private Expression sqlStmt;

	public FreeSQLStatement(Expression sqlStmt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.sqlStmt = sqlStmt;
		sqlStmt.setParent(this);
	}
	
	public Expression getSqlStmt() {
		return sqlStmt;
	}
	
	public void accept(IASTVisitor visitor) {
		boolean visitChildren = visitor.visit(this);
		if(visitChildren) {
			sqlStmt.accept(visitor);
		}
		visitor.endVisit(this);
	}
	
	protected Object clone() throws CloneNotSupportedException {
		return new FreeSQLStatement((Expression)sqlStmt.clone(), getOffset(), getOffset() + getLength());
	}
}
