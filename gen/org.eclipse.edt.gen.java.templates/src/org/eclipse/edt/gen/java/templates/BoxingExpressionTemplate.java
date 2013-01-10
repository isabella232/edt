/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.BoxingExpression;

public class BoxingExpressionTemplate extends JavaTemplate {

	public void genExpression(BoxingExpression expr, Context ctx, TabbedWriter out) {
		if (expr.getType() instanceof ArrayType) {
			out.print("org.eclipse.edt.runtime.java.eglx.lang.EList.ezeBox(");
			ctx.invoke(genExpression, expr.getExpr(), ctx, out);
			if ((CommonUtilities.isBoxedOutputTemp(expr.getExpr(), ctx)))
				out.print(".ezeUnbox()");
			out.print(", \"");
			out.print(expr.getType().getTypeSignature());
			out.print("\")");
		} else if (ctx.mapsToPrimitiveType(expr.getType())) {
			ctx.invoke(genRuntimeTypeName, expr.getType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".ezeBox(");
			ctx.invoke(genExpression, expr.getExpr(), ctx, out);
			if ((CommonUtilities.isBoxedOutputTemp(expr.getExpr(), ctx)))
				out.print(".ezeUnbox()");
			ctx.invoke(genTypeDependentOptions, expr.getType(), ctx, out);
			out.print(")");
		} else {
			if (ctx.getAttribute(expr, org.eclipse.edt.gen.Constants.SubKey_functionArgumentNeedsWrapping) != null
				&& ((Boolean) ctx.getAttribute(expr, org.eclipse.edt.gen.Constants.SubKey_functionArgumentNeedsWrapping)).booleanValue())
				out.print("org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeWrap(");
			else
				out.print("org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeBox(");
			ctx.invoke(genExpression, expr.getExpr(), ctx, out);
			if ((CommonUtilities.isBoxedOutputTemp(expr.getExpr(), ctx)))
				out.print(".ezeUnbox()");
			out.print(")");
		}
	}
}
