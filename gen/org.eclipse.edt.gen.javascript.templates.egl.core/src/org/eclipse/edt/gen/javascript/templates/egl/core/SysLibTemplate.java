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

public class SysLibTemplate extends NativeTypeTemplate {
	private static final String audit = "audit";
	private static final String bytes = "bytes";
	private static final String calculateChkDigitMod10 = "calculateChkDigitMod10";
	private static final String calculateChkDigitMod11 = "calculateChkDigitMod11";
	private static final String callCmd = "callCmd";
	private static final String conditionAsInt = "conditionAsInt";
	private static final String convert = "convert";
	private static final String convertBidi = "convertBidi";
	private static final String convertEncodedTextToString = "convertEncodedTextToString";
	private static final String convertNumberToUnicodeNum = "convertNumberToUnicodeNum";
	private static final String convertNumberToUnsignedUnicodeNum = "convertNumberToUnsignedUnicodeNum";
	private static final String convertStringToEncodedText = "convertStringToEncodedText";
	private static final String convertUnicodeNumToNumber = "convertUnicodeNumToNumber";
	private static final String convertUnsignedUnicodeNumToNumber = "convertUnsignedUnicodeNumToNumber";
	private static final String commit = "commit";
	private static final String errorLog = "errorLog";
	private static final String getCmdLineArgCount = "getCmdLineArgCount";
	private static final String getCmdLineArg = "getCmdLineArg";
	private static final String getMessage = "getMessage";
	private static final String getProperty = "getProperty";
	private static final String maximumSize = "maximumSize";
	private static final String purge = "purge";
	private static final String rollback = "rollback";
	private static final String setError = "setError";
	private static final String setErrorForComponentId = "setErrorForComponentId";
	private static final String setLocale = "setLocale";
	private static final String setRemoteUser = "setRemoteUser";
	private static final String size = "size";
	private static final String startCmd = "startCmd";
	private static final String verifyChkDigitMod10 = "verifyChkDigitMod10";
	private static final String verifyChkDigitMod11 = "verifyChkDigitMod11";
	private static final String wait = "wait";
	private static final String writeStderr = "writeStderr";
	private static final String writeStdout = "writeStdout";
	private static final String startLog = "startLog";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(audit))
			genAudit(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(bytes))
			genBytes(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(calculateChkDigitMod10))
			genCalculateChkDigitMod10(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(calculateChkDigitMod11))
			genCalculateChkDigitMod11(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(callCmd))
			genCallCmd(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(conditionAsInt))
			genConditionAsInt(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(convert))
			genConvert(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(convertBidi))
			genConvertBidi(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(convertEncodedTextToString))
			genConvertEncodedTextToString(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(convertNumberToUnicodeNum))
			genConvertNumberToUnicodeNum(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(convertNumberToUnsignedUnicodeNum))
			genConvertNumberToUnsignedUnicodeNum(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(convertStringToEncodedText))
			genConvertStringToEncodedText(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(convertUnicodeNumToNumber))
			genConvertUnicodeNumToNumber(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(convertUnsignedUnicodeNumToNumber))
			genConvertUnsignedUnicodeNumToNumber(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(commit))
			genCommit(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(errorLog))
			genErrorLog(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getCmdLineArgCount))
			genGetCmdLineArgCount(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getCmdLineArg))
			genGetCmdLineArg(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getMessage))
			genGetMessage(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getProperty))
			genGetProperty(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(maximumSize))
			genMaximumSize(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(purge))
			genPurge(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(rollback))
			genRollback(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setError))
			genSetError(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setErrorForComponentId))
			genSetErrorForComponentId(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setLocale))
			genSetLocale(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setRemoteUser))
			genSetRemoteUser(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(size))
			genSize(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(startCmd))
			genStartCmd(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(verifyChkDigitMod10))
			genVerifyChkDigitMod10(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(verifyChkDigitMod11))
			genVerifyChkDigitMod11(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(wait))
			genWait(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(writeStderr))
			genWriteStderr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(writeStdout))
			genWriteStdout(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(startLog))
			genStartLog(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genAudit(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genBytes(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCalculateChkDigitMod10(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCalculateChkDigitMod11(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCallCmd(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConditionAsInt(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConvert(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConvertBidi(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConvertEncodedTextToString(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConvertNumberToUnicodeNum(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConvertNumberToUnsignedUnicodeNum(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConvertStringToEncodedText(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConvertUnicodeNumToNumber(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConvertUnsignedUnicodeNumToNumber(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCommit(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genErrorLog(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetCmdLineArgCount(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetCmdLineArg(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetMessage(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetProperty(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genMaximumSize(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genPurge(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genRollback(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetError(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetErrorForComponentId(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetLocale(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetRemoteUser(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSize(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genStartCmd(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genVerifyChkDigitMod10(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genVerifyChkDigitMod11(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genWait(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genWriteStderr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		out.print("egl.println(");
		ctx.gen(genExpression, expr.getArguments().get(0), ctx, out);
		out.print(")");
	}

	public void genWriteStdout(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		out.print("egl.println(");
		ctx.gen(genExpression, expr.getArguments().get(0), ctx, out);
		out.print(")");
	}

	public void genStartLog(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
