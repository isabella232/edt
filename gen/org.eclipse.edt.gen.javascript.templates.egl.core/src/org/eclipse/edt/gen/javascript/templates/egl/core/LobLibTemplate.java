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

public class LobLibTemplate extends NativeTypeTemplate {
	private static final String attachBlobToFile = "attachBlobToFile";
	private static final String getBlobLen = "getBlobLen";
	private static final String truncateBlob = "truncateBlob";
	private static final String loadBlobFromFile = "loadBlobFromFile";
	private static final String updateBlobToFile = "updateBlobToFile";
	private static final String freeBlob = "freeBlob";
	private static final String attachClobToFile = "attachClobToFile";
	private static final String getClobLen = "getClobLen";
	private static final String getSubStrFromClob = "getSubStrFromClob";
	private static final String getStrFromClob = "getStrFromClob";
	private static final String setClobFromStringAtPosition = "setClobFromStringAtPosition";
	private static final String setClobFromString = "setClobFromString";
	private static final String truncateClob = "truncateClob";
	private static final String loadClobFromFile = "loadClobFromFile";
	private static final String updateClobToFile = "updateClobToFile";
	private static final String freeClob = "freeClob";
	private static final String attachBlobToTempFile = "attachBlobToTempFile";
	private static final String attachClobToTempFile = "attachClobToTempFile";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(attachBlobToFile))
			genAttachBlobToFile(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getBlobLen))
			genGetBlobLen(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(truncateBlob))
			genTruncateBlob(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(loadBlobFromFile))
			genLoadBlobFromFile(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(updateBlobToFile))
			genUpdateBlobToFile(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(freeBlob))
			genFreeBlob(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(attachClobToFile))
			genAttachClobToFile(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getClobLen))
			genGetClobLen(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getSubStrFromClob))
			genGetSubStrFromClob(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getStrFromClob))
			genGetStrFromClob(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setClobFromStringAtPosition))
			genSetClobFromStringAtPosition(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setClobFromString))
			genSetClobFromString(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(truncateClob))
			genTruncateClob(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(loadClobFromFile))
			genLoadClobFromFile(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(updateClobToFile))
			genUpdateClobToFile(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(freeClob))
			genFreeClob(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(attachBlobToTempFile))
			genAttachBlobToTempFile(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(attachClobToTempFile))
			genAttachClobToTempFile(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genAttachBlobToFile(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetBlobLen(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genTruncateBlob(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genLoadBlobFromFile(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genUpdateBlobToFile(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFreeBlob(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genAttachClobToFile(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetClobLen(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetSubStrFromClob(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetStrFromClob(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetClobFromStringAtPosition(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetClobFromString(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genTruncateClob(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genLoadClobFromFile(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genUpdateClobToFile(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFreeClob(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genAttachBlobToTempFile(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genAttachClobToTempFile(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
