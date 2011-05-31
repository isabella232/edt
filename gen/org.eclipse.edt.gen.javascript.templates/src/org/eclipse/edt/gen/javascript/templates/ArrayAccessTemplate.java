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
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Type;

public class ArrayAccessTemplate extends JavaScriptTemplate {

	public void genAssignment(ArrayAccess expr, Context ctx, TabbedWriter out, Object... args) {
		out.print("egl.checkNull(");
		ctx.gen(genExpression, expr.getArray(), ctx, out, args);
		out.print(")");
		out.print("[");
		ctx.gen(genExpression, expr.getArray(), ctx, out, args);
		out.print(".checkIndex(");
		ctx.gen(genExpression, expr.getIndex(), ctx, out, args);
		out.print(" - 1)]");
	}

	public void genExpression(ArrayAccess expr, Context ctx, TabbedWriter out, Object... args) {
		Field field = null;
		if (((Name) expr.getArray()).getNamedElement() instanceof Field)
			field = (Field) ((Name) expr.getArray()).getNamedElement();
		if (field != null && field.getContainer() != null && field.getContainer() instanceof Type)
			ctx.gen(genContainerBasedArrayAccess, (Type) field.getContainer(), ctx, out, expr, field);
		else
			genArrayAccess(expr, ctx, out, args);
	}

	public void genArrayAccess(ArrayAccess expr, Context ctx, TabbedWriter out, Object... args) {
		if (((ArrayType) expr.getArray().getType()).elementsNullable()) {
			out.print("egl.nullableCheckIndex(");
			ctx.gen(genExpression, expr.getArray(), ctx, out, args);
			out.print(", ");
			ctx.gen(genExpression, expr.getIndex(), ctx, out, args);
			out.print(")");
		} else {
			out.print("egl.checkNull(");
			ctx.gen(genExpression, expr.getArray(), ctx, out, args);
			out.print(")");
			out.print("[");
			ctx.gen(genExpression, expr.getArray(), ctx, out, args);
			out.print(".checkIndex(");
			ctx.gen(genExpression, expr.getIndex(), ctx, out, args);
			out.print(" - 1)]");
		}
	}
}
