package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.sql.SqlExecuteStatement;

public class SqlExecuteStatementTemplate extends SqlIOStatementTemplate {

	public void genStatementBody(SqlExecuteStatement addStmt, Context ctx, TabbedWriter out) {
		if (addStmt.getSqlString() != null) {
			genSqlStatementSetup(addStmt, ctx, out);
			out.println(var_statement + ".execute();");
			genSqlStatementEnd(addStmt, ctx, out);
		}
		else {
			out.println(err_noSqlGenerated);
		}
	}
}
