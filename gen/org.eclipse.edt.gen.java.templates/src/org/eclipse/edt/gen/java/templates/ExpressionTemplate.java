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

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.Type;

public class ExpressionTemplate extends JavaTemplate {

	public void genExpression(Expression expr, Context ctx, TabbedWriter out, Type asType) {
		ctx.invoke(genExpression, expr, ctx, out);
	}

	public void genExpression(Expression expr, Context ctx, TabbedWriter out, Expression parameter) {
		// if the parameter is nullable we need to wrap with a checkNullable
		if (parameter.isNullable() && !CommonUtilities.isBoxedOutputTemp(expr, ctx)) {
			out.print("org.eclipse.edt.javart.util.JavartUtil.checkNullable(");
			ctx.invoke(genExpression, expr, ctx, out);
			out.print(")");
		} else
			ctx.invoke(genExpression, expr, ctx, out);
	}

	public void genExpression(Expression expr, Context ctx, TabbedWriter out, FunctionParameter parameter) {
		// if the parameter is non-nullable but the argument is nullable, we have a special case
		if (!parameter.isNullable() && expr.isNullable() && !CommonUtilities.isBoxedOutputTemp(expr, ctx)) {
			out.print("org.eclipse.edt.javart.util.JavartUtil.checkNullable(");
			// if this is the null literal, we need to cast this to prevent the javagen ambiguous errors
			if (expr instanceof NullLiteral) {
				out.print("(");
				ctx.invoke(genRuntimeTypeName, parameter.getType(), ctx, out, TypeNameKind.JavaObject);
				out.print(") ");
			}
			ctx.invoke(genExpression, expr, ctx, out);
			out.print(")");
		} else
			ctx.invoke(genExpression, expr, ctx, out);
	}

	public void genExpression(Expression expr, Context ctx, TabbedWriter out, FunctionMember parameter) {
		// if the parameter is non-nullable but the argument is nullable, we have a special case
		if (!parameter.isNullable() && expr.isNullable() && !CommonUtilities.isBoxedOutputTemp(expr, ctx)) {
			out.print("org.eclipse.edt.javart.util.JavartUtil.checkNullable(");
			// if this is the null literal, we need to cast this to prevent the javagen ambiguous errors
			if (expr instanceof NullLiteral) {
				out.print("(");
				ctx.invoke(genRuntimeTypeName, parameter.getType(), ctx, out, TypeNameKind.JavaObject);
				out.print(") ");
			}
			ctx.invoke(genExpression, expr, ctx, out);
			out.print(")");
		} else
			ctx.invoke(genExpression, expr, ctx, out);
	}

	public void genExpression(Expression expr, Context ctx, TabbedWriter out) {
		String[] details = new String[] { expr.getEClass().getETypeSignature() };
		EGLMessage message = EGLMessage.createEGLMessage(ctx.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
			Constants.EGLMESSAGE_MISSING_TEMPLATE_FOR_OBJECT, expr, details,
			org.eclipse.edt.gen.CommonUtilities.includeEndOffset(expr.getAnnotation(IEGLConstants.EGL_LOCATION), ctx));
		ctx.getMessageRequestor().addMessage(message);
	}
}
