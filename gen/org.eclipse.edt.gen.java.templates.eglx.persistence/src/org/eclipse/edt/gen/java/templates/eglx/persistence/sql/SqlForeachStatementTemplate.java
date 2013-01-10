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
import org.eclipse.edt.mof.eglx.persistence.sql.SqlForEachStatement;

public class SqlForeachStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlForEachStatement stmt, Context ctx, TabbedWriter out) {
		String var_resultSet = ctx.nextTempName();
		if (stmt.getSqlString() != null && !"".equals(stmt.getSqlString())) {
			genSqlStatementSetup(stmt, ctx, out);
			out.println(class_ResultSet + " " + var_resultSet + " = " + var_statement + ".executeQuery();");
		}
		else {
			out.println("try {");
			// DataSource is a ResultSet already which is why there is no sql string
			out.print(class_ResultSet + " " + var_resultSet);
			out.print(" = ");
			ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
			out.println(".getResultSet();");
		}
		out.println("while (" + var_resultSet + ".next()) {");
		if (stmt.getTargets().size() == 1) {
			genGetSingleRowFromResultSet(stmt.getTarget(), var_resultSet, ctx, out);			
		}
		else {
			genGetSingleRowFromResultSet(stmt.getTargets(), var_resultSet, ctx, out);
		}
		ctx.invoke(genStatement, stmt.getBody(), ctx, out);
		out.println('}');
		genSqlStatementEnd(stmt, ctx, out);
	}
}
