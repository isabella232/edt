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
import org.eclipse.edt.mof.egl.ArrayLiteral;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class ArrayLiteralTemplate extends LiteralTemplate {

	public void genExpression(ArrayLiteral expr, Context ctx, TabbedWriter out, Object... args) {
		out.print("new ");
		ctx.gen(genRuntimeTypeName, expr.getType(), ctx, out, TypeNameKind.JavaImplementation);
		out.print("(");
		if (expr.getEntries() != null)
			out.print(expr.getEntries().size());
		out.print(")");
		// now loop through all of the array literals, adding them to the new expression
		for (int i = 0; i < expr.getEntries().size(); i++) {
			out.print(".ezeSet(");
			out.print(i + 1);
			out.print(", ");
			genExpression(IRUtils.makeExprCompatibleToType(expr.getEntries().get(i), ((ArrayType) expr.getType()).getElementType()), ctx, out, args);
			out.print(")");
		}
	}
}
