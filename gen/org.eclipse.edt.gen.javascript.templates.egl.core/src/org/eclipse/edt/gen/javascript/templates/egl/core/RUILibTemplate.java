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

public class RUILibTemplate extends NativeTypeTemplate {
	private static final String sort = "sort";
	private static final String setTextSelectionEnabled = "setTextSelectionEnabled";
	private static final String getTextSelectionEnabled = "getTextSelectionEnabled";
	private static final String getUserAgent = "getUserAgent";
	private static final String setTitle = "setTitle";
	private static final String getTitle = "getTitle";
	private static final String setTheme = "setTheme";
	private static final String getTheme = "getTheme";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(sort))
			genSort(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setTextSelectionEnabled))
			genSetTextSelectionEnabled(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getTextSelectionEnabled))
			genGetTextSelectionEnabled(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getUserAgent))
			genGetUserAgent(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setTitle))
			genSetTitle(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getTitle))
			genGetTitle(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setTheme))
			genSetTheme(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getTheme))
			genGetTheme(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genSort(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetTextSelectionEnabled(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetTextSelectionEnabled(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetUserAgent(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetTitle(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetTitle(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetTheme(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetTheme(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
