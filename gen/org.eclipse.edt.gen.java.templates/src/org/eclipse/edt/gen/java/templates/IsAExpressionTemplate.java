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
import org.eclipse.edt.mof.egl.IsAExpression;

public class IsAExpressionTemplate extends ExpressionTemplate {

	public void genExpression(IsAExpression expr, Context ctx, TabbedWriter out, Object... args) {
		if (ctx.mapsToPrimitiveType(expr.getEType())) {
			ctx.gen(genRuntimeTypeName, expr.getEType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".ezeIsa(");
			genExpression(expr.getObjectExpr(), ctx, out, args);
			ctx.gen(genTypeDependentOptions, expr.getEType(), ctx, out, args);
			out.print(')');
		} else {
			out.print("AnyObject.ezeIsa(");
			genExpression(expr.getObjectExpr(), ctx, out, args);
			out.print(", ");
			ctx.gen(genRuntimeTypeName, expr.getEType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".class)");
		}
	}
}
