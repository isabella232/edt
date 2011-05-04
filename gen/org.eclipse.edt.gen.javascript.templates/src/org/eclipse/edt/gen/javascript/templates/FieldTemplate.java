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
import org.eclipse.edt.mof.egl.Container;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.ParameterKind;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class FieldTemplate extends JavaScriptTemplate {

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

	public void genQualifier(Field field, Context ctx, TabbedWriter out, Object... args) {
		final Container cnr = field.getContainer();
		if (cnr != null) {
			ctx.gen(genQualifier, cnr, ctx, out, args);
		}
	}

	public void genInstantiation(Field field, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInstantiation, field.getType(), ctx, out, field);
	}

	public void genInitialization(Field field, Context ctx, TabbedWriter out, Object... args) {
		// is this an inout or out temporary variable to a function. if so, then we need to default or instantiate for
		// our parms, and set to null for inout
		if (ctx.getAttribute(field, org.eclipse.edt.gen.Constants.Annotation_functionArgumentTemporaryVariable) != null
			&& ctx.getAttribute(field, org.eclipse.edt.gen.Constants.Annotation_functionArgumentTemporaryVariable) != ParameterKind.PARM_IN) {
			// if the value associated with the temporary variable is 2, then it is to be instantiated (OUT parm)
			// otherwise it is to be defaulted to null (INOUT parm), as there is an assignment already created
			if (ctx.getAttribute(field, org.eclipse.edt.gen.Constants.Annotation_functionArgumentTemporaryVariable) == ParameterKind.PARM_OUT) {
				if (ctx.mapsToNativeType(field.getType()) || ctx.mapsToPrimitiveType(field.getType()))
					ctx.gen(genDefaultValue, field.getType(), ctx, out, field);
				else
					ctx.gen(genInstantiation, field.getType(), ctx, out, field);
			} else
				out.print("null");
		} else {
			if (field.isNullable() || TypeUtils.isReferenceType(field.getType()))
				ctx.gen(genDefaultValue, field.getType(), ctx, out, field);
			else if (ctx.mapsToNativeType(field.getType()) || ctx.mapsToPrimitiveType(field.getType()))
				ctx.gen(genDefaultValue, field.getType(), ctx, out, field);
			else
				ctx.gen(genInstantiation, field.getType(), ctx, out, field);
		}
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
