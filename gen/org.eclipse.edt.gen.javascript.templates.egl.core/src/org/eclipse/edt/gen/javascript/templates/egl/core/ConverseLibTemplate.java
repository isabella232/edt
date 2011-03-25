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

public class ConverseLibTemplate extends NativeTypeTemplate {
	private static final String clearScreen = "clearScreen";
	private static final String displayMsgNum = "displayMsgNum";
	private static final String fieldInputLength = "fieldInputLength";
	private static final String pageEject = "pageEject";
	private static final String validationFailed = "validationFailed";
	private static final String setCursorPosition = "setCursorPosition";
	private static final String getCursorLine = "getCursorLine";
	private static final String getCursorColumn = "getCursorColumn";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(clearScreen))
			genClearScreen(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(displayMsgNum))
			genDisplayMsgNum(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(fieldInputLength))
			genFieldInputLength(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(pageEject))
			genPageEject(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(validationFailed))
			genValidationFailed(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setCursorPosition))
			genSetCursorPosition(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getCursorLine))
			genGetCursorLine(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getCursorColumn))
			genGetCursorColumn(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genClearScreen(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDisplayMsgNum(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFieldInputLength(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genPageEject(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genValidationFailed(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetCursorPosition(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetCursorLine(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetCursorColumn(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
