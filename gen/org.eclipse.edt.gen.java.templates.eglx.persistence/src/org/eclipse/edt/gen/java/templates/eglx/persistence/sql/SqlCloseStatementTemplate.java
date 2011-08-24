package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.sql.SqlCloseStatement;

public class SqlCloseStatementTemplate extends SqlIOStatementTemplate {

	public void genStatementBody(SqlCloseStatement stmt, Context ctx, TabbedWriter out) {
		ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
		out.println(".getResultSet().close();");
	}

}
