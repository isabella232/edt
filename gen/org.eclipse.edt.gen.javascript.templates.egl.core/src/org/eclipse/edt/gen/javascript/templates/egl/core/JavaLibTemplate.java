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

public class JavaLibTemplate extends NativeTypeTemplate {
	private static final String invoke = "invoke";
	private static final String store = "store";
	private static final String storeNew = "storeNew";
	private static final String getField = "getField";
	private static final String setField = "setField";
	private static final String storeField = "storeField";
	private static final String qualifiedTypeName = "qualifiedTypeName";
	private static final String storeCopy = "storeCopy";
	private static final String remove = "remove";
	private static final String removeAll = "removeAll";
	private static final String isNull = "isNull";
	private static final String isObjId = "isObjId";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(invoke))
			genInvoke(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(store))
			genStore(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(storeNew))
			genStoreNew(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getField))
			genGetField(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setField))
			genSetField(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(storeField))
			genStoreField(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(qualifiedTypeName))
			genQualifiedTypeName(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(storeCopy))
			genStoreCopy(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(remove))
			genRemove(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(removeAll))
			genRemoveAll(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(isNull))
			genIsNull(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(isObjId))
			genIsObjId(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genInvoke(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genStore(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genStoreNew(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetField(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetField(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genStoreField(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genQualifiedTypeName(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genStoreCopy(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genRemove(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genRemoveAll(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genIsNull(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genIsObjId(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
