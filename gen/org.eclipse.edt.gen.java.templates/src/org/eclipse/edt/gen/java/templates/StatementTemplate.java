/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;

import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public abstract class StatementTemplate extends JavaTemplate {

	public void validate(Statement stmt, Context ctx, Object... args) {
	// statements may override this validation for specific checking
	}

	public void genStatement(Statement stmt, Context ctx, TabbedWriter out, Object... args) {
		genStatementBody(stmt, ctx, out, args);
		genStatementEnd(stmt, ctx, out, args);
	}

	public void genStatementNoBraces(Statement stmt, Context ctx, TabbedWriter out, Object... args) {
		genStatementBodyNoBraces(stmt, ctx, out, args);
		genStatementEnd(stmt, ctx, out, args);
	}

	public void genExpression(Expression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genExpression, expr, ctx, out, args);
	}

	public abstract void genStatementBody(Statement stmt, Context ctx, TabbedWriter out, Object... args);

	public void genStatementBodyNoBraces(Statement stmt, Context ctx, TabbedWriter out, Object... args) {
		genStatementBody(stmt, ctx, out, args);
	}

	public void genStatementEnd(Statement stmt, Context ctx, TabbedWriter out, Object... args) {
		out.println(';');
	}
}
