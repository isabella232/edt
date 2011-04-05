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
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class FieldTemplate extends JavascriptTemplate {

	public void validate(Field field, Context ctx, Object... args) {
		ctx.validate(validate, field.getType(), ctx, args);
	}

	public void genDeclaration(Field field, Context ctx, TabbedWriter out, Object... args) {
		// process the field
		ctx.genSuper(genDeclaration, Field.class, field, ctx, out, args);
		ctx.gen(genRuntimeTypeName, field, ctx, out, args);
		out.print(" ");
		ctx.gen(genName, field, ctx, out, args);
		out.println(";");
	}

	public void genInstantiation(Field field, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInstantiation, field.getType(), ctx, out, field);
	}

	public void genInitialization(Field field, Context ctx, TabbedWriter out, Object... args) {
		if (field.isNullable() || TypeUtils.isReferenceType(field.getType()))
			out.print("null");
		else if (ctx.mapsToPrimitiveType(field.getType().getClassifier()))
			ctx.gen(genDefaultValue, field.getType(), ctx, out, field);
		else
			ctx.gen(genInstantiation, field.getType(), ctx, out, field);
	}

	public void genGetter(Field field, Context ctx, TabbedWriter out, Object... args) {
		StringBuilder name = new StringBuilder();
		name.append("get");
		name.append(field.getName().substring(0, 1).toUpperCase());
		if (field.getName().length() > 1)
			name.append(field.getName().substring(1));
		out.print(quoted(name.toString()));
		out.println(": function() {");
		out.print("return ");
		ctx.gen(genName, field, ctx, out, args);
		out.println(";");
		out.println("}");
	}

	public void genSetter(Field field, Context ctx, TabbedWriter out, Object... args) {
		StringBuilder name = new StringBuilder();
		name.append("set");
		name.append(field.getName().substring(0, 1).toUpperCase());
		if (field.getName().length() > 1)
			name.append(field.getName().substring(1));
		out.print(quoted(name.toString()));
		out.println(": function(ezeValue) {");
		out.print("this.");
		ctx.gen(genName, field, ctx, out, args);
		out.println(" = ezeValue;");
		out.println("}");
	}
}
