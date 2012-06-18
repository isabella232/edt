/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate.TypeNameKind;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.Type;

public class ExpressionTemplate extends JavaScriptTemplate {

	public void genExpression(Expression expr, Context ctx, TabbedWriter out) {
		ctx.invoke(genExpression, expr, ctx, out);
	}
	
	public void genTypeBasedExpression(Expression expr, Context ctx, TabbedWriter out, Type arg) {
		ctx.invoke(genExpression, expr, ctx, out);
	}
	
	public void genExpression(Expression expr, Context ctx, TabbedWriter out, FunctionParameter parameter) {
		// if the parameter is non-nullable but the argument is nullable, we have a special case
		if (!parameter.isNullable() && expr.isNullable()) {
			out.print("egl.checkNull(");
			// if this is the null literal, we need to cast this to prevent the javagen ambiguous errors
			if (expr instanceof NullLiteral) {
				out.print("(");
				ctx.invoke(genRuntimeTypeName, parameter.getType(), ctx, out, TypeNameKind.JavascriptImplementation);
				out.print(") ");
			}
			ctx.invoke(genExpression, expr, ctx, out);
			out.print(")");
		} else
			ctx.invoke(genExpression, expr, ctx, out);
	}

	public void genExpression(Expression expr, Context ctx, TabbedWriter out, FunctionMember parameter) {
		// if the parameter is non-nullable but the argument is nullable, we have a special case
		if (!parameter.isNullable() && expr.isNullable()) {
			out.print("egl.checkNull(");
			// if this is the null literal, we need to cast this to prevent the javagen ambiguous errors
			if (expr instanceof NullLiteral) {
				out.print("(");
				ctx.invoke(genRuntimeTypeName, parameter.getType(), ctx, out, TypeNameKind.JavascriptImplementation);
				out.print(") ");
			}
			ctx.invoke(genExpression, expr, ctx, out);
			out.print(")");
		} else
			ctx.invoke(genExpression, expr, ctx, out);
	}

}
