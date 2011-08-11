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
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Field;

public class ArrayTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(ArrayType generic, Context ctx, TabbedWriter out, Field arg) {
		if (arg.getInitializerStatements() != null)
			processDefaultValue(generic, ctx, out);
		else
			out.print("null");
	}

	public void genDefaultValue(ArrayType generic, Context ctx, TabbedWriter out) {
		if (generic.getInitialSize() != null)
			processDefaultValue(generic, ctx, out);
		else
			out.print("null");
	}

	public void processDefaultValue(ArrayType generic, Context ctx, TabbedWriter out) {
		String temporary = ctx.nextTempName();
		out.print("(function() { var ");
		out.print(temporary);
		out.print(" = []; ");
		out.print(temporary);
		out.print(".setType(");
		out.print("\"");
		genSignature(generic, ctx, out);
		out.print("\"");
		if (generic.getInitialSize() == null)
			out.println(");");
		else {
			out.println("); for (var i = 0; i < " + generic.getInitialSize() + "; i++) {");
			out.print(temporary);
			out.print("[i] = ");
			ctx.invoke(genDefaultValue, generic.getElementType(), ctx, out);
			out.println(";}");
		}
		out.print("return ");
		out.print(temporary);
		out.print(";})()");
	}

	public void genSignature(ArrayType generic, Context ctx, TabbedWriter out) {
		if (!generic.getTypeArguments().isEmpty()) {
			for (int i = 0; i < generic.getTypeArguments().size(); i++)
				out.print("[");
		}
		if (generic.elementsNullable())
			out.print("?");
		ctx.invoke(genSignature, generic.getElementType(), ctx, out);
	}

	public void genRuntimeTypeName(ArrayType generic, Context ctx, TabbedWriter out, TypeNameKind arg) {
		out.print("Array");
	}

	public void genFieldInfoTypeName(ArrayType generic, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genFieldInfoTypeName, generic.getElementType(), ctx, out, TypeNameKind.JavascriptImplementation);
	}

	public void genConversionOperation(ArrayType type, Context ctx, TabbedWriter out, AsExpression arg) {
		ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
	}
}
