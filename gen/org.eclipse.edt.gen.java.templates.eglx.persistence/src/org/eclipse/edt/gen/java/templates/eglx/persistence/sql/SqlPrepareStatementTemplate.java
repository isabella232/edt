package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlPrepareStatement;

public class SqlPrepareStatementTemplate extends SqlActionStatementTemplate {
	
	public void genStatementBody(SqlPrepareStatement stmt, Context ctx, TabbedWriter out) {
		String varName = getExprString(stmt.getPreparedStatement(), ctx);
		genSqlStatementSetup(stmt, ctx, out, varName, true);
		genSqlStatementEnd(stmt, ctx, out);
	}

}
