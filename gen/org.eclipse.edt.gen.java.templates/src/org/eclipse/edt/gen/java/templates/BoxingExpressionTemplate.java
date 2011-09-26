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
import org.eclipse.edt.mof.egl.BoxingExpression;

public class BoxingExpressionTemplate extends JavaTemplate {

	public void genExpression(BoxingExpression expr, Context ctx, TabbedWriter out) {
		if (ctx.mapsToPrimitiveType(expr.getType())) {
			ctx.invoke(genRuntimeTypeName, expr.getType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".");
			ctx.invoke(genBoxingFunctionName, expr.getExpr(), ctx, out);
			out.print("(");
			ctx.invoke(genExpression, expr.getExpr(), ctx, out);
			ctx.invoke(genTypeDependentOptions, expr.getType(), ctx, out);
			out.print(")");
		} else {
			out.print("EAny.");
			ctx.invoke(genBoxingFunctionName, expr.getExpr(), ctx, out);
			out.print("(");
			ctx.invoke(genExpression, expr.getExpr(), ctx, out);
			out.print(")");
		}
	}
}
