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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.InvocationExpression;

public class InvocationExpressionTemplate extends JavaTemplate {

	public void genInvocation(InvocationExpression expr, Context ctx, TabbedWriter out) {
		// then process the function invocation
		if (expr.getQualifier() != null) {
			ctx.invoke(genExpression, expr.getQualifier(), ctx, out, expr.getQualifier());
			out.print(".");
		}
		ctx.invoke(genName, expr.getTarget(), ctx, out);
		out.print("(");
		ctx.invoke(genInvocationArguments, expr, ctx, out);
		out.print(")");
	}

	public void genInvocationNonstaticArgument(InvocationExpression expr, Context ctx, TabbedWriter out) {
		// for static functions, we don't want to write the qualifier as the 1st argument
		if (expr.getTarget() instanceof Function && ((Function) expr.getTarget()).isStatic())
			return;
		ctx.invoke(genExpression, expr.getQualifier(), ctx, out, expr.getQualifier());
		if (expr.getArguments() != null && expr.getArguments().size() > 0)
			out.print(", ");
	}

	public void genInvocationArguments(InvocationExpression expr, Context ctx, TabbedWriter out) {
		// check to see if we are trying to pass a nullable to a non-nullable
		if (expr.getArguments() != null) {
			for (int i = 0; i < expr.getArguments().size(); i++) {
				ctx.invoke(genExpression, expr.getArguments().get(i), ctx, out, expr.getTarget().getParameters().get(i));
				if (i < expr.getArguments().size() - 1)
					out.print(", ");
			}
		}
	}
}
