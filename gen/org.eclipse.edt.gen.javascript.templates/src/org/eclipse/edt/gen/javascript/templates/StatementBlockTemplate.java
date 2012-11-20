/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import java.util.List;

import org.eclipse.edt.gen.ReorganizeCode;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;

public class StatementBlockTemplate extends JavaScriptTemplate {

	public void preGen(StatementBlock block, Context ctx) {
		for (Statement stmt : block.getStatements()) {
			ctx.invoke(preGen, stmt, ctx);
		}
	}

	public void genStatementBody(StatementBlock block, Context ctx, TabbedWriter out) {
		out.println("{");
		processStatements(block, ctx, out);
		out.println("}");
	}

	public void genStatementBodyNoBraces(StatementBlock block, Context ctx, TabbedWriter out) {
		processStatements(block, ctx, out);
	}

	public void genStatementEnd(StatementBlock block, Context ctx, TabbedWriter out) {
		// StatementBlocks do not end with semicolons so do nothing here
	}

	protected void processStatements(StatementBlock block, Context ctx, TabbedWriter out) {
		for (Statement stmt : block.getStatements()) {
			ReorganizeCode reorganizeCode = new ReorganizeCode();
			List<StatementBlock> blockArray = reorganizeCode.reorgCode(stmt, ctx);
			if (blockArray != null && blockArray.get(0) != null)
				ctx.invoke(genStatementNoBraces, blockArray.get(0), ctx, out);
			ctx.invoke(genStatement, stmt, ctx, out);
			if (blockArray != null && blockArray.get(1) != null)
				ctx.invoke(genStatementNoBraces, blockArray.get(1), ctx, out);
		}
	}
}
