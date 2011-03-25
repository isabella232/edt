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

public class StrLibTemplate extends NativeTypeTemplate {
	private static final String booleanAsString = "booleanAsString";
	private static final String byteLen = "byteLen";
	private static final String characterLen = "characterLen";
	private static final String clip = "clip";
	private static final String formatDate = "formatDate";
	private static final String formatNumber = "formatNumber";
	private static final String formatTime = "formatTime";
	private static final String formatTimeStamp = "formatTimeStamp";
	private static final String getNextToken = "getNextToken";
	private static final String getTokenCount = "getTokenCount";
	private static final String indexOf = "indexOf";
	private static final String intAsChar = "intAsChar";
	private static final String charAsInt = "charAsInt";
	private static final String intAsUnicode = "intAsUnicode";
	private static final String unicodeAsInt = "unicodeAsInt";
	private static final String setBlankTerminator = "setBlankTerminator";
	private static final String setNullTerminator = "setNullTerminator";
	private static final String lowerCase = "lowerCase";
	private static final String spaces = "spaces";
	private static final String upperCase = "upperCase";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(booleanAsString))
			genBooleanAsString(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(byteLen))
			genByteLen(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(characterLen))
			genCharacterLen(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(clip))
			genClip(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(formatDate))
			genFormatDate(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(formatNumber))
			genFormatNumber(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(formatTime))
			genFormatTime(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(formatTimeStamp))
			genFormatTimeStamp(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getNextToken))
			genGetNextToken(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getTokenCount))
			genGetTokenCount(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(indexOf))
			genIndexOf(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(intAsChar))
			genIntAsChar(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(charAsInt))
			genCharAsInt(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(intAsUnicode))
			genIntAsUnicode(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(unicodeAsInt))
			genUnicodeAsInt(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setBlankTerminator))
			genSetBlankTerminator(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setNullTerminator))
			genSetNullTerminator(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(lowerCase))
			genLowerCase(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(spaces))
			genSpaces(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(upperCase))
			genUpperCase(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genBooleanAsString(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genByteLen(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCharacterLen(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genClip(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFormatDate(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFormatNumber(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFormatTime(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genFormatTimeStamp(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetNextToken(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetTokenCount(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genIndexOf(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genIntAsChar(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCharAsInt(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genIntAsUnicode(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genUnicodeAsInt(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetBlankTerminator(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetNullTerminator(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genLowerCase(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSpaces(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genUpperCase(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
