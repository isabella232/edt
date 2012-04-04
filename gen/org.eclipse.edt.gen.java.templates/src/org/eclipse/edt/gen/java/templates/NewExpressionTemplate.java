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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.Type;

public class NewExpressionTemplate extends JavaTemplate {

	public void genExpression(NewExpression expr, Context ctx, TabbedWriter out) {
		ctx.invoke(genContainerBasedNewExpression, (Type) expr.getType(), ctx, out, expr);
	}

	public void genNewExpression(NewExpression expr, Context ctx, TabbedWriter out) {
		out.print("new ");
		ctx.invoke(genRuntimeTypeName, expr.getType(), ctx, out, TypeNameKind.JavaImplementation);
		out.print("(");
		if (expr.getArguments() != null && expr.getArguments().size() > 0)
			ctx.foreach(expr.getArguments(), ',', genExpression, ctx, out);
		else
			ctx.invoke(genConstructorOptions, expr.getType(), ctx, out);
		out.print(")");
	}
}
