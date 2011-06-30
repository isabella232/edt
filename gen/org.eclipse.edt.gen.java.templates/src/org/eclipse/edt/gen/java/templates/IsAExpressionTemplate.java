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

public class IsAExpressionTemplate extends JavaTemplate {

	public void genExpression(IsAExpression expr, Context ctx, TabbedWriter out) {
		if (ctx.mapsToPrimitiveType(expr.getEType())) {
			ctx.invoke(genRuntimeTypeName, expr.getEType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".ezeIsa(");
			ctx.invoke(genExpression, expr.getObjectExpr(), ctx, out);
			ctx.invoke(genTypeDependentOptions, expr.getEType(), ctx, out);
			out.print(")");
		} else {
			out.print("AnyObject.ezeIsa(");
			ctx.invoke(genExpression, expr.getObjectExpr(), ctx, out);
			out.print(", ");
			ctx.invoke(genRuntimeTypeName, expr.getEType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".class)");
		}
	}
}
