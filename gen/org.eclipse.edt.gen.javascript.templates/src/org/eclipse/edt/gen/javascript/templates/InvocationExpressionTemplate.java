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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.ParameterKind;

public class InvocationExpressionTemplate extends JavascriptTemplate {

	public void genInvocation(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		// process the function invocation
		if (expr.getQualifier() != null) {
			ctx.gen(genExpression, expr.getQualifier(), ctx, out, args);
			out.print(".");
		}
		ctx.gen(genName, expr.getTarget(), ctx, out, args);
		out.print("(");
		int i = 0;
		FunctionParameter[] resetFunctionParms = null;
		for (FunctionParameter parameter : expr.getTarget().getParameters()) {
			Expression argExpr = expr.getArguments().get(i);
			if (CommonUtilities.isBoxedParameterType(parameter, ctx)) {
				if (resetFunctionParms == null)
					resetFunctionParms = new FunctionParameter[expr.getTarget().getParameters().size()];
				resetFunctionParms[i] = parameter;
			}
			if (parameter.getParameterKind() == ParameterKind.PARM_IN) {
				if (!parameter.isNullable() && argExpr.isNullable()) {
					out.print("(function(x){ return x != null ? ((x)) : ");
					ctx.gen(genDefaultValue, parameter.getType(), ctx, out, args);
					out.print("; })(");
					ctx.gen(genExpression, expr.getArguments().get(i), ctx, out);
					out.print(")");
				} else
					ctx.gen(genExpression, expr.getArguments().get(i), ctx, out);
			} else if (parameter.getParameterKind() == ParameterKind.PARM_INOUT)
				ctx.gen(genExpression, expr.getArguments().get(i), ctx, out);
			else
				ctx.gen(genDefaultValue, parameter.getType(), ctx, out, args);
			i++;
			if (i < expr.getArguments().size())
				out.print(", ");
		}
		if (resetFunctionParms != null) {
			out.print(", ");
			out.print("function(");
			for (int j = 0; j < resetFunctionParms.length; j++) {
				if (resetFunctionParms[j] != null) {
					out.print(resetFunctionParmName(resetFunctionParms[j]));
					if (j < resetFunctionParms.length - 1)
						out.print(", ");
				}
			}
			out.println(") {");
			for (int j = 0; j < resetFunctionParms.length; j++) {
				if (resetFunctionParms[j] != null && expr.getArguments().get(j) instanceof Name) {
					out.print("this.");
					ctx.gen(genName, ((Name) expr.getArguments().get(j)).getNamedElement(), ctx, out, args);
					out.print(" = ");
					out.print(resetFunctionParmName(resetFunctionParms[j]));
					out.println(";");
				}
			}
			out.println("}");
			out.print(", this");
		}
		out.print(")");
	}

	private String resetFunctionParmName(FunctionParameter parm) {
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
		name.append(parm.getName());
		return name.toString();
	}
}
