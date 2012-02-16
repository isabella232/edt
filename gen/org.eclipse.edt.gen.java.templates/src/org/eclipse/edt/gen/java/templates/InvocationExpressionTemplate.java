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
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.NullLiteral;

public class InvocationExpressionTemplate extends JavaTemplate {

	public void genInvocation(InvocationExpression expr, Context ctx, TabbedWriter out) {
		// then process the function invocation
		if (expr.getQualifier() != null) {
			ctx.invoke(genExpression, expr.getQualifier(), ctx, out);
			out.print(".");
		}
		ctx.invoke(genName, expr.getTarget(), ctx, out);
		out.print("(");
		// check to see if we are trying to pass a nullable to a non-nullable
		if (expr.getArguments() != null) {
			for (int i = 0; i < expr.getArguments().size(); i++) {
				Expression argExpr = expr.getArguments().get(i);
				// if the parameter is non-nullable but the argument is nullable, we have a special case
				if (!expr.getTarget().getParameters().get(i).isNullable() && argExpr.isNullable() && !CommonUtilities.isBoxedOutputTemp(argExpr, ctx)) {
					out.print("org.eclipse.edt.javart.util.JavartUtil.checkNullable(");
					// if this is the null literal, we need to cast this to prevent the javagen ambiguous errors
					if (argExpr instanceof NullLiteral) {
						out.print("(");
						ctx.invoke(genRuntimeTypeName, expr.getTarget().getParameters().get(i).getType(), ctx, out, TypeNameKind.JavaObject);
						out.print(") ");
					}
					ctx.invoke(genExpression, argExpr, ctx, out);
					out.print(")");
				} else
					ctx.invoke(genExpression, argExpr, ctx, out);
				if (i < expr.getArguments().size() - 1)
					out.print(", ");
			}
		}
		out.print(")");
	}
}
