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

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ExceptionBlock;
import org.eclipse.edt.mof.egl.TryStatement;

public class TryStatementTemplate extends JavaTemplate {

	public void genStatementBody(TryStatement stmt, Context ctx, TabbedWriter out) {
		out.print("try ");
		ctx.invoke(genStatement, stmt.getTryBlock(), ctx, out);
		for (ExceptionBlock exceptionBlock : stmt.getExceptionBlocks()) {
			genException(exceptionBlock, ctx, out);
		}
	}

	public void genException(ExceptionBlock exceptionBlock, Context ctx, TabbedWriter out) {
		CommonUtilities.generateSmapExtension(exceptionBlock.getException(), ctx);
		out.print("catch (" + ctx.getNativeImplementationMapping(exceptionBlock.getException().getType()) + " ");
		ctx.invoke(genName, exceptionBlock.getException(), ctx, out);
		out.print(") ");
		ctx.invoke(genStatement, exceptionBlock, ctx, out);
	}

	public void genStatementEnd(TryStatement stmt, Context ctx, TabbedWriter out) {
		// we don't want a semi-colon
	}
}
