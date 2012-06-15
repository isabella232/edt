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
package org.eclipse.edt.gen.java.templates;

import java.util.List;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.TryStatement;

public class TryStatementTemplate extends JavaTemplate {

	public void genStatementBody(TryStatement stmt, Context ctx, TabbedWriter out) {
		out.print("try ");
		ctx.invoke(genStatement, stmt.getTryBlock(), ctx, out);
		// write out smap info, otherwise the onexception block gets included with the last line of the try block
		ctx.writeSmapLine();
		List<ExceptionBlock> blocks = stmt.getExceptionBlocks();
		if (blocks.isEmpty()) {
			// Since there are no onException blocks, generate code to ignore all
			// handleable exceptions.
			String exTemp = ctx.nextTempName();
			out.println("catch ( java.lang.Exception " + exTemp + " ) {");
			out.println("org.eclipse.edt.javart.util.JavartUtil.checkHandleable( " + exTemp + " );");
			out.println('}');
		} else {
			// Two kinds of onException blocks require special generation: AnyException
			// and JavaObjectException. They require us to catch all Exceptions, then 
			// check if we got the kind of exception we were looking for. We generate 
			// them after the other onException blocks so they don't cause an 
			// "unreachable catch block" error.
			ExceptionBlock anyExBlock = null;
			ExceptionBlock javaOjbectExBlock = null;
			int specialBlocksCount = 0;
			for (ExceptionBlock exceptionBlock : blocks) {
				String sig = exceptionBlock.getException().getType().getTypeSignature();
				if (sig.equals("eglx.lang.AnyException")) {
					anyExBlock = exceptionBlock;
					specialBlocksCount++;
				} else if (sig.equals("eglx.java.JavaObjectException")) {
					javaOjbectExBlock = exceptionBlock;
					specialBlocksCount++;
				} else {
					ctx.invoke(genOnException, exceptionBlock, ctx, out);
				}
			}

			if (specialBlocksCount == 1) {
				ExceptionBlock block = anyExBlock != null ? anyExBlock : javaOjbectExBlock;
				ctx.invoke(genOneSpecialOnException, block, ctx, out);
			} else if (specialBlocksCount == 2) {
				ctx.invoke(genSpecialOnExceptions, anyExBlock, javaOjbectExBlock, ctx, out);
			}
		}
	}

	public void genStatementEnd(TryStatement stmt, Context ctx, TabbedWriter out) {
		// we don't want a semi-colon
	}
}
