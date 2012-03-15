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

import java.util.List;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.MemberName;

public class InvocationExpressionTemplate extends JavaScriptTemplate {

	public void genInvocation(InvocationExpression expr, Context ctx, TabbedWriter out) {
		// then process the function invocation
		if (expr.getQualifier() != null) {
			boolean nullCheck = (expr.getQualifier().isNullable() && expr.getQualifier() instanceof MemberName);
			if (nullCheck) {
				out.print( "egl.checkNull(" );
			}
			ctx.invoke(genExpression, expr.getQualifier(), ctx, out);
			if (nullCheck) {
				out.print(")");
			}
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
				Expression argExpr = expr.getArguments().get(i);
				// if the parameter is non-nullable but the argument is nullable, we have a special case
				if (!expr.getTarget().getParameters().get(i).isNullable() && argExpr.isNullable()) {
					out.print("egl.checkNull(");
					ctx.invoke(genExpression, argExpr, ctx, out);
					out.print(")");
				} else{
					if(expr.getTarget().getParameters().get(i).isConst()){
						ctx.putAttribute(argExpr, "function parameter is const in", Boolean.TRUE);
					}
					ctx.invoke(genExpression, argExpr, ctx, out);
					if(expr.getTarget().getParameters().get(i).isConst()){
						List<Annotation> list = (List<Annotation>)ctx.get(argExpr);
						list.remove("function parameter is const in");
					}
				}
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
		name.append(parm.getName());
		return name.toString();
	}
}
