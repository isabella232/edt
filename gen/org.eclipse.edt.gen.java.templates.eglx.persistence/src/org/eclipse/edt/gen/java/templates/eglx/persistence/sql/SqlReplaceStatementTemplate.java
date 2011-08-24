package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.sql.SqlReplaceStatement;

public class SqlReplaceStatementTemplate extends SqlIOStatementTemplate {

	public void genStatementBody(SqlReplaceStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getSqlString() != null) {
			genSqlStatementSetup(stmt, ctx, out);
			EGLClass targetType = getTargetType(stmt);
			int i = 1;
			for (Field f : targetType.getFields()) {
				if (!isAssociationField(f, ctx) && !isKeyField(f)) {
					out.print(var_statement);
					out.print(".setObject(" + i + ", ");
					ctx.invoke(genExpression, stmt.getTarget(), ctx, out);
					out.print('.');
					ctx.invoke(genName, f, ctx, out);
					out.println(");");
					i++;
				}
			}
			genDefaultWhereClauseParameterSettings(targetType, stmt.getTarget(), var_statement, i, ctx, out);
			out.println(var_statement + ".executeUpdate();");
			genSqlStatementEnd(stmt, ctx, out);
		}
		else {
			ctx.invoke("genUpdateExpression", stmt.getDataSource().getType(), ctx, out, stmt);
		}
	}	
}
