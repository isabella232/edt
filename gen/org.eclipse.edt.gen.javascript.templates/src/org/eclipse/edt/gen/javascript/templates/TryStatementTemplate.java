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
import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.TryStatement;

public class TryStatementTemplate extends JavaScriptTemplate {

	public void genStatementBody(TryStatement stmt, Context ctx, TabbedWriter out, Object... args) {
		out.print("try ");
		ctx.gen(genStatement, stmt.getTryBlock(), ctx, out, args);
		for (ExceptionBlock exceptionBlock : stmt.getExceptionBlocks()) {
			genException(exceptionBlock, ctx, out, args);
		}
	}

	public void genException(ExceptionBlock exceptionBlock, Context ctx, TabbedWriter out, Object... args) {
		String exceptionVar = ctx.nextTempName();
		out.println("catch (" + exceptionVar + "){");
		out.println("if ( " + exceptionVar + " instanceof " + ctx.getNativeImplementationMapping(exceptionBlock.getException().getType()) + " ) {");
		out.print("var ");
		ctx.gen(genName, exceptionBlock.getException(), ctx, out, args);
		out.println(" = " + exceptionVar + ";");
		for (Statement stmt : exceptionBlock.getStatements()) {
			genExceptionStatement(stmt, ctx, out, args);
		}
		out.println("}");
		out.println("else {");
		out.println("throw " + exceptionVar + ";");
		out.println("}");
		out.println("}");
	}

	public void genExceptionStatement(Statement stmt, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genStatement, stmt, ctx, out, args);
	}

	public void genStatementEnd(TryStatement stmt, Context ctx, TabbedWriter out, Object... args) {
		// we don't want a semi-colon
	}
}
