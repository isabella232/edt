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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.TimestampType;

public class TimestampTypeTemplate extends JavaTemplate {

	public void genConstructorOptions(TimestampType type, Context ctx, TabbedWriter out) {
		generateOptions(type, ctx, out);
	}

	public void genTypeDependentOptions(TimestampType type, Context ctx, TabbedWriter out) {
		out.print(", ");
		generateOptions(type, ctx, out);
	}

	public void genJsonTypeDependentOptions(TimestampType type, Context ctx, TabbedWriter out) {
		String pattern = "yyyyMMddhhmmss";
		if (type.getPattern() != null && !type.getPattern().equalsIgnoreCase("null"))
			pattern = type.getPattern();
		out.print("\"");
		out.print(getStartPattern(pattern));
		out.print("\", \"");
		out.print(getEndPattern(pattern));
		out.print("\"");
	}
	
	protected void generateOptions(TimestampType type, Context ctx, TabbedWriter out) {
		String pattern = "yyyyMMddhhmmss";
		if (type.getPattern() != null && !type.getPattern().equalsIgnoreCase("null"))
			pattern = type.getPattern();
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".");
		out.print(getStartPattern(pattern));
		out.print(", ");
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".");
		out.print(getEndPattern(pattern));
	}

	public static String getStartPattern(String pattern) {
		if (pattern.startsWith("yyyy"))
			return "YEAR_CODE";
		else if (pattern.startsWith("MM"))
			return "MONTH_CODE";
		else if (pattern.startsWith("dd"))
			return "DAY_CODE";
		else if (pattern.startsWith("HH"))
			return "HOUR_CODE";
		else if (pattern.startsWith("mm"))
			return "MINUTE_CODE";
		else if (pattern.startsWith("ss"))
			return "SECOND_CODE";
		else if (pattern.startsWith("f"))
			return "FRACTION1_CODE";
		return "";
	}

	public static String getEndPattern(String pattern) {
		if (pattern.endsWith("yyyy"))
			return "YEAR_CODE";
		else if (pattern.endsWith("MM"))
			return "MONTH_CODE";
		else if (pattern.endsWith("dd"))
			return "DAY_CODE";
		else if (pattern.endsWith("HH"))
			return "HOUR_CODE";
		else if (pattern.endsWith("mm"))
			return "MINUTE_CODE";
		else if (pattern.endsWith("ss"))
			return "SECOND_CODE";
		else if (pattern.endsWith("ffffff"))
			return "FRACTION6_CODE";
		else if (pattern.endsWith("fffff"))
			return "FRACTION5_CODE";
		else if (pattern.endsWith("ffff"))
			return "FRACTION4_CODE";
		else if (pattern.endsWith("fff"))
			return "FRACTION3_CODE";
		else if (pattern.endsWith("ff"))
			return "FRACTION2_CODE";
		else if (pattern.endsWith("f"))
			return "FRACTION1_CODE";
		return "";
	}
}
