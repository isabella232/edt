package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Classifier;
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
				if (SQL.isUpdateable(f)) {
					out.print(var_statement);
					out.print(".setObject(" + i + ", ");
					genConvertValueStart(targetType, ctx, out);
					ctx.invoke(genExpression, stmt.getTarget(), ctx, out);
					out.print('.');
					ctx.invoke(genName, f, ctx, out);
					genConvertValueEnd(targetType, ctx, out);
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
				String var_resultSet = ctx.nextTempName();
				out.println("try {");
				// DataSource is a ResultSet already which is why there is no sql string
				out.print(class_ResultSet + " " + var_resultSet);
				out.print(" = ");
				ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
				out.println(".getResultSet();");
				for (Field f : ((EGLClass)stmt.getTarget().getType()).getFields()) {
					if (SQL.isUpdateable(f)) {
						EGLClass type = (EGLClass)f.getType().getClassifier();
						out.print(var_resultSet);
						out.print(".");
						genSqlUpdateValueMethodName(type, ctx, out);
						out.print("(");
						out.print(quoted(SQL.getColumnName(f)));
						out.print(", ");
						genConvertValueStart(type, ctx, out);
						ctx.invoke(genExpression, stmt.getTarget(), ctx, out);
						out.print('.');
						ctx.invoke(genName, f, ctx, out);
						genConvertValueEnd(type, ctx, out);
						out.println(");");
					}
				}
				ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
				out.println(".getResultSet().updateRow();");
			}
			genSqlStatementEnd(stmt, ctx, out);
		}
	}	
	public void genSqlUpdateValueMethodName(Classifier type, Context ctx, TabbedWriter out) {
		String name = "update";
		name += SQL.getSqlSimpleTypeName(type);
		out.print(name);
	}

}
