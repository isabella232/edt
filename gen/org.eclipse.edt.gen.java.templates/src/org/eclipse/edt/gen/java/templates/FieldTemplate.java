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
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ExternalType;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class FieldTemplate extends JavaTemplate {

	public void validate(Field field, Context ctx, Object... args) {
		ctx.validate(validate, field.getType(), ctx, args);
	}

	public void genDeclaration(Field field, Context ctx, TabbedWriter out, Object... args) {
		// write out the debug extension data
		CommonUtilities.generateDebugExtension(field, ctx);
		// process the field
		ctx.genSuper(genDeclaration, Field.class, field, ctx, out, args);
		transientOption(field, out, args);
		ctx.gen(genRuntimeTypeName, field, ctx, out, args);
		out.print(" ");
		ctx.gen(genName, field, ctx, out, args);
		out.println(";");
	}

	public void genInstantiation(Field field, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInstantiation, field.getType(), ctx, out, field);
	}

	public void genInitialization(Field field, Context ctx, TabbedWriter out, Object... args) {
		// is this an inout or out temporary variable to a function. if so, then we need to default or instantiate for
		// our parms, and set to null for inout
		if (ctx.getAttribute(field, Constants.Annotation_functionArgumentTemporaryVariable) != null
			&& ((Integer) ctx.getAttribute(field, Constants.Annotation_functionArgumentTemporaryVariable)).intValue() != 0) {
			// if the value associated with the temporary variable is 2, then it is to be instantiated (OUT parm)
			// otherwise it is to be defaulted to null (INOUT parm), as there is an assignment already created
			if (((Integer) ctx.getAttribute(field, Constants.Annotation_functionArgumentTemporaryVariable)).intValue() == 2) {
				out.print("AnyObject.ezeWrap(");
				if (ctx.mapsToNativeType(field.getType()) || ctx.mapsToPrimitiveType(field.getType()))
					ctx.gen(genDefaultValue, field.getType(), ctx, out, field);
				else
					ctx.gen(genInstantiation, field.getType(), ctx, out, field);
				out.print(")");
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
		out.print("public ");
		ctx.gen(genRuntimeTypeName, field, ctx, out, args);
		out.print(" get");
		genMethodName(field, ctx, out, args);
		out.println("() {");
		out.print("return (");
		out.print(field.getName());
		out.println(");");
		out.println("}");
	}

	public void genSetter(Field field, Context ctx, TabbedWriter out, Object... args) {
		out.print("public void set");
		genMethodName(field, ctx, out, args);
		out.print("( ");
		ctx.gen(genRuntimeTypeName, field, ctx, out, args);
		out.println(" ezeValue ) {");
		out.print("this.");
		out.print(field.getName());
		out.println(" = ezeValue;");
		out.println("}");
	}

	protected void genMethodName(Field field, Context ctx, TabbedWriter out, Object... args) {
		out.print(field.getName().substring(0, 1).toUpperCase());
		if (field.getName().length() > 1)
			out.print(field.getName().substring(1));
	}

	protected void transientOption(Field field, TabbedWriter out, Object... args) {
		ExternalType et = CommonUtilities.getUserDefinedExternalType(field.getType());
		if (et != null && !CommonUtilities.isSerializable(et))
			out.print("transient ");
	}
}
