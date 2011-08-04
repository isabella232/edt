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
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.TimestampType;
import org.eclipse.edt.mof.egl.Type;

public class TimestampTypeTemplate extends JavaScriptTemplate {

	// this method gets invoked when there is a specific timestamp needed
	public void genDefaultValue(TimestampType type, Context ctx, TabbedWriter out) {
		processDefaultValue(type, ctx, out);
	}

	// this method gets invoked when there is a generic (unknown) timestamp needed
	public void genDefaultValue(ParameterizableType type, Context ctx, TabbedWriter out) {
		processDefaultValue(type, ctx, out);
	}

	public void processDefaultValue(Type type, Context ctx, TabbedWriter out) {
		out.print(Constants.JSRT_DTTMLIB_PKG + "currentTimeStamp(");
		ctx.invoke(genTypeDependentOptions, type, ctx, out);
		out.print(")");
	}

	// this method gets invoked when there is a specific timestamp needed
	public void genSignature(TimestampType type, Context ctx, TabbedWriter out) {
		String signature = "J'" + type.getPattern() + "';";
		out.print(signature);
	}

	// this method gets invoked when there is a generic (unknown) timestamp needed
	public void genSignature(ParameterizableType type, Context ctx, TabbedWriter out) {
		String signature = "J;";
		out.print(signature);
	}

	public void genTypeDependentOptions(TimestampType type, Context ctx, TabbedWriter out) {
		String pattern = "yyyyMMddhhmmss";
		if (type.getPattern() != null && !type.getPattern().equalsIgnoreCase("null"))
			pattern = type.getPattern();
		out.print(quoted(pattern));
	}
}
