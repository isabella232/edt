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
package org.eclipse.edt.gen.javascript.templates.egl.lang;

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.TimestampType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;

public class AnyTimestampTypeTemplate extends JavaScriptTemplate {

	// this method gets invoked when there is a specific timestamp needed
	public void genDefaultValue(TimestampType type, Context ctx, TabbedWriter out, Object... args) {
		processDefaultValue(type, ctx, out, args);
	}

	// this method gets invoked when there is a generic (unknown) timestamp needed
	public void genDefaultValue(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		processDefaultValue(type, ctx, out, args);
	}

	public void processDefaultValue(Type type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else {
			out.print(Constants.JSRT_DTTMLIB_PKG + "currentTimeStamp(");
			ctx.gen(genTypeDependentOptions, type, ctx, out, args);
			out.print(")");
		}
	}

	// this method gets invoked when there is a specific timestamp needed
	public void genSignature(TimestampType type, Context ctx, TabbedWriter out, Object... args) {
		String signature = "";
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			signature += "?";
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			signature += "?";
		signature += "J'" + type.getPattern() + "';";
		out.print(signature);
	}

	// this method gets invoked when there is a generic (unknown) timestamp needed
	public void genSignature(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		String signature = "";
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			signature += "?";
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			signature += "?";
		signature += "J;";
		out.print(signature);
	}

	public void genTypeDependentOptions(TimestampType type, Context ctx, TabbedWriter out, Object... args) {
		String pattern = "yyyyMMddhhmmss";
		if (type.getPattern() != null && !type.getPattern().equalsIgnoreCase("null"))
			pattern = type.getPattern();
		out.print(quoted(pattern));
	}
}
