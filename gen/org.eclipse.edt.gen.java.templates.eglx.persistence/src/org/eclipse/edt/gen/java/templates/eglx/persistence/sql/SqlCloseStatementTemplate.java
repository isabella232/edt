package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlCloseStatement;

public class SqlCloseStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlCloseStatement stmt, Context ctx, TabbedWriter out) {
		out.println("try {");
		ctx.invoke(genExpression, stmt.getTarget(), ctx, out);
		out.println(".getResultSet().close();");
		genSqlStatementEnd(stmt, ctx, out);
	}

}
