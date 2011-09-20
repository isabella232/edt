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

import java.util.List;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayLiteral;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class ArrayLiteralTemplate extends JavaTemplate {

	public void genExpression(ArrayLiteral expr, Context ctx, TabbedWriter out) {
		List<Expression> entries = expr.getEntries();
		out.print("new ");
		ctx.invoke(genRuntimeTypeName, expr.getType(), ctx, out, TypeNameKind.EGLImplementation);
		out.print('(');
		out.print(entries == null ? 0 : entries.size());
		out.print(')');
		// now loop through all of the entries, adding them to the new expression
		if (entries != null) {
			Type elementType = ((ArrayType) expr.getType()).getElementType();
			int i = 1;
			for (Expression element : entries) {
				out.print(".ezeSet(");
				out.print(i);
				out.print(", ");
				ctx.invoke(genExpression, IRUtils.makeExprCompatibleToType(element, elementType), ctx, out);
				out.print(")");
				i++;
			}
		}
	}
}
