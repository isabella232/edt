package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlActionStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlExecuteStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.utils.SQL;

public class SqlExecuteStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlExecuteStatement stmt, Context ctx, TabbedWriter out) {
		String stmtVar = var_statement;
		if (stmt.getPreparedStatement() == null && stmt.getSqlString() != null) {
			String[] stmts = stmt.getSqlString().split("[;]");
			if (stmts.length == 1) {
				genSqlStatementSetup(stmt, ctx, out);
				out.println(stmtVar + ".execute();");
			}
			else {
				out.println("try {");
				out.println("java.sql.Statement ezeStmt = ");
				ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
				out.println(".getConnection().createStatement();");
				for (String sql : stmts) {
					if (!SQL.isComment(sql)) {
						out.print("ezeStmt.execute(");
						out.print(quoted(SQL.removeCommentsCRLFs(sql)));
						out.println(");");
					}
				}
			}
		}
		else {
			stmtVar = getExprString(stmt.getPreparedStatement(), ctx);
			out.println("try {");
			out.print(stmtVar);
			out.println(".execute();");
		}
		if (stmt.getUsingExpressions() != null && !stmt.getUsingExpressions().isEmpty()) {
			out.println("if (" + stmtVar + " instanceof java.sql.CallableStatement) {");
			out.print("ezeParmData = ");
			out.println(stmtVar + ".getParameterMetaData();");
			int i = 1;
			for (Expression uexpr : stmt.getUsingExpressions()) {
				if (i == 1) out.print("int ");
				out.println("ezeParmMode = ezeParmData.getParameterMode("+ i + ");");
				out.println("if (ezeParmMode == java.sql.ParameterMetaData.parameterModeOut || ezeParmMode == java.sql.ParameterMetaData.parameterModeInOut) {");
				ctx.invoke(genExpression, uexpr, ctx, out);
				out.print(" = ");
				genGetColumnValueByIndex(uexpr.getType().getClassifier(), stmtVar, i, ctx, out);
				out.println(";");
				out.println("}");
				i++;
			}
			out.println("}");
		}
		
		genSqlStatementEnd(stmt, ctx, out);
	}
	
}
