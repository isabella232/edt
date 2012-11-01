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
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.utils.TypeUtils;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlActionStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlOpenStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.ext.Utils;

public class SqlOpenStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlOpenStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getSqlString() != null && !"".equals(stmt.getSqlString() != null)) {
			genSqlStatementSetup(stmt, ctx, out);
		}
		else {
			out.println("try {");
			if (stmt.getUsingExpressions()!= null) {
				int i = 1;
				String varName = var_statement;
				if(stmt.getPreparedStatement() != null){
					varName = getExprString(stmt.getPreparedStatement(), ctx);
				}
				for (Expression uexpr : stmt.getUsingExpressions()) {
					genSetColumnValue(stmt, uexpr, varName, i, ctx, out);
					i++;
				}
			}
		}
		String var_resultSet = ctx.nextTempName();
		out.print(class_ResultSet + " " + var_resultSet + " = ");
		if (stmt.getPreparedStatement() == null)
			out.println(var_statement + ".executeQuery();");
		else {
			ctx.invoke(genExpression, stmt.getPreparedStatement(), ctx, out);
			out.println(".executeQuery();");
		}
		Expression resultSet = getResultSet(stmt);
		ctx.invoke(genExpression, resultSet, ctx, out);
		out.print(" = ");
		ctx.invoke(genInstantiation, resultSet.getType(), ctx, out, var_resultSet);
		out.println(";");
		genSqlStatementEnd(stmt, ctx, out);
	}
	protected void genSetStatementsForForClause(SqlActionStatement stmt, String var_stmt, Context ctx, TabbedWriter out){
		if (stmt.getTargets().size() == 2) {//resultset and a for clause
			Expression target = stmt.getTargets().get(1);
			EGLClass targetType = (EGLClass)target.getType().getClassifier();
			if (!TypeUtils.isDynamicType(targetType)) {
				int i = 1;
				for (Field f : targetType.getFields()) {
					if (Utils.isKeyField(f) && Utils.isMappedSQLType((EGLClass)f.getType().getClassifier())){
						genSetColumnValue(f, var_stmt, getExprString(stmt.getTargets().get(1), ctx), i, ctx, out);
						out.println(";");
						i++;
					}
				}
			}
		}
	}
	@Override
	protected Expression getResultSet(SqlActionStatement stmt) {
		return ((SqlOpenStatement)stmt).getResultSet();
	}
}
