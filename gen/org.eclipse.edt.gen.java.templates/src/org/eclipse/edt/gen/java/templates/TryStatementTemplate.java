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

import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.Statement;
import org.eclipse.edt.mof.egl.TryStatement;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;

public class TryStatementTemplate extends StatementTemplate {

	public void genStatementBody(Statement stmt, Context ctx, TabbedWriter out, Object... args) {
		out.print("try ");
		ctx.gen(genStatement, ((TryStatement) stmt).getTryBlock(), ctx, out, args);
		for (int i = 0; i < ((TryStatement) stmt).getExceptionBlocks().size(); i++) {
			ExceptionBlock exceptionBlock = ((TryStatement) stmt).getExceptionBlocks().get(i);
			out.print("catch (" + ctx.getNativeImplementationMapping(exceptionBlock.getException().getType()) + " ");
			ctx.gen(genName, exceptionBlock.getException(), ctx, out, args);
			out.println(") {");
			for (int j = 0; j < exceptionBlock.getStatements().size(); j++) {
				ctx.gen(genStatement, exceptionBlock.getStatements().get(j), ctx, out, args);
			}
			out.println("}");
		}
	}

	public void genStatementEnd(TabbedWriter out, Object... args) {
	// we don't want a semi-colon
	}
}
