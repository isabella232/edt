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

import java.util.List;

import org.eclipse.edt.gen.ReorganizeCode;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.StatementBlock;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class StatementBlockTemplate extends JavaTemplate {

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

	protected void processStatements(StatementBlock block, Context ctx, TabbedWriter out, Object... args) {
		ctx.setCurrentFile(IRUtils.getFileName(block));
		for (Statement stmt : block.getStatements()) {
			ReorganizeCode reorganizeCode = new ReorganizeCode();
			List<StatementBlock> blockArray = reorganizeCode.reorgCode(stmt, ctx);
			if (blockArray != null && blockArray.get(0) != null)
				ctx.gen(genStatementNoBraces, blockArray.get(0), ctx, out, args);
			ctx.gen(genStatement, stmt, ctx, out, args);
			if (blockArray != null && blockArray.get(1) != null)
				ctx.gen(genStatementNoBraces, blockArray.get(1), ctx, out, args);
		}
	}
}
