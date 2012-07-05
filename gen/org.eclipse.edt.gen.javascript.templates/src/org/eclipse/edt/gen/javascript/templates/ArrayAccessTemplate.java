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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayAccess;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Name;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class ArrayAccessTemplate extends JavaScriptTemplate {

	public void genAssignment(ArrayAccess expr, Context ctx, TabbedWriter out, Expression arg1, String arg2) {
		out.print("egl.checkNull(");
		ctx.invoke(genCheckNullArgs, expr, ctx, out);
		out.print(")");
		out.print("[");
		ctx.invoke(genExpression, expr.getArray(), ctx, out);
		out.print(".checkIndex(");
		ctx.invoke(genExpression, expr.getIndex(), ctx, out);
		out.print(" - 1)]");
		out.print( " = " );
		if (TypeUtils.isReferenceType(expr.getType()) || ctx.mapsToPrimitiveType(expr.getType())) {
			out.print("egl.unboxAny(");
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(")");
		} else {
			ctx.invoke(genExpression, arg1, ctx, out);
		}
	}
	
	public void genCheckNullArgs(ArrayAccess expr, Context ctx, TabbedWriter out) {
		ctx.invoke(genExpression, expr.getArray(), ctx, out);
	}

	public void genExpression(ArrayAccess expr, Context ctx, TabbedWriter out) {
		Field field = null;
		if(expr.getArray() instanceof Name){
			if (((Name) expr.getArray()).getNamedElement() instanceof Field)
				field = (Field) ((Name) expr.getArray()).getNamedElement();
		}
		if (field != null && field.getContainer() != null && field.getContainer() instanceof Type)
				ctx.invoke(genContainerBasedArrayAccess, (Type) field.getContainer(), ctx, out, expr, field);
		else
			genArrayAccess(expr, ctx, out);
	}

	public void genArrayAccess(ArrayAccess expr, Context ctx, TabbedWriter out) {
		if ((expr instanceof ArrayType) && ((ArrayType) expr.getArray().getType()).elementsNullable()) {
			out.print("egl.nullableCheckIndex(");
			ctx.invoke(genExpression, expr.getArray(), ctx, out);
			out.print(", ");
			ctx.invoke(genExpression, expr.getIndex(), ctx, out);
			out.print(")");
		} else {
			out.print("egl.checkNull(");
			ctx.invoke(genCheckNullArgs, expr, ctx, out);
			out.print(")");
			out.print("[");
			ctx.invoke(genExpression, expr.getArray(), ctx, out);
			out.print(".checkIndex(");
			ctx.invoke(genExpression, expr.getIndex(), ctx, out);
			out.print(" - 1)]");
		}
	}
}
