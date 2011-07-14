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

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;

public class MemberNameTemplate extends JavaScriptTemplate {

	public void genAssignment(MemberName expr, Context ctx, TabbedWriter out, Expression arg) {
		// check to see if we are copying boxed function parameters
		if (expr.getMember() instanceof FunctionParameter
			&& org.eclipse.edt.gen.CommonUtilities.isBoxedParameterType((FunctionParameter) expr.getMember(), ctx)) {
			ctx.invoke(genAccessor, expr.getMember(), ctx, out);
			out.print(".ezeCopy(");
			ctx.invoke(genExpression, arg, ctx, out);
			out.print(")");
			// check to see if we are copying LHS boxed temporary variables (inout and out types only)
		} else if (ctx.getAttribute(expr.getMember(), org.eclipse.edt.gen.Constants.Annotation_functionArgumentTemporaryVariable) != null
			&& ctx.getAttribute(expr.getMember(), org.eclipse.edt.gen.Constants.Annotation_functionArgumentTemporaryVariable) != ParameterKind.PARM_IN) {
			ctx.invoke(genExpression, (Expression) expr, ctx, out);
			out.print(" = ");
			out.print(Constants.JSRT_EGL_NAMESPACE + ctx.getNativeMapping("egl.lang.AnyObject") + ".ezeWrap(");
			ctx.invoke(genExpression, arg, ctx, out);
			out.print(")");
			// check to see if we are unboxing RHS temporary variables (inout and out types only)
		} else if (arg instanceof MemberName
			&& ctx.getAttribute(((MemberName) arg).getMember(), org.eclipse.edt.gen.Constants.Annotation_functionArgumentTemporaryVariable) != null
			&& ctx.getAttribute(((MemberName) arg).getMember(), org.eclipse.edt.gen.Constants.Annotation_functionArgumentTemporaryVariable) != ParameterKind.PARM_IN) {
			ctx.invoke(genExpression, (Expression) expr, ctx, out);
			out.print(" = ");
			ctx.invoke(genExpression, arg, ctx, out);
			out.print(".ezeUnbox()");
		} else
			ctx.invokeSuper(this, genAssignment, expr, ctx, out, arg);
	}

	public void genExpression(MemberName expr, Context ctx, TabbedWriter out) {
		Member member = expr.getMember();
		if (member != null && member.getContainer() != null && member.getContainer() instanceof Type)
			ctx.invoke(genContainerBasedMemberName, (Type) member.getContainer(), ctx, out, expr, member);
		else
			genMemberName(expr, ctx, out);
	}

	public Function getCallbackFunction(MemberName expr, Context ctx) {
		return (Function)ctx.invoke(getCallbackFunction, expr.getMember(), ctx);
	}
	
	public void genCallbackAccesor(MemberName expr, Context ctx, TabbedWriter out) {
		ctx.invoke(genName, expr.getMember(), ctx, out);
	}
	
	public void genMemberName(MemberName expr, Context ctx, TabbedWriter out) {
		ctx.invoke(genAccessor, expr.getMember(), ctx, out);
		if (expr.getMember() instanceof FunctionParameter
			&& org.eclipse.edt.gen.CommonUtilities.isBoxedParameterType((FunctionParameter) expr.getMember(), ctx)) {
			out.print(".ezeUnbox()");
		}
	}
}
