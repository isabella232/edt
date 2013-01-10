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
package org.eclipse.edt.mof.eglx.persistence.sql.ext;

import java.util.List;

import org.eclipse.edt.compiler.core.ast.AbstractASTExpressionVisitor;
import org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause;
import org.eclipse.edt.compiler.core.ast.InlineSQLStatement;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.WithExpressionClause;
import org.eclipse.edt.compiler.core.ast.WithInlineSQLClause;
import org.eclipse.edt.compiler.internal.egl2mof.AbstractElementGenerator;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlActionStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlAddStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlCloseStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlDeleteStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlExecuteStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlFactory;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlForEachStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlGetByKeyStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlOpenStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlPrepareStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlReplaceStatement;
import org.eclipse.edt.mof.serialization.IEnvironment;

public class SQLActionStatementGenerator extends AbstractElementGenerator {
	
	SqlFactory factory = SqlFactory.INSTANCE;
	
	public SQLActionStatementGenerator() {
		super(null);
	}
	
	public SQLActionStatementGenerator(IEnvironment env) {
		super(env);
	}

	@SuppressWarnings("unchecked")
	private void doCommonVisit(org.eclipse.edt.compiler.core.ast.Statement node, final SqlActionStatement stmt) {
		node.accept(new AbstractASTExpressionVisitor() {
			public boolean visit(FromOrToExpressionClause clause) {
				clause.getExpression().accept(SQLActionStatementGenerator.this);
				stmt.setDataSource((Expression) stack.pop());
				return false;
			}
			public boolean visit(WithInlineSQLClause sqlStmt) {
				String sql = sqlStmt.getSqlStmt().getValue();
				stmt.setHasExplicitSql(true);
				stmt.setSqlString(sql);
				return false;
			}
			public boolean visit(WithExpressionClause sqlStmt) {
				sqlStmt.getExpression().accept(SQLActionStatementGenerator.this);
				stmt.setPreparedStatement((Expression)stack.pop());
				return false;
			}
			public boolean visit(InlineSQLStatement sqlStmt) {
				String sql = sqlStmt.getValue();
				stmt.setHasExplicitSql(true);
				stmt.setSqlString(sql);
				return false;
			}
		});
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.AddStatement node) {
		final SqlAddStatement stmt = factory.createSqlAddStatement();
		stack.push(stmt);
		doCommonVisit(node, stmt);
		return false;
	}
	
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.CloseStatement node) {
		SqlCloseStatement stmt = factory.createSqlCloseStatement();
		stack.push(stmt);
		doCommonVisit(node, stmt);
		return false;
	}
	
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByPositionStatement getByPositionStatement) {
		Statement stmt = factory.createGetByPositionStatement();
		stack.push(stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.DeleteStatement node) {
		SqlDeleteStatement stmt = factory.createSqlDeleteStatement();
		stack.push(stmt);
		doCommonVisit(node, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ExecuteStatement node) {
		SqlExecuteStatement stmt = factory.createSqlExecuteStatement();
		stack.push(stmt);
		doCommonVisit(node, stmt);
		return false;		
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ForEachStatement forEachStatement) {
		SqlForEachStatement forEachStmt = factory.createSqlForEachStatement();
		stack.push(forEachStmt);
		doCommonVisit(forEachStatement, forEachStmt);
		
		for (Node expr : (List<Node>)forEachStatement.getTargets()) {
			expr.accept(this);
			forEachStmt.getTargets().add((Expression)stack.pop());
		}

		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.GetByKeyStatement node) {
		SqlGetByKeyStatement stmt = factory.createSqlGetByKeyStatement();
		stack.push(stmt);
		doCommonVisit(node, stmt);
		return false;
	}


	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.OpenStatement node) {
		SqlOpenStatement stmt = factory.createSqlOpenStatement();
		stack.push(stmt);
		node.getResultSet().accept(this);
		stmt.getTargets().add((Expression)stack.pop());
		doCommonVisit(node, stmt);
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.PrepareStatement node) {
		SqlPrepareStatement stmt = factory.createSqlPrepareStatement();
		stack.push(stmt);
		
		// We don't use doCommonVisit() because we want the sql string a little different.
		if (node.getSqlStmt() != null) {
			node.getSqlStmt().accept(this);
			stmt.setPreparedStatement((Expression)stack.pop());
			stmt.getTargets().add(stmt.getPreparedStatement());
		}
		
		if (node.getWithClause() != null) {
			node.getWithClause().accept(this);
			if (node.getWithClause().isWithExpression()) {
				stmt.setSqlStringExpr((Expression)stack.pop());
			}
			if (node.getWithClause().isWithInlineSQL()) {
				InlineSQLStatement inline = ((WithInlineSQLClause)node.getWithClause()).getSqlStmt();
				String sql = inline.getValue().replaceAll("[\\n\\r]", " ");
				stmt.setSqlString(sql);
			}
		}
		
		return false;
	}

	@Override
	public boolean visit(org.eclipse.edt.compiler.core.ast.ReplaceStatement node) {
		SqlReplaceStatement stmt = factory.createSqlReplaceStatement();
		stack.push(stmt);
		doCommonVisit(node, stmt);
		return false;
	}
}
