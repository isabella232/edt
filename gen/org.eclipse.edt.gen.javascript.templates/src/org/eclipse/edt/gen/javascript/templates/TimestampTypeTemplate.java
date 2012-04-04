/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.TimestampType;

public class TimestampTypeTemplate extends JavaScriptTemplate {

	public void genConstructorOptions(TimestampType type, Context ctx, TabbedWriter out) {
		// we need to skip over the 1st comma and space
		generateOptions(type, ctx, out, false);
	}

	public void genTypeDependentOptions(TimestampType type, Context ctx, TabbedWriter out) {
		generateOptions(type, ctx, out, true);
	}
	
	public void genTypeDependentPatterns(TimestampType type, Context ctx, TabbedWriter out) {
		generateOptions(type, ctx, out, true);
	}

	protected void generateOptions(TimestampType type, Context ctx, TabbedWriter out, boolean needSeparator) {
		if (needSeparator)
			out.print(", ");
		String pattern = "yyyyMMddhhmmss";
		if (type.getPattern() != null && !type.getPattern().equalsIgnoreCase("null"))
			pattern = type.getPattern();
		out.print(quoted(pattern));
	}	
	
}
