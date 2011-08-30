package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlExecuteStatement;

public class SqlExecuteStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlExecuteStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getSqlString() != null) {
			genSqlStatementSetup(stmt, ctx, out);
			out.println(var_statement + ".execute();");
		}
		else {
			out.println("try {");
			ctx.invoke(genExpression, stmt.getPreparedStatement(), ctx, out);
			out.println(".execute();");
		}
		genSqlStatementEnd(stmt, ctx, out);
	}
}
