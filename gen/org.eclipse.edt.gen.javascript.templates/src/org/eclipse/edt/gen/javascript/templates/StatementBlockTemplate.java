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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;

public class StatementBlockTemplate extends JavascriptTemplate {

	public void validate(StatementBlock block, Context ctx, Object... args) {
		for (Statement stmt : block.getStatements()) {
			ctx.validate(validate, stmt, ctx, args);
		}
	}

	public void genStatementBody(StatementBlock block, Context ctx, TabbedWriter out, Object... args) {
		out.println("{");
		processStatements(block, ctx, out, args);
		out.println("}");
	}

	public void genStatementBodyNoBraces(StatementBlock block, Context ctx, TabbedWriter out, Object... args) {
		processStatements(block, ctx, out, args);
	}

	public void genStatementEnd(StatementBlock block, Context ctx, TabbedWriter out, Object... args) {
		// StatementBlocks do not end with semicolons so do nothing here
	}

	private void processStatements(StatementBlock block, Context ctx, TabbedWriter out, Object... args) {
		for (Statement stmt : block.getStatements()) {
			ctx.gen(genStatement, stmt, ctx, out, args);
		}
	}
}
