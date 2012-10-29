/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates.eglx.persistence.sql;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.eglx.persistence.sql.Utils;
import org.eclipse.edt.mof.eglx.persistence.sql.gen.SqlActionStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.gen.SqlReplaceStatement;

public class SqlReplaceStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlReplaceStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getSqlString() != null) {
			genSqlStatementSetup(stmt, ctx, out);
			if(stmt.getUsingExpressions() != null && stmt.getUsingExpressions().size() > 0 ){
				int i = 1;
				for (Expression uexpr : stmt.getUsingExpressions()) {
					genSetColumnValue(stmt, uexpr, var_statement, i, ctx, out);
					i++;
				}
			}
			else{
				EGLClass targetType = getTargetType(stmt);
				int i = 1;
				for (Field f : targetType.getFields()) {
					if (!Utils.isKeyField(f) && Utils.isUpdateable(f) && Utils.isMappedSQLType((EGLClass)f.getType().getClassifier())) {
						genSetColumnValue(f, var_statement, getExprString(stmt.getTarget(), ctx), i, ctx, out);
						i++;
					}
				}
				genWhereClauseParameterSettings(stmt, var_statement, i, ctx, out);
			}
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
					if (Utils.isUpdateable(f) && Utils.isMappedSQLType((EGLClass)f.getType().getClassifier())) {
						if(f.isNullable()){
							out.print("if(null == ");
							out.print(getExprString(stmt.getTarget(), ctx));
							out.print('.');
							ctx.invoke(genName, f, ctx, out);
							out.println("){");
							out.print(var_resultSet);
							out.print(".updateNull(");
							out.print(quoted(Utils.getColumnName(f)));
							out.println(");");
							out.println("}");
							out.println("else{");
						}
						EGLClass type = (EGLClass)f.getType().getClassifier();
						out.print(var_resultSet);
						out.print(".");
						genSqlUpdateValueMethodName(type, ctx, out);
						out.print("(");
						out.print(quoted(Utils.getColumnName(f)));
						out.print(", ");
						genConvertValueStart(type, ctx, out);
						ctx.invoke(genExpression, stmt.getTarget(), ctx, out);
						out.print('.');
						ctx.invoke(genName, f, ctx, out);
						genConvertValueEnd(type, ctx, out);
						out.println(");");
						if(f.isNullable()){
							out.println("}");
						}
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
		name += Utils.getSqlSimpleTypeName(type);
		out.print(name);
	}
	protected void genSetStatementsForUsingClause(SqlActionStatement stmt, String var_stmt, Context ctx, TabbedWriter out){
	}
}
