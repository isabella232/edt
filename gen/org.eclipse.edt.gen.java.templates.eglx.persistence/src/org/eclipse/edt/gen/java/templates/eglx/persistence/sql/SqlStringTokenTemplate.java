package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.sql.SqlIOStatement;
import org.eclipse.edt.mof.egl.sql.SqlStringToken;

public class SqlStringTokenTemplate extends SqlTokenTemplate {

	public void genToken(SqlStringToken token, Context ctx, TabbedWriter out, SqlIOStatement sqlStmt) {
		out.print(token.getSqlString());
	}

}
