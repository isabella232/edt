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
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class ArrayAccessTemplate extends ExpressionTemplate {

	public void genAssignment(ArrayAccess expr, Context ctx, TabbedWriter out, Object... args) {
		// are we dealing with a nullable array
		if (expr.isNullable()) {
			// if this is a well-behaved assignment, we can avoid the temporary
			if (IRUtils.hasSideEffects(expr)) {
				String temporary = ctx.nextTempName();
				ctx.gen(genRuntimeTypeName, expr.getType(), ctx, out, TypeNameKind.JavaObject);
				out.print(" " + temporary + " = ");
				genExpression((Expression) expr, ctx, out, args);
				out.println(";");
				out.print(temporary + ".set(");
				genExpression(expr.getIndex(), ctx, out, args);
				out.print(" - 1, ");
				out.print("org.eclipse.edt.runtime.java.egl.lang.AnyValue.ezeCopyTo(");
				genExpression((Expression) args[0], ctx, out, args);
				out.print(", ");
				out.print(temporary + ".get(");
				genExpression(expr.getIndex(), ctx, out, args);
				out.print(" - 1)");
				out.print(")");
				out.print(")");
			} else if (TypeUtils.isReferenceType(expr.getType()) || ctx.mapsToPrimitiveType(expr.getType())) {
				genExpression(expr.getArray(), ctx, out, args);
				out.print(".set(");
				genExpression(expr.getIndex(), ctx, out, args);
				out.print(" - 1, ");
				genExpression((Expression) args[0], ctx, out, args);
				out.print(")");
			} else {
				genExpression(expr.getArray(), ctx, out, args);
				out.print(".set(");
				genExpression(expr.getIndex(), ctx, out, args);
				out.print(" - 1, ");
				out.print("org.eclipse.edt.runtime.java.egl.lang.AnyValue.ezeCopyTo(");
				genExpression((Expression) args[0], ctx, out, args);
				out.print(", ");
				genExpression((Expression) expr, ctx, out, args);
				out.print(")");
				out.print(")");
			}
		} else if (TypeUtils.isReferenceType(expr.getType()) || ctx.mapsToPrimitiveType(expr.getType())) {
			genExpression(expr.getArray(), ctx, out, args);
			out.print(".set(");
			genExpression(expr.getIndex(), ctx, out, args);
			out.print(" - 1, ");
			genExpression((Expression) args[0], ctx, out, args);
			out.print(")");
		} else {
			// non-nullable array
			out.print("org.eclipse.edt.runtime.java.egl.lang.AnyValue.ezeCopyTo(");
			genExpression((Expression) args[0], ctx, out, args);
			out.print(", ");
			genExpression((Expression) expr, ctx, out, args);
			out.print(")");
		}
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
		genExpression(expr.getArray(), ctx, out, args);
		out.print(".get(");
		genExpression(expr.getIndex(), ctx, out, args);
		out.print(" - 1)");
	}
}
