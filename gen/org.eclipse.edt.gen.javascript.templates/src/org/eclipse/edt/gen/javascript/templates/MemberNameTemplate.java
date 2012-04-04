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

import org.eclipse.edt.gen.CommonUtilities;
import org.eclipse.edt.gen.EglContext;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class MemberNameTemplate extends JavaScriptTemplate {

	public void genAssignment(MemberName expr, Context ctx, TabbedWriter out, Expression arg1, String arg2) {
		// check to see if we are copying boxed function parameters
		if (expr.getMember() instanceof FunctionParameter
			&& isBoxedParameterType((FunctionParameter) expr.getMember(), ctx)) {
			ctx.invoke(genAccessor, expr.getMember(), ctx, out);
			out.print(".ezeCopy(");
			// if we are doing some type of complex assignment, we need to place that in the argument
			if (arg2.length() > 3 && arg2.indexOf("=") > 1) {
				ctx.invoke(genAccessor, expr.getMember(), ctx, out);
				out.print(".ezeUnbox()");
				out.print(arg2.substring(0, arg2.indexOf("=")) + arg2.substring(arg2.indexOf("=") + 1));
			}
			ctx.invoke(genExpression, arg1, ctx, out);
			// check to see if we are unboxing RHS temporary variables (inout and out types only)
			if (arg1 instanceof MemberName
				&& ctx.getAttribute(((MemberName) arg1).getMember(), org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != null
					&& ctx.getAttribute(((MemberName) arg1).getMember(), org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != ParameterKind.PARM_IN)
				out.print(".ezeUnbox()");
			out.print(")");
			// check to see if we are copying LHS boxed temporary variables (inout and out types only)
		} else if (ctx.getAttribute(expr.getMember(), org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != null
				&& ctx.getAttribute(expr.getMember(), org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != ParameterKind.PARM_IN) {
			ctx.invoke(genExpression, (Expression) expr, ctx, out);
			out.print(arg2);
			out.print(Constants.JSRT_EGL_NAMESPACE + ctx.getNativeMapping("eglx.lang.EAny") + ".ezeWrap(");
			ctx.invoke(genExpression, arg1, ctx, out);
			// check to see if we are unboxing RHS temporary variables (inout and out types only)
			if (arg1 instanceof MemberName
				&& ctx.getAttribute(((MemberName) arg1).getMember(), org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != null
					&& ctx.getAttribute(((MemberName) arg1).getMember(), org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != ParameterKind.PARM_IN)
				out.print(".ezeUnbox()");
			out.print(")");
			// check to see if we are unboxing RHS temporary variables (inout and out types only)
		} else if (arg1 instanceof MemberName
			&& ctx.getAttribute(((MemberName) arg1).getMember(), org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != null
				&& ctx.getAttribute(((MemberName) arg1).getMember(), org.eclipse.edt.gen.Constants.SubKey_functionArgumentTemporaryVariable) != ParameterKind.PARM_IN) {
			ctx.invoke(genExpression, (Expression) expr, ctx, out);
			out.print(arg2);
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(".ezeUnbox()");
		} else
			ctx.invokeSuper(this, genAssignment, expr, ctx, out, arg1, arg2);
	}

	public void genExpression(MemberName expr, Context ctx, TabbedWriter out) {
		Member member = expr.getMember();
		if (member != null && member instanceof Function) {
			ctx.invoke(genCallbackAccesor, expr, ctx, out, null);
		} else if (member != null && member.getContainer() != null && member.getContainer() instanceof Type) {
			ctx.invoke(genContainerBasedMemberName, (Type) member.getContainer(), ctx, out, expr, member);
		} else {
			genMemberName(expr, ctx, out);
		}
	}

	public Function getCallbackFunction(MemberName expr, Context ctx) {
		return (Function) ctx.invoke(getCallbackFunction, expr.getMember(), ctx);
	}

	public void genCallbackAccesor(MemberName expr, Context ctx, TabbedWriter out, Member arg1) {
		out.print("new egl.egl.jsrt.Delegate(this");
		if ( arg1 != null ) {
			out.print(".");
			ctx.invoke(genName, expr.getMember(), ctx, out);
			out.print(", this.");
			ctx.invoke(genName, expr.getMember(), ctx, out);
			out.print(".");
			ctx.invoke(genName, arg1, ctx, out);
		} else {
			out.print(", ");
			ctx.invoke(genPartName, expr.getMember().getContainer(), ctx, out);
			out.print(".prototype.");
			ctx.invoke(genName, expr.getMember(), ctx, out);
		}
		out.print(")");
	}

	public void genMemberName(MemberName expr, Context ctx, TabbedWriter out) {
		/*
		 * Determine whether we need an implicit "this." as required by JavaScript for when accessing local fields within
		 * their containing part
		 */
		if ((expr.getQualifier() == null) && (expr.getMember() instanceof Field)) {
			ctx.invoke(genQualifier, (Field) expr.getMember(), ctx, out);
		}

		boolean unbox = (expr.getMember() instanceof FunctionParameter
			&& isBoxedParameterType((FunctionParameter) expr.getMember(), ctx));
		unboxStart(unbox, out);
		ctx.invoke(genAccessor, expr.getMember(), ctx, out);
		unboxEnd(unbox, out);
	}

	private static boolean isBoxedParameterType(FunctionParameter parameter, EglContext ctx)
	{
		/* TODO sbg If the parm type is ANY, then we want to also treat as boxed; this check may get moved
		 * to CommonUtilities.isBoxedParameterType if it makes sense for all generators....
		 */
		return (CommonUtilities.isBoxedParameterType(parameter, ctx)); // || (parameter.getType() == TypeUtils.Type_ANY));
	}
}
