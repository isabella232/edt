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

import java.util.List;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class RecordTemplate extends ClassTemplate {

	public void genSuperClass(Record part, Context ctx, TabbedWriter out, Object... args) {
		Stereotype stereotype = part.getStereotype();
		if (stereotype == null || stereotype.getEClass().getName().equals("BasicRecord"))
			out.print("AnyValue");
		else
			ctx.gen(genSuperClass, stereotype, ctx, out, args);
	}

	public void genConstructor(Record part, Context ctx, TabbedWriter out, Object... args) {
		// Generate RunUnit constructor
		out.print("public ");
		genClassName(part, ctx, out, args);
		out.print("( Executable ru");
		genAdditionalConstructorParams(part, out, args);
		out.println(" ) {");
		out.print("super( ru");
		genAdditionalSuperConstructorArgs(part, out, args);
		out.println(" );");
		out.println("ezeInitialize();");
		out.println('}');

		// generate inherited methods
		out.println("@Override");
		out.println("public void ezeCopy(Object source) {");
		out.print("ezeCopy(");
		out.print("(");
		genClassName(part, ctx, out, args);
		out.println(") source);");
		out.println("}");
		out.println("@Override");
		out.println("public void ezeCopy(egl.lang.AnyValue source) {");
		List<Field> fields = part.getFields();
		if (fields != null && fields.size() != 0) {
			for (Field field : fields) {
				if (TypeUtils.isReferenceType(field.getType()) || ctx.mapsToPrimitiveType(field.getType())) {
					out.print("this.");
					ctx.gen(genName, field, ctx, out, args);
					out.print(" = ((");
					genClassName(part, ctx, out, args);
					out.print(") source).");
					ctx.gen(genName, field, ctx, out, args);
					out.println(";");
				} else if (field.isNullable()) {
					out.print("if (");
					ctx.gen(genName, field, ctx, out, args);
					out.println(" == null)");
					out.print("this.");
					ctx.gen(genName, field, ctx, out, args);
					out.print(".ezeCopy(");
					out.print("((");
					genClassName(part, ctx, out, args);
					out.print(") source).");
					ctx.gen(genName, field, ctx, out, args);
					out.println(");");
				} else {
					out.print("this.");
					ctx.gen(genName, field, ctx, out, args);
					out.print(".ezeCopy(");
					out.print("((");
					genClassName(part, ctx, out, args);
					out.print(") source).");
					ctx.gen(genName, field, ctx, out, args);
					out.println(");");
				}
			}
		}
		out.println("}");
		out.println("@Override");
		out.print("public ");
		genClassName(part, ctx, out, args);
		out.println(" ezeNewValue(Object... args) {");
		out.print("return new ");
		genClassName(part, ctx, out, args);
		out.println("(this.ezeProgram);");
		out.println("}");
		out.println("@Override");
		out.println("public void ezeSetEmpty() {");
		if (fields != null && fields.size() != 0) {
			for (Field field : fields) {
				if (TypeUtils.isReferenceType(field.getType()) || ctx.mapsToPrimitiveType(field.getType())) {
					ctx.gen(genName, field, ctx, out, args);
					out.print(" = ");
					ctx.gen(genInitialization, field, ctx, out, args);
					out.println(';');
				} else if (field.isNullable()) {
					out.print("if (");
					ctx.gen(genName, field, ctx, out, args);
					out.println(" == null)");
					out.print("this.");
					ctx.gen(genName, field, ctx, out, args);
					out.print(".ezeSetEmpty(");
					out.println(");");
				} else {
					out.print("this.");
					ctx.gen(genName, field, ctx, out, args);
					out.print(".ezeSetEmpty(");
					out.println(");");
				}
			}
		}
		out.println("}");
		out.println("@Override");
		out.println("public boolean isVariableDataLength() {");
		out.println("return false;");
		out.println("}");
		out.println("@Override");
		out.println("public void loadFromBuffer(ByteStorage buffer, Program program) {");
		out.println("}");
		out.println("@Override");
		out.println("public int sizeInBytes() {");
		out.println("return 0;");
		out.println("}");
		out.println("@Override");
		out.println("public void storeInBuffer(ByteStorage buffer) {");
		out.println("}");
	}

	public void genConstructorOptions(Record part, Context ctx, TabbedWriter out, Object... args) {
		out.print("ezeProgram");
	}

	public void genAccessor(Record part, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genName, part, ctx, out, args);
	}

	public void genRuntimeTypeName(Record part, Context ctx, TabbedWriter out, Object... args) {
		genPartName(part, ctx, out, args);
	}

	public void genDefaultValue(Record part, Context ctx, TabbedWriter out, Object... args) {
		out.print("(");
		genRuntimeTypeName(part, ctx, out, args);
		out.print(") null");
	}

	public void genGetterSetter(Record part, Context ctx, TabbedWriter out, Object... args) {
		genSimpleGetter(part, ctx, out, args);
		genSimpleSetter(part, ctx, out, args);
	}

	private void genSimpleGetter(Record part, Context ctx, TabbedWriter out, Object... args) {
		out.print("public ");
		genRuntimeTypeName(part, ctx, out, args);
		out.print(" ");
		genGetMethodName((Field) args[0], ctx, out, args);
		out.println("() {");
		genReturnStatement((Field) args[0], ctx, out, args);
		out.println("}");
	}

	private void genSimpleSetter(Record part, Context ctx, TabbedWriter out, Object... args) {
		out.print("public void ");
		genSetMethodName((Field) args[0], ctx, out, args);
		out.print("( ");
		genRuntimeTypeName(part, ctx, out, args);
		out.println(" ezeValue ) {");
		genSetStatement((Field) args[0], ctx, out, args);
		out.println("}");
	}

	private void genGetMethodName(Field part, Context ctx, TabbedWriter out, Object... args) {
		out.print("get");
		out.print(part.getName().substring(0, 1).toUpperCase());
		if (part.getName().length() > 1)
			out.print(part.getName().substring(1));
	}

	private void genSetMethodName(Field part, Context ctx, TabbedWriter out, Object... args) {
		out.print("set");
		out.print(part.getName().substring(0, 1).toUpperCase());
		if (part.getName().length() > 1)
			out.print(part.getName().substring(1));
	}

	private void genReturnStatement(Field part, Context ctx, TabbedWriter out, Object... args) {
		out.print("return (");
		out.print(part.getName());
		out.println(");");
	}

	private void genSetStatement(Field part, Context ctx, TabbedWriter out, Object... args) {
		out.print("this.");
		out.print(part.getName());
		out.println(" = ezeValue;");
	}
}
