package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.sql.SqlPrepareStatement;

public class SqlPrepareStatementTemplate extends SqlIOStatementTemplate {
	
	public void genStatementBody(SqlPrepareStatement stmt, Context ctx, TabbedWriter out) {
		genSqlStatementSetup(stmt, ctx, out, stmt.getPreparedStatementId());
		genSqlStatementEnd(stmt, ctx, out);
	}

}
