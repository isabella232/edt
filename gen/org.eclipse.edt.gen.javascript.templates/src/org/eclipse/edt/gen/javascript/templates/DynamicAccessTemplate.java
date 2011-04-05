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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.DynamicAccess;
import org.eclipse.edt.mof.egl.Expression;

public class DynamicAccessTemplate extends JavascriptTemplate {

	public void genAssignment(DynamicAccess expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genExpression, expr.getExpression(), ctx, out, args);
		out.print(".ezeSet(");
		ctx.gen(genExpression, expr.getAccess(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, (Expression) args[0], ctx, out, args);
		out.print(")");
	}

	public void genExpression(DynamicAccess expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genExpression, expr.getExpression(), ctx, out, args);
		out.print(".ezeGet(");
		ctx.gen(genExpression, expr.getAccess(), ctx, out, args);
		out.print(")");
	}
}
