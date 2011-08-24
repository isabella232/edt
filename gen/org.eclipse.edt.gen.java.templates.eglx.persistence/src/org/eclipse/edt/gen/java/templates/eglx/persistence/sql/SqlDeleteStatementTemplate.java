package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.sql.SqlDeleteStatement;

public class SqlDeleteStatementTemplate extends SqlIOStatementTemplate {

	public void genStatementBody(SqlDeleteStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getSqlString() != null) {
			genSqlStatementSetup(stmt, ctx, out);
			if (!stmt.noCursor()) {
				out.print(var_statement + ".setCursorName(");
				// Assumes the generator puts the cursor name on the result set from the OPEN
				String cursorName = getCursorNameFromDataSource(stmt.getDataSource(), ctx);
				out.print(quoted(cursorName));
				out.println(");");
			}
			else {
				EGLClass targetType = getTargetType(stmt);
				genDefaultWhereClauseParameterSettings(targetType, stmt.getTarget(), var_statement, 1, ctx, out);
			}
			out.println(var_statement + ".execute();");
			genSqlStatementEnd(stmt, ctx, out);
		}
		else {
			ctx.invoke("genDeleteExpression", stmt.getDataSource(), ctx, out, stmt);
		}
	}
}
