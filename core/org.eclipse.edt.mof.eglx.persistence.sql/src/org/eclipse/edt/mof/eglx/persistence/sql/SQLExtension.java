package org.eclipse.edt.mof.eglx.persistence.sql;

import org.eclipse.edt.compiler.ASTValidator;
import org.eclipse.edt.compiler.ICompilerExtension;
import org.eclipse.edt.compiler.SystemEnvironmentUtil;
import org.eclipse.edt.compiler.core.ast.AbstractASTVisitor;
import org.eclipse.edt.compiler.core.ast.AddStatement;
import org.eclipse.edt.compiler.core.ast.CloseStatement;
import org.eclipse.edt.compiler.core.ast.DeleteStatement;
import org.eclipse.edt.compiler.core.ast.ExecuteStatement;
import org.eclipse.edt.compiler.core.ast.Expression;
import org.eclipse.edt.compiler.core.ast.ForEachStatement;
import org.eclipse.edt.compiler.core.ast.GetByKeyStatement;
import org.eclipse.edt.compiler.core.ast.GetByPositionStatement;
import org.eclipse.edt.compiler.core.ast.Node;
import org.eclipse.edt.compiler.core.ast.OpenStatement;
import org.eclipse.edt.compiler.core.ast.PrepareStatement;
import org.eclipse.edt.compiler.core.ast.ReplaceStatement;
import org.eclipse.edt.compiler.internal.egl2mof.ElementGenerator;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.eglx.persistence.sql.gen.SQLActionStatementGenerator;
import org.eclipse.edt.mof.eglx.persistence.sql.gen.SqlActionStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.utils.SQL;
import org.eclipse.edt.mof.eglx.persistence.sql.validation.SQLActionStatementValidator;

public class SQLExtension implements ICompilerExtension {
	
	@Override
	public String[] getSystemEnvironmentPaths() {
		return new String[]{SystemEnvironmentUtil.getSystemLibraryPath(SqlActionStatement.class, "egllib")};
	}
	
	@Override
	public Class[] getExtendedTypes() {
		return new Class[]{AddStatement.class, CloseStatement.class, DeleteStatement.class, ExecuteStatement.class, ForEachStatement.class,
				GetByKeyStatement.class, GetByPositionStatement.class, OpenStatement.class, PrepareStatement.class, ReplaceStatement.class};
	}
	
	@Override
	public ElementGenerator getElementGeneratorFor(Node node) {
		if (shouldExtend(node)) {
			return new SQLActionStatementGenerator();
		}
		return null;
	}
	
	@Override
	public ASTValidator getValidatorFor(Node node) {
		if (shouldExtend(node)) {
			return new SQLActionStatementValidator();
		}
		return null;
	}
	
	private boolean shouldExtend(Node node) {
		// The types of various nodes dictate whether this is an SQL statement. Most common is the FromOrToExpressionClause.
		final boolean[] result = new boolean[1];
		node.accept(new AbstractASTVisitor() {
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.FromOrToExpressionClause clause) {
				if (!result[0]) {
					result[0] = SQL.isSQLDataSource(exprToType(clause.getExpression())) || SQL.isSQLResultSet(exprToType(clause.getExpression()));
				}
				return false;
			};
			@Override
			public boolean visit(org.eclipse.edt.compiler.core.ast.WithExpressionClause clause) {
				if (!result[0]) {
					result[0] = SQL.isSQLStatement(exprToType(clause.getExpression()));
				}
				return false;
			};
			@Override
			public boolean visit(CloseStatement stmt) {
				if (!result[0]) {
					result[0] = SQL.isSQLDataSource(exprToType(stmt.getExpr())) || SQL.isSQLResultSet(exprToType(stmt.getExpr()));
				}
				return false;
			};
			@Override
			public void endVisit(OpenStatement stmt) {
				if (!result[0]) {
					result[0] = SQL.isSQLResultSet(exprToType(stmt.getResultSet()));
				}
			};
		});
		
		return result[0];
	}
	
	private Type exprToType(Expression expr) {
		if (expr != null) {
			return expr.resolveType();
		}
		return null;
	}
}
