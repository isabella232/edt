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
import org.eclipse.edt.mof.eglx.persistence.sql.SqlPrepareStatement;

public class SqlPrepareStatementTemplate extends SqlActionStatementTemplate {
	
	public void genStatementBody(SqlPrepareStatement stmt, Context ctx, TabbedWriter out) {
		String varName = getExprString(stmt.getPreparedStatement(), ctx);
		genSqlStatementSetup(stmt, ctx, out, varName, true);
		genSqlStatementEnd(stmt, ctx, out);
	}

}
