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
package org.eclipse.edt.gen.javascript.templates.eglx.lang;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.Type;

public class TimeTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out) {
		out.print(Constants.JSRT_DTTMLIB_PKG + "currentTime()");
	}

	public void genContainerBasedNewExpression(EGLClass type, Context ctx, TabbedWriter out, NewExpression arg) throws GenerationException {
		out.print("new ");
		ctx.invoke(genRuntimeTypeName, arg.getType(), ctx, out, TypeNameKind.JavascriptImplementation);
		out.print("(");
		if (arg.getArguments() != null && arg.getArguments().size() > 0) {
			String delim = "";
			for (Expression argument : arg.getArguments()) {
				out.print(delim);
				ctx.invoke(genExpression, argument, ctx, out);
				delim = ", ";
			}
		} else
			ctx.invoke(genConstructorOptions, arg.getType(), ctx, out);
		out.print(")");
	}

	public void genContainerBasedInvocation(EGLClass type, Context ctx, TabbedWriter out, InvocationExpression expr) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".");
		ctx.invoke(genName, expr.getTarget(), ctx, out);
		out.print("(");
		ctx.invoke(genExpression, expr.getQualifier(), ctx, out, expr.getQualifier());
		if (expr.getArguments() != null && expr.getArguments().size() > 0)
			out.print(", ");
		ctx.invoke(genInvocationArguments, expr, ctx, out);
		out.print(")");
	}

	public void genSignature(EGLClass type, Context ctx, TabbedWriter out) {
		String signature = "L;";
		out.print(signature);
	}

	public void genBinaryExpression(EGLClass type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// for time type, always use the runtime
		out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
		out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
		out.print("(");
		ctx.invoke(genExpression, arg.getLHS(), ctx, out, arg.getOperation().getParameters().get(0));
		out.print(", ");
		ctx.invoke(genExpression, arg.getRHS(), ctx, out, arg.getOperation().getParameters().get(1));
		out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
	}

	public void genServiceInvocationInParam(EGLClass type, Context ctx, TabbedWriter out, Expression arg) {
		out.print("egl.eglx.lang.EAny.fromEAny(");
		ctx.invoke(genExpression, arg, ctx, out);
		out.print(", \"");
		ctx.invoke(genSignature, type, ctx, out);
		out.print("\")");
	}
}
