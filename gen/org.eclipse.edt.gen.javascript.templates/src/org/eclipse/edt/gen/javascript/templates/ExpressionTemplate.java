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

import java.util.List;

import org.eclipse.edt.compiler.core.IEGLConstants;
import org.eclipse.edt.gen.EGLMessages.EGLMessage;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.NullLiteral;
import org.eclipse.edt.mof.egl.Type;

public class ExpressionTemplate extends JavaScriptTemplate {

	public void genTypeBasedExpression(Expression expr, Context ctx, TabbedWriter out, Type arg) {
		ctx.invoke(genExpression, expr, ctx, out);
	}
	
	public void genExpression(Expression expr, Context ctx, TabbedWriter out, Expression parameter) {
		// if the parameter is nullable we need to wrap with a checkNullable
		if (parameter.isNullable()) {
			out.print("egl.checkNull(");
			ctx.invoke(genExpression, expr, ctx, out);
			out.print(")");
		} else
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
		} else {
			if (parameter.isConst()) {
				ctx.putAttribute(expr, "function parameter is const in", Boolean.TRUE);
				ctx.invoke(genExpression, expr, ctx, out);
				((List<Annotation>)ctx.get(expr)).remove("function parameter is const in");
			} else
				ctx.invoke(genExpression, expr, ctx, out);
		}
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

	public void genExpression(Expression expr, Context ctx, TabbedWriter out) {
		String[] details = new String[] { expr.getEClass().getETypeSignature() };
		EGLMessage message = EGLMessage.createEGLMessage(ctx.getMessageMapping(), EGLMessage.EGL_ERROR_MESSAGE,
			Constants.EGLMESSAGE_MISSING_TEMPLATE_FOR_OBJECT, expr, details,
			org.eclipse.edt.gen.CommonUtilities.includeEndOffset(expr.getAnnotation(IEGLConstants.EGL_LOCATION), ctx));
		ctx.getMessageRequestor().addMessage(message);
	}
}
