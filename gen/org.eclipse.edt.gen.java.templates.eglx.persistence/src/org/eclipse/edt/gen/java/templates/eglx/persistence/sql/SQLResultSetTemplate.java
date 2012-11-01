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
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlDeleteStatement;
import org.eclipse.edt.mof.eglx.persistence.sql.SqlReplaceStatement;

public class SQLResultSetTemplate extends JavaTemplate {

	public void genInstantiation(EGLClass dataSourceType, Context ctx, TabbedWriter out, String resultSet) {
		out.print("new ");
		ctx.invoke(genRuntimeTypeName, dataSourceType, ctx, out);
		out.print("(" + resultSet + ")");
	}
	
	public void genUpdateExpression(EGLClass resultSet, Context ctx, TabbedWriter out, SqlReplaceStatement stmt) {
		ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
		if (stmt.getUsingExpressions() == null || stmt.getUsingExpressions().isEmpty()) {
			out.print(".updateUsing(");
			ctx.invoke(genExpression, stmt.getTarget(), ctx, out);
			out.println(");");
		}
		else {
			out.print(".updateColumnsUsing(");
			boolean doComma = false;
			for (Expression expr : stmt.getUsingExpressions()) {
				if (doComma) out.print(", ");
				ctx.invoke(genExpression, expr, ctx, out);
				if (!doComma) doComma = true;
			}
			out.println(");");
		}
	}

	public void genDeleteExpression(EGLClass resultSet, Context ctx, TabbedWriter out, SqlDeleteStatement stmt) {
		ctx.invoke(genExpression, stmt.getDataSource(), ctx, out);
		out.println(".getResultSet().deleteRow();");
	}

}
