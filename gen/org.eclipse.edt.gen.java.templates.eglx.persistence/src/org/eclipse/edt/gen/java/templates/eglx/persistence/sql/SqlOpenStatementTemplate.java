package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlOpenStatement;

public class SqlOpenStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlOpenStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getSqlString() != null) {
			genSqlStatementSetup(stmt, ctx, out);
			if (stmt.getUsingExpressions()!= null) {
				int i = 1;
				for (Expression uexpr : stmt.getUsingExpressions()) {
					out.print(var_statement + ".setObject(" + i + ", ");
					ctx.invoke(genExpression, uexpr, ctx, out);
					out.println(");");
					i++;
				}
			}
			out.println(class_ResultSet + " " + var_resultSet + " = " + var_statement + ".executeQuery();");
			ctx.invoke(genExpression, stmt.getResultSet(), ctx, out);
			out.print(" = ");
			ctx.invoke(genInstantiation, stmt.getResultSet().getType(), ctx, out, var_resultSet);
			out.println(";");
			genSqlStatementEnd(stmt, ctx, out);
		}
		else {
			out.println(err_noSqlGenerated);
		}
	}
	
}
