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
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class RecordTemplate extends JavascriptTemplate {

	public void genConstructor(Record part, Context ctx, TabbedWriter out, Object... args) {
		out.print(quoted("ezeCopy"));
		out.println(": function(source) {");
		for (Field field : part.getFields()) {
			if (TypeUtils.isReferenceType(field.getType()) || ctx.mapsToPrimitiveType(field.getType())) {
				out.print("this.");
				ctx.gen(genName, field, ctx, out, args);
				out.print(" = ((");
				ctx.gen(genClassName, part, ctx, out, args);
				out.print(") source).");
				ctx.gen(genName, field, ctx, out, args);
				out.println(";");
			} else {
				out.print("this.");
				ctx.gen(genName, field, ctx, out, args);
				out.print(".ezeCopy(");
				out.print("source.");
				ctx.gen(genName, field, ctx, out, args);
				out.println(");");
			}
		}
		out.println("};");
	}

	public void genAccessor(Record part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genName, part, ctx, out, args);
	}

	public void genRuntimeTypeName(Record part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genPartName, part, ctx, out, args);
	}

	public void genDefaultValue(Record part, Context ctx, TabbedWriter out, Object... args) {
		out.print("(");
		genRuntimeTypeName(part, ctx, out, args);
		out.print(") null");
	}

	public void genSuperClass(Record type, Context ctx, TabbedWriter out, Object... args) {
		// TODO: make a constant
		out.print(quoted("egl.jsrt"));
		out.print(", ");
		// TODO: make a constant
		out.print(quoted("Record"));
	}

	public void genAssignment(Record type, Context ctx, TabbedWriter out, Object... args) {
		if (((Expression) args[0]).isNullable()) {
			ctx.gen(genExpression, (Expression) args[0], ctx, out, args);
			out.print(" = ");
		}
		out.print("org.eclipse.edt.runtime.java.egl.lang.AnyValue.ezeCopyTo(");
		ctx.gen(genExpression, (Expression) args[1], ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, (Expression) args[0], ctx, out, args);
		out.print(")");
	}

	public void genGetterSetter(Record part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genGetter, (Field) args[0], ctx, out, args);
		ctx.gen(genSetter, (Field) args[0], ctx, out, args);
	}
}
