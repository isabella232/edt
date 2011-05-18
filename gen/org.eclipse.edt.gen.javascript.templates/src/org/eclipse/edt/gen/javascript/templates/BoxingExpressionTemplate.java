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
import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.Type;

public class BoxingExpressionTemplate extends JavaScriptTemplate {

	public void genExpression(BoxingExpression expr, Context ctx, TabbedWriter out, Object... args) {
		out.print("{");
		out.print(eze$$value);
		out.print(" : ");
		ctx.gen(genExpression, expr.getExpr(), ctx, out, args);
		out.print(", ");
		out.print(eze$$signature);
		out.print(" : ");
		out.print("\"");
		ctx.gen(genSignature, (Type) expr.getExpr().getType(), ctx, out, expr.getExpr());
		out.print("\"");
		out.print("}");
	}
}
