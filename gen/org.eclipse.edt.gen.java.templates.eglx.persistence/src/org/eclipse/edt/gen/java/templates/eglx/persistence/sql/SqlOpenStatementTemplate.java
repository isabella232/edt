package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlActionStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlOpenStatement;

public class SqlOpenStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlOpenStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getSqlString() != null && !"".equals(stmt.getSqlString() != null)) {
			genSqlStatementSetup(stmt, ctx, out);
		}
		else {
			out.println("try {");
			if (stmt.getUsingExpressions()!= null) {
				int i = 1;
				String varName = var_statement;
				if(stmt.getPreparedStatement() != null){
					varName = getExprString(stmt.getPreparedStatement(), ctx);
				}
				for (Expression uexpr : stmt.getUsingExpressions()) {
					genSetColumnValue(uexpr, varName, i, ctx, out);
					out.println(";");
					i++;
				}
			}
		}
		String var_resultSet = ctx.nextTempName();
		out.print(class_ResultSet + " " + var_resultSet + " = ");
		if (stmt.getPreparedStatement() == null)
			out.println(var_statement + ".executeQuery();");
		else {
			ctx.invoke(genExpression, stmt.getPreparedStatement(), ctx, out);
			out.println(".executeQuery();");
		}
		Expression resultSet = getResultSet(stmt);
		ctx.invoke(genExpression, resultSet, ctx, out);
		out.print(" = ");
		ctx.invoke(genInstantiation, resultSet.getType(), ctx, out, var_resultSet);
		out.println(";");
		genSqlStatementEnd(stmt, ctx, out);
	}
	@Override
	protected Expression getResultSet(SqlActionStatement stmt) {
		return ((SqlOpenStatement)stmt).getResultSet();
	}
}
