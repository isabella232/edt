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

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.IsAExpression;

public class IsAExpressionTemplate extends JavaScriptTemplate {

	public void genExpression(IsAExpression expr, Context ctx, TabbedWriter out) {
		out.print("egl.isa(");
		Expression objectExpr = expr.getObjectExpr();
		ctx.putAttribute(objectExpr, Constants.DONT_UNBOX, Boolean.TRUE);
		ctx.invoke(genExpression, objectExpr, ctx, out);
		ctx.putAttribute(objectExpr, Constants.DONT_UNBOX, Boolean.FALSE);  //TODO sbg Can we just remove DONT_UNBOX?
		out.print(", ");
		if (ctx.mapsToPrimitiveType(expr.getEType())) {
			out.print("\"");
			ctx.invoke(genSignature, expr.getEType(), ctx, out); // out.print(quoted(expr.getEType().getTypeSignature()));
			out.print("\"");
		}
		else
			ctx.invoke(genRuntimeTypeName, expr.getEType(), ctx, out, TypeNameKind.JavascriptObject);
		out.print(")");
	}
}
