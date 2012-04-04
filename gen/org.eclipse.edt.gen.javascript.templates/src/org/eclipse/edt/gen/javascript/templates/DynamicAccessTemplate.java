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
import org.eclipse.edt.mof.egl.DynamicAccess;
import org.eclipse.edt.mof.egl.Expression;

public class DynamicAccessTemplate extends JavaScriptTemplate {

	public void genAssignment(DynamicAccess expr, Context ctx, TabbedWriter out, Expression arg1, String arg2) {
		out.print("egl.eglx.lang.EDictionary");  //TODO sbg need to lookup
		out.print(".set("); 
		ctx.invoke(genExpression, expr.getExpression(), ctx, out);
		out.print(", ");
		ctx.invoke(genExpression, expr.getAccess(), ctx, out);
		out.print(", ");
		ctx.invoke(genExpression, arg1, ctx, out);
		out.print(")");
	}

	public void genExpression(DynamicAccess expr, Context ctx, TabbedWriter out) {
		out.print("egl.eglx.lang.EDictionary");  //TODO sbg need to lookup
		out.print(".get(");
		ctx.invoke(genExpression, expr.getExpression(), ctx, out);
		out.print(", ");
		ctx.invoke(genExpression, expr.getAccess(), ctx, out);
		out.print(")");
	}
}
