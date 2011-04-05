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

	public void genExpression(BoxingExpression expr, Context ctx, TabbedWriter out, Object... args) {
		if (ctx.mapsToPrimitiveType(expr.getType())) {
			ctx.gen(genRuntimeTypeName, expr.getType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".ezeBox(");
			ctx.gen(genExpression, expr.getExpr(), ctx, out, args);
			ctx.gen(genTypeDependentOptions, expr.getType(), ctx, out, args);
			out.print(")");
		} else {
			out.print("AnyObject.ezeBox(");
			ctx.gen(genExpression, expr.getExpr(), ctx, out, args);
			out.print(")");
		}
	}
}
