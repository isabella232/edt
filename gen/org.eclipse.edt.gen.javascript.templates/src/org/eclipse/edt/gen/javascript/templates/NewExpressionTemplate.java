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
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.NewExpression;

public class NewExpressionTemplate extends JavascriptTemplate {

	public void genExpression(NewExpression expr, Context ctx, TabbedWriter out, Object... args) {
		out.print("new ");
		ctx.gen(genRuntimeTypeName, expr.getType(), ctx, out, TypeNameKind.JavascriptImplementation);
		out.print("(");
		if (expr.getArguments() != null && expr.getArguments().size() > 0) {
			for (Expression argument : expr.getArguments()) {
				ctx.gen(genExpression, argument, ctx, out, args);
			}
		} else
			ctx.gen(genConstructorOptions, expr.getType(), ctx, out, args);
		out.print(")");
	}
}
