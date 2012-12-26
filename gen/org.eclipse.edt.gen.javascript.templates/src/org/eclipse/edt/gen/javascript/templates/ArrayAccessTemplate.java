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
		Expression array = expr.getArray();
		Expression index = expr.getIndex();
		boolean arrayIsAny = TypeUtils.getTypeKind(array.getType()) == TypeUtils.TypeKind_ANY;

		out.print("egl.setElement(");
		if (arrayIsAny) {
			out.print("egl.unboxAny(");
		}
		if (array.isNullable()) {
			out.print("egl.checkNull(");
			ctx.invoke(genCheckNullArgs, expr, ctx, out);
			out.print(")");
		} else {
			ctx.invoke(genExpression, array, ctx, out);
		}
		if (arrayIsAny) {
			out.print(")");
		}
		out.print(", ");
		ctx.invoke(genExpression, index, ctx, out);
		out.print(" - 1, ");
		if (TypeUtils.getTypeKind(expr.getType()) != TypeUtils.TypeKind_ANY 
				&& TypeUtils.getTypeKind(expr.getType()) != TypeUtils.TypeKind_NUMBER
				&& (TypeUtils.isReferenceType(expr.getType()) || ctx.mapsToPrimitiveType(expr.getType()))) {
			out.print("egl.unboxAny(");
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(")");
		} else {
			TypeTemplate.assignmentSource(expr, arg1, ctx, out);
		}
		out.print(")");
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
		Expression array = expr.getArray();
		Expression index = expr.getIndex();
		boolean nullableElements = 
				array.getType() instanceof ArrayType 
				&& ((ArrayType)array.getType()).elementsNullable();
		boolean arrayIsAny = TypeUtils.getTypeKind(array.getType()) == TypeUtils.TypeKind_ANY;

		if (nullableElements) {
			out.print("egl.nullableGetElement(");
			ctx.invoke(genExpression, array, ctx, out);
			out.print(", ");
			ctx.invoke(genExpression, index, ctx, out);
			out.print(")");
		} else {
			out.print("egl.getElement(");
			if (arrayIsAny) {
				out.print("egl.unboxAny(");
			}
			if (array.isNullable()) {
				out.print("egl.checkNull(");
				ctx.invoke(genCheckNullArgs, expr, ctx, out);
				out.print(")");
			} else {
				ctx.invoke(genExpression, array, ctx, out);
			}
			if (arrayIsAny) {
				out.print(")");
			}
			out.print(", ");
			ctx.invoke(genExpression, index, ctx, out);
			out.print(" - 1)");
		}
	}
}
