package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlCloseStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.utils.SQL;

public class SqlCloseStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlCloseStatement stmt, Context ctx, TabbedWriter out) {
		out.println("try {");
		ctx.invoke(genExpression, stmt.getTarget(), ctx, out);
		if(SQL.isSQLResultSet(stmt.getTarget().getType())){
			out.println(".getResultSet().close();");
		}
		else{
			out.println(".getConnection().close();");
		}
		genSqlStatementEnd(stmt, ctx, out);
	}

}
