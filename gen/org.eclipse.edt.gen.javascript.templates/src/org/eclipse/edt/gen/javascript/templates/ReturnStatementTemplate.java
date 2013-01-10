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
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.ReturnStatement;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class ReturnStatementTemplate extends JavaScriptTemplate {
	
	public void genStatementBody(ReturnStatement stmt, Context ctx, TabbedWriter out) {
		if (stmt.getExpression() != null)
			ctx.invoke(genReturnStatement, stmt.getExpression().getType(), ctx, out, stmt);
		else
			out.print("return");
	}

	public void genReturnStatement(ReturnStatement stmt, Context ctx, TabbedWriter out) {
		out.print("return");
		if (stmt.getExpression() != null) {
			out.print(" ");
			ctx.invoke(genExpression, IRUtils.makeExprCompatibleToType(stmt.getExpression(), ((FunctionMember) stmt.getContainer()).getType()), ctx, out, (FunctionMember) stmt.getContainer());
		}
	}

}
