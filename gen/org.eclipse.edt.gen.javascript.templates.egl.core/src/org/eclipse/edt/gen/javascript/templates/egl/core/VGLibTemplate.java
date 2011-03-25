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

public class VGLibTemplate extends NativeTypeTemplate {
	private static final String compareBytes = "compareBytes";
	private static final String compareNum = "compareNum";
	private static final String compareStr = "compareStr";
	private static final String concatenate = "concatenate";
	private static final String concatenateBytes = "concatenateBytes";
	private static final String concatenateWithSeparator = "concatenateWithSeparator";
	private static final String connectionService = "connectionService";
	private static final String copyBytes = "copyBytes";
	private static final String copyStr = "copyStr";
	private static final String findStr = "findStr";
	private static final String floatingDifference = "floatingDifference";
	private static final String floatingMod = "floatingMod";
	private static final String floatingProduct = "floatingProduct";
	private static final String floatingQuotient = "floatingQuotient";
	private static final String floatingSum = "floatingSum";
	private static final String getVAGSysType = "getVAGSysType";
	private static final String setSubStr = "setSubStr";
	private static final String startTransaction = "startTransaction";
	private static final String VGTDLI = "VGTDLI";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(compareBytes))
			genCompareBytes(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(compareNum))
			genCompareNum(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(compareStr))
			genCompareStr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(concatenate))
			genConcatenate(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(concatenateBytes))
			genConcatenateBytes(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(concatenateWithSeparator))
			genConcatenateWithSeparator(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(connectionService))
			genConnectionService(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(copyBytes))
			genCopyBytes(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(copyStr))
			genCopyStr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(findStr))
			genFindStr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(floatingDifference))
			genFloatingDifference(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(floatingMod))
			genFloatingMod(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(floatingProduct))
			genFloatingProduct(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(floatingQuotient))
			genFloatingQuotient(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(floatingSum))
			genFloatingSum(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getVAGSysType))
			genGetVAGSysType(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setSubStr))
			genSetSubStr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(startTransaction))
			genStartTransaction(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(VGTDLI))
			genVGTDLI(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genCompareBytes(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCompareNum(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCompareStr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConcatenate(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConcatenateBytes(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConcatenateWithSeparator(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConnectionService(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCopyBytes(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCopyStr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFindStr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFloatingDifference(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFloatingMod(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFloatingProduct(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFloatingQuotient(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFloatingSum(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetVAGSysType(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetSubStr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genStartTransaction(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genVGTDLI(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
