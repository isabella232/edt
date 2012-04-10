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
import org.eclipse.edt.mof.egl.ObjectExpression;
import org.eclipse.edt.mof.egl.ObjectExpressionEntry;

public class ObjectExpressionTemplate extends JavaTemplate {

	public void genExpression(ObjectExpression expr, Context ctx, TabbedWriter out) {
		if (expr.getQualifier() != null) {
			ctx.invoke(genExpression, expr.getQualifier(), ctx, out);
			out.print(".");
		}
		out.print("(");
		ctx.invoke(genObjectExpressionEntries, expr, ctx, out);
		out.print(")");
	}

	public void genObjectExpressionEntries(ObjectExpression expr, Context ctx, TabbedWriter out) {
		// check to see if we are trying to pass a nullable to a non-nullable
		if (expr.getEntries() != null) {
			for (int i = 0; i < expr.getEntries().size(); i++) {
				ObjectExpressionEntry argExpr = expr.getEntries().get(i);
				ctx.invoke(genExpression, argExpr, ctx, out);
				if (i < expr.getEntries().size() - 1)
					out.print(", ");
			}
		}
	}
}
