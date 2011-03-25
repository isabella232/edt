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

public class DateTimeLibTemplate extends NativeTypeTemplate {
	private static final String currentDate = "currentDate";
	private static final String currentTime = "currentTime";
	private static final String currentTimeStamp = "currentTimeStamp";
	private static final String dateFromInt = "dateFromInt";
	private static final String dateValue = "dateValue";
	private static final String dateValueFromGregorian = "dateValueFromGregorian";
	private static final String dateValueFromJulian = "dateValueFromJulian";
	private static final String timeValue = "timeValue";
	private static final String intervalValue = "intervalValue";
	private static final String intervalValueWithPattern = "intervalValueWithPattern";
	private static final String timeStampValue = "timeStampValue";
	private static final String timeStampValueWithPattern = "timeStampValueWithPattern";
	private static final String timestampFrom = "timestampFrom";
	private static final String dayOf = "dayOf";
	private static final String monthOf = "monthOf";
	private static final String yearOf = "yearOf";
	private static final String weekdayOf = "weekdayOf";
	private static final String mdy = "mdy";
	private static final String dateOf = "dateOf";
	private static final String timeOf = "timeOf";
	private static final String extend = "extend";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(currentDate))
			genCurrentDate(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(currentTime))
			genCurrentTime(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(currentTimeStamp))
			genCurrentTimeStamp(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(dateFromInt))
			genDateFromInt(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(dateValue))
			genDateValue(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(dateValueFromGregorian))
			genDateValueFromGregorian(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(dateValueFromJulian))
			genDateValueFromJulian(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(timeValue))
			genTimeValue(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(intervalValue))
			genIntervalValue(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(intervalValueWithPattern))
			genIntervalValueWithPattern(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(timeStampValue))
			genTimeStampValue(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(timeStampValueWithPattern))
			genTimeStampValueWithPattern(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(timestampFrom))
			genTimestampFrom(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(dayOf))
			genDayOf(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(monthOf))
			genMonthOf(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(yearOf))
			genYearOf(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(weekdayOf))
			genWeekdayOf(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(mdy))
			genMdy(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(dateOf))
			genDateOf(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(timeOf))
			genTimeOf(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(extend))
			genExtend(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genCurrentDate(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCurrentTime(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCurrentTimeStamp(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDateFromInt(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDateValue(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDateValueFromGregorian(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDateValueFromJulian(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genTimeValue(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genIntervalValue(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genIntervalValueWithPattern(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genTimeStampValue(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genTimeStampValueWithPattern(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genTimestampFrom(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDayOf(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genMonthOf(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genYearOf(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genWeekdayOf(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genMdy(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDateOf(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genTimeOf(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genExtend(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
