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
import org.eclipse.edt.mof.egl.InvocationExpression;

public class InvocationExpressionTemplate extends JavaTemplate {

	public void genInvocation(InvocationExpression expr, Context ctx, TabbedWriter out) {
		// then process the function invocation
		if (expr.getQualifier() != null) {
			ctx.invoke(genExpression, expr.getQualifier(), ctx, out);
			out.print(".");
		}
		ctx.invoke(genName, expr.getTarget(), ctx, out);
		out.print("(");
		ctx.foreach(expr.getArguments(), ',', genExpression, ctx, out);
		out.print(")");
	}

	public void genRununitInvocation(InvocationExpression expr, Context ctx, TabbedWriter out) {
		// then process the function invocation
		if (expr.getQualifier() != null) {
			ctx.invoke(genExpression, expr.getQualifier(), ctx, out);
			out.print(".");
		}
		ctx.invoke(genName, expr.getTarget(), ctx, out);
		out.print("(");
		out.print("_runUnit(), ");
		ctx.foreach(expr.getArguments(), ',', genExpression, ctx, out);
		out.print(")");
	}
}
