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

import org.eclipse.edt.compiler.core.IEGLConstants;

/**
 * WithInlineSQLClause AST node type.
 *
 * @author Albert Ho
 * @author David Murray
 */
public class WithInlineSQLClause extends WithInlineClause {
	
	public static IOStatementClauseInfo INFO = new IOStatementClauseInfo() {
		public String getClauseKeyword() {
			return IEGLConstants.KEYWORD_WITH;
		}

		public String getContentPrefix() {
			return "#sql{";
		}

		public String getContentSuffix() {
			return "}";
		}

		public int getContentType() {
			return IOStatementClauseInfo.INLINE_STMT_VALUE;
		}		
	};
	
	private InlineSQLStatement sqlStmt;

	public WithInlineSQLClause(InlineSQLStatement sqlStmt, int startOffset, int endOffset) {
		super(startOffset, endOffset);
		
		this.sqlStmt = sqlStmt;
		sqlStmt.setParent(this);
	}
	
	public InlineSQLStatement getSqlStmt() {
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
		return new WithInlineSQLClause((InlineSQLStatement) sqlStmt.clone(), getOffset(), getOffset() + getLength());
	} 
	
	@Override
	public boolean isWithInlineSQL() {
		return true;
	}

	@Override
	public String getStatement() {
		return getSqlStmt().getValue();
	}
}
