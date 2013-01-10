/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Statement;

public class StatementTemplate extends JavaScriptTemplate {

	public void preGen(Statement stmt, Context ctx) {
		// statements may override this validation for specific checking
	}

	public void genStatement(Statement stmt, Context ctx, TabbedWriter out) {
		ctx.invoke(genStatementBody, stmt, ctx, out);
		ctx.invoke(genStatementEnd, stmt, ctx, out);
	}

	public void genStatementNoBraces(Statement stmt, Context ctx, TabbedWriter out) {
		ctx.invoke(genStatementBodyNoBraces, stmt, ctx, out);
		ctx.invoke(genStatementEnd, stmt, ctx, out);
	}

	public void genStatementEnd(Statement stmt, Context ctx, TabbedWriter out) {
		out.println(";");
	}
	
	public Boolean isStatementRequiringWrappedParameters(Statement stmt, Context ctx){
		return Boolean.TRUE;
	}
}
