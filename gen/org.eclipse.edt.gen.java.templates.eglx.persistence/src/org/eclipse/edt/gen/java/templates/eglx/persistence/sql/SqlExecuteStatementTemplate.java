/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlExecuteStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.ext.Utils;

public class SqlExecuteStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlExecuteStatement stmt, Context ctx, TabbedWriter out) {
		String stmtVar = var_statement;
		if (stmt.getPreparedStatement() == null && stmt.getSqlString() != null) {
			String[] stmts = stmt.getSqlString().split("[;]");
			if (stmts.length == 1) {
				genSqlStatementSetup(stmt, ctx, out);
				out.println(stmtVar + ".execute();");
			}
			else {
				out.println("try {");
				out.println("java.sql.Statement ezeStmt = ");
				ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
				out.println(".getConnection().createStatement();");
				for (String sql : stmts) {
					if (!Utils.isComment(sql)) {
						out.print("ezeStmt.execute(");
						out.print(quoted(Utils.removeCommentsCRLFs(sql)));
						out.println(");");
					}
				}
			}
		}
		else {
			stmtVar = getExprString(stmt.getPreparedStatement(), ctx);
			out.println("try {");
			int idx = 1;
			if(stmt.getUsingExpressions() != null){
				for (Expression uexpr : stmt.getUsingExpressions()) {
					genSetColumnValue(stmt, uexpr, stmtVar, idx++, ctx, out);
				}
			}
			out.print(stmtVar);
			out.println(".execute();");
		}
		if (stmt.getUsingExpressions() != null && !stmt.getUsingExpressions().isEmpty()) {
			out.println("if (" + stmtVar + " instanceof java.sql.CallableStatement) {");
			out.print("java.sql.ParameterMetaData ezeParmData = ");
			out.println(stmtVar + ".getParameterMetaData();");
			int i = 1;
			for (Expression uexpr : stmt.getUsingExpressions()) {
				if (i == 1) out.print("int ");
				out.println("ezeParmMode = ezeParmData.getParameterMode("+ i + ");");
				out.println("if (ezeParmMode == java.sql.ParameterMetaData.parameterModeOut || ezeParmMode == java.sql.ParameterMetaData.parameterModeInOut) {");
				ctx.invoke(genExpression, uexpr, ctx, out);
				out.print(" = ");
				genGetColumnValueByIndex(uexpr.getType(), "((java.sql.CallableStatement)" + stmtVar + ")", i, ctx, out);
				out.println(";");
				out.println("}");
				i++;
			}
			out.println("}");
		}
		
		genSqlStatementEnd(stmt, ctx, out);
	}
	
}
