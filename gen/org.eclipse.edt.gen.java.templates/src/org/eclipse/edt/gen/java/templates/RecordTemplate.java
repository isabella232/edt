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

import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class RecordTemplate extends JavaTemplate {

	@SuppressWarnings("unchecked")
	public void validate(Record part, Context ctx, Object... args) {
		// process anything else the superclass needs to do
		ctx.validateSuper(validate, Record.class, part, ctx, args);
		// when we get here, it is because a part is being referenced by the original part being validated. Add it to the
		// parts used table if it doesn't already exist
		boolean found = false;
		List<Record> records = (List<Record>) ctx.getAttribute(ctx.getClass(), Constants.Annotation_partRecordsUsed);
		for (Record record : records) {
			if (part.getTypeSignature().equalsIgnoreCase(record.getTypeSignature())) {
				found = true;
				break;
			}
		}
		if (!found)
			records.add(part);
	}

	public void genConstructor(Record part, Context ctx, TabbedWriter out, Object... args) {
		// Generate RunUnit constructor
		out.print("public ");
		ctx.gen(genClassName, part, ctx, out, args);
		out.print("( Executable ru");
		ctx.gen(genAdditionalConstructorParams, part, ctx, out, args);
		out.println(" ) {");
		out.print("super( ru");
		ctx.gen(genAdditionalSuperConstructorArgs, part, ctx, out, args);
		out.println(" );");
		out.println("ezeInitialize();");
		out.println("}");
		// generate inherited methods
		genConstructorEzeCopy(part, ctx, out, args);
		genConstructorEzeNewValue(part, ctx, out, args);
		genConstructorEzeSetEmpty(part, ctx, out, args);
		genConstructorIsVariableDataLength(part, ctx, out, args);
		genConstructorLoadFromBuffer(part, ctx, out, args);
		genConstructorSizeInBytes(part, ctx, out, args);
		genConstructorStoreInBuffer(part, ctx, out, args);
	}

	protected void genConstructorEzeCopy(Record part, Context ctx, TabbedWriter out, Object... args) {
		out.println("public void ezeCopy(Object source) {");
		out.print("ezeCopy(");
		out.print("(");
		ctx.gen(genClassName, part, ctx, out, args);
		out.println(") source);");
		out.println("}");
		out.println("public void ezeCopy(egl.lang.AnyValue source) {");
		List<Field> fields = part.getFields();
		if (fields != null && fields.size() != 0) {
			for (Field field : fields) {
				if (TypeUtils.isReferenceType(field.getType()) || ctx.mapsToPrimitiveType(field.getType())) {
					out.print("this.");
					ctx.gen(genName, field, ctx, out, args);
					out.print(" = ((");
					ctx.gen(genClassName, part, ctx, out, args);
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
					ctx.gen(genClassName, part, ctx, out, args);
					out.print(") source).");
					ctx.gen(genName, field, ctx, out, args);
					out.println(");");
				} else {
					out.print("this.");
					ctx.gen(genName, field, ctx, out, args);
					out.print(".ezeCopy(");
					out.print("((");
					ctx.gen(genClassName, part, ctx, out, args);
					out.print(") source).");
					ctx.gen(genName, field, ctx, out, args);
					out.println(");");
				}
			}
		}
		out.println("}");
	}

	protected void genConstructorEzeNewValue(Record part, Context ctx, TabbedWriter out, Object... args) {
		out.print("public ");
		ctx.gen(genClassName, part, ctx, out, args);
		out.println(" ezeNewValue(Object... args) {");
		out.print("return new ");
		ctx.gen(genClassName, part, ctx, out, args);
		out.println("(this.ezeProgram);");
		out.println("}");
	}

	protected void genConstructorEzeSetEmpty(Record part, Context ctx, TabbedWriter out, Object... args) {
		out.println("public void ezeSetEmpty() {");
		List<Field> fields = part.getFields();
		if (fields != null && fields.size() != 0) {
			for (Field field : fields) {
				if (TypeUtils.isReferenceType(field.getType()) || ctx.mapsToPrimitiveType(field.getType())) {
					ctx.gen(genName, field, ctx, out, args);
					out.print(" = ");
					ctx.gen(genInitialization, field, ctx, out, args);
					out.println(";");
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
	}

	protected void genConstructorIsVariableDataLength(Record part, Context ctx, TabbedWriter out, Object... args) {
		out.println("public boolean isVariableDataLength() {");
		out.println("return false;");
		out.println("}");
	}

	protected void genConstructorLoadFromBuffer(Record part, Context ctx, TabbedWriter out, Object... args) {
		out.println("public void loadFromBuffer(ByteStorage buffer, Program program) {");
		out.println("}");
	}

	protected void genConstructorSizeInBytes(Record part, Context ctx, TabbedWriter out, Object... args) {
		out.println("public int sizeInBytes() {");
		out.println("return 0;");
		out.println("}");
	}

	protected void genConstructorStoreInBuffer(Record part, Context ctx, TabbedWriter out, Object... args) {
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
		ctx.gen(genPartName, part, ctx, out, args);
	}

	public void genDefaultValue(Record part, Context ctx, TabbedWriter out, Object... args) {
		out.print("(");
		genRuntimeTypeName(part, ctx, out, args);
		out.print(") null");
	}

	public void genSuperClass(Record part, Context ctx, TabbedWriter out, Object... args) {
		Stereotype stereotype = part.getStereotype();
		if (stereotype == null || stereotype.getEClass().getName().equals("BasicRecord"))
			out.print("AnyValue");
		else
			ctx.gen(genSuperClass, stereotype, ctx, out, args);
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
