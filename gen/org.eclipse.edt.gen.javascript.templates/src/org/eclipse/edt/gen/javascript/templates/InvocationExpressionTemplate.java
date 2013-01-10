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
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.InvocationExpression;

public class InvocationExpressionTemplate extends JavaScriptTemplate {

	public void genInvocation(InvocationExpression expr, Context ctx, TabbedWriter out) {
		// then process the function invocation
		if (expr.getQualifier() != null) {
			ctx.invoke(genExpression, expr.getQualifier(), ctx, out, expr.getQualifier());
			out.print(".");
		} else {
			ctx.invoke(genQualifier, expr.getTarget(), ctx, out);
		}
		ctx.invoke(genName, expr.getTarget(), ctx, out);
		out.print("(");
		ctx.invoke(genInvocationArguments, expr, ctx, out);
		out.print(")");
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

	protected String resetFunctionParmName(FunctionParameter parm) {
		StringBuilder name = new StringBuilder();
		switch (parm.getParameterKind()) {
			case PARM_IN:
				name.append("in");
				break;
			case PARM_OUT:
				name.append("out");
				break;
			case PARM_INOUT:
				name.append("inout");
				break;
		}
		name.append("$");
		name.append(parm.getCaseSensitiveName());
		return name.toString();
	}
}
