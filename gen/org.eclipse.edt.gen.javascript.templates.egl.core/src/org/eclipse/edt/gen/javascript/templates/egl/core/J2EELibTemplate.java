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
package org.eclipse.edt.gen.javascript.templates.egl.core;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.NativeTypeTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.InvocationExpression;

public class J2EELibTemplate extends NativeTypeTemplate {
	private static final String clearEGLSessionAttrs = "clearEGLSessionAttrs";
	private static final String clearApplicationAttr = "clearApplicationAttr";
	private static final String clearRequestAttr = "clearRequestAttr";
	private static final String clearSessionAttr = "clearSessionAttr";
	private static final String getAuthenticationType = "getAuthenticationType";
	private static final String getQueryParameter = "getQueryParameter";
	private static final String getRemoteUser = "getRemoteUser";
	private static final String getRequestAttr = "getRequestAttr";
	private static final String getSessionAttr = "getSessionAttr";
	private static final String getApplicationAttr = "getApplicationAttr";
	private static final String isUserInRole = "isUserInRole";
	private static final String setApplicationAttr = "setApplicationAttr";
	private static final String setRequestAttr = "setRequestAttr";
	private static final String setSessionAttr = "setSessionAttr";
	private static final String getContext = "getContext";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(clearEGLSessionAttrs))
			genClearEGLSessionAttrs(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(clearApplicationAttr))
			genClearApplicationAttr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(clearRequestAttr))
			genClearRequestAttr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(clearSessionAttr))
			genClearSessionAttr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getAuthenticationType))
			genGetAuthenticationType(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getQueryParameter))
			genGetQueryParameter(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getRemoteUser))
			genGetRemoteUser(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getRequestAttr))
			genGetRequestAttr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getSessionAttr))
			genGetSessionAttr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getApplicationAttr))
			genGetApplicationAttr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(isUserInRole))
			genIsUserInRole(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setApplicationAttr))
			genSetApplicationAttr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setRequestAttr))
			genSetRequestAttr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setSessionAttr))
			genSetSessionAttr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getContext))
			genGetContext(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genClearEGLSessionAttrs(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genClearApplicationAttr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genClearRequestAttr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genClearSessionAttr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetAuthenticationType(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetQueryParameter(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetRemoteUser(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetRequestAttr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetSessionAttr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetApplicationAttr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genIsUserInRole(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetApplicationAttr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetRequestAttr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetSessionAttr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetContext(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
