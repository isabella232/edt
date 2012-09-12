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
import org.eclipse.edt.mof.eglx.persistence.sql.gen.SqlActionStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.gen.SqlDeleteStatement;

public class SqlDeleteStatementTemplate extends SqlActionStatementTemplate {

	public void genStatementBody(SqlDeleteStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getSqlString() != null) {
			genSqlStatementSetup(stmt, ctx, out);
			genWhereClauseParameterSettings(stmt, var_statement, 1, ctx, out);
			out.println(var_statement + ".execute();");
		}
		else {
			out.println("try {");
			ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
			out.println(".getResultSet().deleteRow();");
		}
		genSqlStatementEnd(stmt, ctx, out);
	}
	protected void genSetStatementsForUsingClause(SqlActionStatement stmt, String var_stmt, Context ctx, TabbedWriter out){

	}
}
