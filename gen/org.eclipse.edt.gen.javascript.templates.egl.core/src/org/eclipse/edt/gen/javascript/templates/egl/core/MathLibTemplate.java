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

public class MathLibTemplate extends NativeTypeTemplate {
	private static final String abs = "abs";
	private static final String acos = "acos";
	private static final String asin = "asin";
	private static final String assign = "assign";
	private static final String atan = "atan";
	private static final String atan2 = "atan2";
	private static final String ceiling = "ceiling";
	private static final String cos = "cos";
	private static final String cosh = "cosh";
	private static final String decimals = "decimals";
	private static final String exp = "exp";
	private static final String floor = "floor";
	private static final String frexp = "frexp";
	private static final String ldexp = "ldexp";
	private static final String log = "log";
	private static final String log10 = "log10";
	private static final String max = "max";
	private static final String min = "min";
	private static final String modf = "modf";
	private static final String pow = "pow";
	private static final String precision = "precision";
	private static final String round = "round";
	private static final String sin = "sin";
	private static final String sinh = "sinh";
	private static final String sqrt = "sqrt";
	private static final String tan = "tan";
	private static final String tanh = "tanh";
	private static final String stringAsFloat = "stringAsFloat";
	private static final String stringAsInt = "stringAsInt";
	private static final String stringAsDecimal = "stringAsDecimal";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(abs))
			genAbs(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(acos))
			genAcos(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(asin))
			genAsin(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(assign))
			genAssign(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(atan))
			genAtan(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(atan2))
			genAtan2(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(ceiling))
			genCeiling(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(cos))
			genCos(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(cosh))
			genCosh(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(decimals))
			genDecimals(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(exp))
			genExp(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(floor))
			genFloor(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(frexp))
			genFrexp(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(ldexp))
			genLdexp(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(log))
			genLog(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(log10))
			genLog10(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(max))
			genMax(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(min))
			genMin(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(modf))
			genModf(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(pow))
			genPow(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(precision))
			genPrecision(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(round))
			genRound(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(sin))
			genSin(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(sinh))
			genSinh(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(sqrt))
			genSqrt(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(tan))
			genTan(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(tanh))
			genTanh(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(stringAsFloat))
			genStringAsFloat(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(stringAsInt))
			genStringAsInt(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(stringAsDecimal))
			genStringAsDecimal(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genAbs(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genAcos(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genAsin(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genAssign(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genAtan(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genAtan2(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCeiling(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCos(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCosh(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDecimals(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genExp(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFloor(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFrexp(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genLdexp(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genLog(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genLog10(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genMax(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genMin(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genModf(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genPow(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genPrecision(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genRound(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSin(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSinh(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSqrt(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genTan(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genTanh(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genStringAsFloat(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genStringAsInt(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genStringAsDecimal(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
