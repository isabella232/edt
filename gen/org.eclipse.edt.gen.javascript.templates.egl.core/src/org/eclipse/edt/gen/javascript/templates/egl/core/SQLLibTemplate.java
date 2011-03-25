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

public class SQLLibTemplate extends NativeTypeTemplate {
	private static final String beginDatabaseTransaction = "beginDatabaseTransaction";
	private static final String connect = "connect";
	private static final String constructQuery = "constructQuery";
	private static final String defineDatabaseAlias = "defineDatabaseAlias";
	private static final String disconnect = "disconnect";
	private static final String disconnectAll = "disconnectAll";
	private static final String loadTable = "loadTable";
	private static final String queryCurrentDatabase = "queryCurrentDatabase";
	private static final String setCurrentDatabase = "setCurrentDatabase";
	private static final String unloadTable = "unloadTable";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(beginDatabaseTransaction))
			genBeginDatabaseTransaction(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(connect))
			genConnect(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(constructQuery))
			genConstructQuery(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(defineDatabaseAlias))
			genDefineDatabaseAlias(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(disconnect))
			genDisconnect(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(disconnectAll))
			genDisconnectAll(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(loadTable))
			genLoadTable(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(queryCurrentDatabase))
			genQueryCurrentDatabase(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setCurrentDatabase))
			genSetCurrentDatabase(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(unloadTable))
			genUnloadTable(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genBeginDatabaseTransaction(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConnect(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConstructQuery(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDefineDatabaseAlias(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDisconnect(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDisconnectAll(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genLoadTable(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genQueryCurrentDatabase(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetCurrentDatabase(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genUnloadTable(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
