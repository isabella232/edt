package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlReplaceStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.utils.SQL;

public class SqlReplaceStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlReplaceStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getSqlString() != null) {
			genSqlStatementSetup(stmt, ctx, out);
			EGLClass targetType = getTargetType(stmt);
			int i = 1;
			for (Field f : targetType.getFields()) {
				if (!SQL.isAssociationField(f) && !SQL.isKeyField(f)) {
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
			// We have an open result set already
			if (stmt.getTarget() != null) {
				for (Field f : ((EGLClass)stmt.getTarget()).getFields()) {
					if (SQL.isPersistable(f)) {
						ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
						out.println(".getResultSet();");
						out.print(".setObject(");
						out.print(quoted(SQL.getColumnName(f)));
						out.println(", ");
						ctx.invoke(genExpression, stmt.getTarget(), ctx, out);
						out.print('.');
						ctx.invoke(genName, f, ctx, out);
						out.println(");");
					}
				}
				ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
				out.println(".getResultSet().updateRow();");
			}
			genSqlStatementEnd(stmt, ctx, out);
		}
	}	
}
