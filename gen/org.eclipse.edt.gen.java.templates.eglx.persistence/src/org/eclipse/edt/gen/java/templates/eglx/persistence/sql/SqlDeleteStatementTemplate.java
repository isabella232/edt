package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlDeleteStatement;

public class SqlDeleteStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlDeleteStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getSqlString() != null) {
			genSqlStatementSetup(stmt, ctx, out);
			EGLClass targetType = getTargetType(stmt);
			genDefaultWhereClauseParameterSettings(targetType, stmt.getTarget(), var_statement, 1, ctx, out);
			out.println(var_statement + ".execute();");
		}
		else {
			out.println("try {");
			ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
			out.println(".getResultSet().deleteRow();");
		}
		genSqlStatementEnd(stmt, ctx, out);
	}
}
