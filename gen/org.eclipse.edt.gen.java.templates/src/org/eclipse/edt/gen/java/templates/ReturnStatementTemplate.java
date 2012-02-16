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
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.ReturnStatement;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class ReturnStatementTemplate extends JavaTemplate {

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
			Expression retExpr = IRUtils.makeExprCompatibleToType(stmt.getExpression(), ((FunctionMember) stmt.getContainer()).getType());
			// check to see if we are trying to pass a nullable to a non-nullable
			if (!((FunctionMember) stmt.getContainer()).isNullable() && stmt.getExpression().isNullable()
				&& !CommonUtilities.isBoxedOutputTemp(stmt.getExpression(), ctx)) {
				out.print("org.eclipse.edt.javart.util.JavartUtil.checkNullable(");
				// if this is the null literal, we need to cast this to prevent the javagen ambiguous errors
				if (stmt.getExpression() instanceof NullLiteral) {
					out.print("(");
					ctx.invoke(genRuntimeTypeName, ((FunctionMember) stmt.getContainer()).getType(), ctx, out, TypeNameKind.JavaObject);
					out.print(") ");
				}
				ctx.invoke(genExpression, retExpr, ctx, out);
				out.print(")");
			} else
				ctx.invoke(genExpression, retExpr, ctx, out);
		}
	}
}
