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

	public void genTypeDependentOptions(TimestampType type, Context ctx, TabbedWriter out, Object... args) {
		String pattern = "yyyyMMddhhmmss";
		if (type.getPattern() != null && !type.getPattern().equalsIgnoreCase("null"))
			pattern = type.getPattern();
		String start = "";
		String end = "";
		if (pattern.startsWith("yyyy"))
			start = ".YEAR_CODE";
		else if (pattern.startsWith("MM"))
			start = ".MONTH_CODE";
		else if (pattern.startsWith("dd"))
			start = ".DAY_CODE";
		else if (pattern.startsWith("hh"))
			start = ".HOUR_CODE";
		else if (pattern.startsWith("mm"))
			start = ".MINUTE_CODE";
		else if (pattern.startsWith("ss"))
			start = ".SECOND_CODE";
		else if (pattern.startsWith("f"))
			start = ".FRACTION1_CODE";
		out.print(", ");
		ctx.gen(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(start);
		if (pattern.endsWith("yyyy"))
			end = ".YEAR_CODE";
		else if (pattern.endsWith("MM"))
			end = ".MONTH_CODE";
		else if (pattern.endsWith("dd"))
			end = ".DAY_CODE";
		else if (pattern.endsWith("hh"))
			end = ".HOUR_CODE";
		else if (pattern.endsWith("mm"))
			end = ".MINUTE_CODE";
		else if (pattern.endsWith("ss"))
			end = ".SECOND_CODE";
		else if (pattern.endsWith("ffffff"))
			end = ".FRACTION6_CODE";
		else if (pattern.endsWith("fffff"))
			end = ".FRACTION5_CODE";
		else if (pattern.endsWith("ffff"))
			end = ".FRACTION4_CODE";
		else if (pattern.endsWith("fff"))
			end = ".FRACTION3_CODE";
		else if (pattern.endsWith("ff"))
			end = ".FRACTION2_CODE";
		else if (pattern.endsWith("f"))
			end = ".FRACTION1_CODE";
		out.print(", ");
		ctx.gen(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(end);
	}
}
