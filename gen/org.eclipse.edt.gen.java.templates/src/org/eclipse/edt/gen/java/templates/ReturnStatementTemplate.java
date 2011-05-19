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
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.ReturnStatement;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class ReturnStatementTemplate extends JavaTemplate {

	public void genStatementBody(ReturnStatement stmt, Context ctx, TabbedWriter out, Object... args) {
		out.print("return ");
		Expression expr = stmt.getExpression();
		if (expr != null) {
			expr = IRUtils.makeExprCompatibleToType(expr, ((FunctionMember)stmt.getContainer()).getType());
			ctx.gen(genExpression, expr, ctx, out, args);
		}
	}
}
