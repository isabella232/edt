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

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class ArrayAccessTemplate extends JavaTemplate {

	public void genAssignment(ArrayAccess expr, Context ctx, TabbedWriter out, Expression arg1, String arg2) {
		// are we dealing with a nullable array
		if (expr.isNullable()) {
			// if this is a well-behaved assignment, we can avoid the temporary
			if (org.eclipse.edt.gen.CommonUtilities.hasSideEffects(expr, ctx)) {
				String temporary = ctx.nextTempName();
				ctx.invoke(genRuntimeTypeName, expr.getType(), ctx, out, TypeNameKind.JavaObject);
				out.print(" " + temporary + " = ");
				ctx.invoke(genExpression, (Expression) expr, ctx, out);
				out.println(";");
				out.print(temporary + ".set(");
				ctx.invoke(genExpression, expr.getIndex(), ctx, out);
				out.print(" - 1, ");
				CommonUtilities.genEzeCopyTo(arg1, ctx, out);
				ctx.invoke(genExpression, arg1, ctx, out);
				out.print(", ");
				out.print(temporary + ".get(");
				out.print("org.eclipse.edt.javart.util.JavartUtil.checkIndex(");
				ctx.invoke(genExpression, expr.getIndex(), ctx, out);
				out.print(" - 1, ");
				out.print(temporary);
				out.print(")");
				out.print(")");
				out.print(")");
				out.print(")");
			} else if (TypeUtils.isReferenceType(expr.getType()) || ctx.mapsToPrimitiveType(expr.getType())) {
				ctx.invoke(genExpression, expr.getArray(), ctx, out);
				out.print(".set(");
				out.print("org.eclipse.edt.javart.util.JavartUtil.checkIndex(");
				ctx.invoke(genExpression, expr.getIndex(), ctx, out);
				out.print(" - 1, ");
				ctx.invoke(genExpression, expr.getArray(), ctx, out);
				out.print("), ");
				ctx.invoke(genExpression, arg1, ctx, out);
				if (CommonUtilities.isBoxedOutputTemp(arg1, ctx))
					out.print(".ezeUnbox()");
				out.print(")");
			} else {
				ctx.invoke(genExpression, expr.getArray(), ctx, out);
				out.print(".set(");
				out.print("org.eclipse.edt.javart.util.JavartUtil.checkIndex(");
				ctx.invoke(genExpression, expr.getIndex(), ctx, out);
				out.print(" - 1, ");
				ctx.invoke(genExpression, expr.getArray(), ctx, out);
				out.print("), ");
				CommonUtilities.genEzeCopyTo(arg1, ctx, out);
				ctx.invoke(genExpression, arg1, ctx, out);
				out.print(", ");
				ctx.invoke(genExpression, (Expression) expr, ctx, out);
				out.print(")");
				out.print(")");
			}
		} else if (TypeUtils.isReferenceType(expr.getType()) || ctx.mapsToPrimitiveType(expr.getType())) {
			ctx.invoke(genExpression, expr.getArray(), ctx, out);
			out.print(".set(");
			out.print("org.eclipse.edt.javart.util.JavartUtil.checkIndex(");
			ctx.invoke(genExpression, expr.getIndex(), ctx, out);
			out.print(" - 1, ");
			ctx.invoke(genExpression, expr.getArray(), ctx, out);
			out.print("), ");
			ctx.invoke(genExpression, arg1, ctx, out);
			if (CommonUtilities.isBoxedOutputTemp(arg1, ctx))
				out.print(".ezeUnbox()");
			out.print(")");
		} else {
			// non-nullable array
			CommonUtilities.genEzeCopyTo(arg1, ctx, out);
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(", ");
			ctx.invoke(genExpression, (Expression) expr, ctx, out);
			out.print(")");
		}
	}

	public void genExpression(ArrayAccess expr, Context ctx, TabbedWriter out) {
		Field field = null;
		if (expr.getArray() instanceof Name && ((Name) expr.getArray()).getNamedElement() instanceof Field)
			field = (Field) ((Name) expr.getArray()).getNamedElement();
		if (field != null && field.getContainer() != null && field.getContainer() instanceof Type)
			ctx.invoke(genContainerBasedArrayAccess, (Type) field.getContainer(), ctx, out, expr, field);
		else
			genArrayAccess(expr, ctx, out);
	}

	public void genArrayAccess(ArrayAccess expr, Context ctx, TabbedWriter out) {
		if (expr.getArray().getType().equals(TypeUtils.Type_ANY)) {
			out.print("org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeGet(");
			ctx.invoke(genExpression, expr.getArray(), ctx, out);
			out.print(", ");
			ctx.invoke(genExpression, expr.getIndex(), ctx, out);
			out.print(" - 1");
			out.print(")");
		} else {
			ctx.invoke(genExpression, expr.getArray(), ctx, out);
			out.print(".get(");
			out.print("org.eclipse.edt.javart.util.JavartUtil.checkIndex(");
			ctx.invoke(genExpression, expr.getIndex(), ctx, out);
			out.print(" - 1, ");
			ctx.invoke(genExpression, expr.getArray(), ctx, out);
			out.print(")");
			out.print(")");
		}
	}
}
