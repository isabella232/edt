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
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.Stereotype;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class RecordTemplate extends JavaTemplate {

	@SuppressWarnings("unchecked")
	public void preGen(Record part, Context ctx) {
		// process anything else the superclass needs to do
		ctx.invokeSuper(this, preGen, part, ctx);
		// Add SMAP file
		ctx.setCurrentFile(IRUtils.getQualifiedFileName(part));
		// ignore adding this entry to the list, if it is the part we are currently generating
		if (((Part) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partBeingGenerated)).getFullyQualifiedName().equalsIgnoreCase(
			part.getFullyQualifiedName()))
			return;
		// when we get here, it is because a part is being referenced by the original part being generated. Add it to the
		// parts used table if it doesn't already exist
		boolean found = false;
		List<Record> records = (List<Record>) ctx.getAttribute(ctx.getClass(), Constants.SubKey_partRecordsUsed);
		for (Record record : records) {
			if (part.getTypeSignature().equalsIgnoreCase(record.getTypeSignature())) {
				found = true;
				break;
			}
		}
		if (!found)
			records.add(part);
	}

	public void genConstructor(Record part, Context ctx, TabbedWriter out) {
		// Generate RunUnit constructor
		out.print("public ");
		ctx.invoke(genClassName, part, ctx, out);
		out.print("(");
		ctx.invoke(genAdditionalConstructorParams, part, ctx, out);
		out.println(") {");
		out.print("super(");
		ctx.invoke(genAdditionalSuperConstructorArgs, part, ctx, out);
		out.println(");");
		out.println("ezeInitialize();");
		out.println("}");
		// generate inherited methods
		genConstructorEzeCopy(part, ctx, out);
		genConstructorEzeNewValue(part, ctx, out);
		genConstructorEzeSetEmpty(part, ctx, out);
		genConstructorIsVariableDataLength(part, ctx, out);
		genConstructorLoadFromBuffer(part, ctx, out);
		genConstructorSizeInBytes(part, ctx, out);
		genConstructorStoreInBuffer(part, ctx, out);
	}

	protected void genConstructorEzeCopy(Record part, Context ctx, TabbedWriter out) {
		out.println("public void ezeCopy(Object source) {");
		out.print("ezeCopy(");
		out.print("(");
		ctx.invoke(genClassName, part, ctx, out);
		out.println(") source);");
		out.println("}");
		out.println("public void ezeCopy(egl.lang.AnyValue source) {");
		List<Field> fields = part.getFields();
		if (fields != null && fields.size() != 0) {
			for (Field field : fields) {
				if (TypeUtils.isReferenceType(field.getType()) || ctx.mapsToPrimitiveType(field.getType())) {
					out.print("this.");
					ctx.invoke(genName, field, ctx, out);
					out.print(" = ((");
					ctx.invoke(genClassName, part, ctx, out);
					out.print(") source).");
					ctx.invoke(genName, field, ctx, out);
					out.println(";");
				} else if (field.isNullable()) {
					out.print("if (");
					ctx.invoke(genName, field, ctx, out);
					out.println(" == null)");
					out.print("this.");
					ctx.invoke(genName, field, ctx, out);
					out.print(".ezeCopy(");
					out.print("((");
					ctx.invoke(genClassName, part, ctx, out);
					out.print(") source).");
					ctx.invoke(genName, field, ctx, out);
					out.println(");");
				} else {
					out.print("this.");
					ctx.invoke(genName, field, ctx, out);
					out.print(".ezeCopy(");
					out.print("((");
					ctx.invoke(genClassName, part, ctx, out);
					out.print(") source).");
					ctx.invoke(genName, field, ctx, out);
					out.println(");");
				}
			}
		}
		out.println("}");
	}

	protected void genConstructorEzeNewValue(Record part, Context ctx, TabbedWriter out) {
		out.print("public ");
		ctx.invoke(genClassName, part, ctx, out);
		out.println(" ezeNewValue(Object... args) {");
		out.print("return new ");
		ctx.invoke(genClassName, part, ctx, out);
		out.println("();");
		out.println("}");
	}

	protected void genConstructorEzeSetEmpty(Record part, Context ctx, TabbedWriter out) {
		out.println("public void ezeSetEmpty() {");
		List<Field> fields = part.getFields();
		if (fields != null && fields.size() != 0) {
			for (Field field : fields) {
				if (TypeUtils.isReferenceType(field.getType()) || ctx.mapsToPrimitiveType(field.getType())) {
					ctx.invoke(genName, field, ctx, out);
					out.print(" = ");
					ctx.invoke(genInitialization, field, ctx, out);
					out.println(";");
				} else if (field.isNullable()) {
					out.print("if (");
					ctx.invoke(genName, field, ctx, out);
					out.println(" == null)");
					out.print("this.");
					ctx.invoke(genName, field, ctx, out);
					out.print(".ezeSetEmpty(");
					out.println(");");
				} else {
					out.print("this.");
					ctx.invoke(genName, field, ctx, out);
					out.print(".ezeSetEmpty(");
					out.println(");");
				}
			}
		}
		out.println("}");
	}

	protected void genConstructorIsVariableDataLength(Record part, Context ctx, TabbedWriter out) {
		out.println("public boolean isVariableDataLength() {");
		out.println("return false;");
		out.println("}");
	}

	protected void genConstructorLoadFromBuffer(Record part, Context ctx, TabbedWriter out) {
		out.println("public void loadFromBuffer(ByteStorage buffer, Program program) {");
		out.println("}");
	}

	protected void genConstructorSizeInBytes(Record part, Context ctx, TabbedWriter out) {
		out.println("public int sizeInBytes() {");
		out.println("return 0;");
		out.println("}");
	}

	protected void genConstructorStoreInBuffer(Record part, Context ctx, TabbedWriter out) {
		out.println("public void storeInBuffer(ByteStorage buffer) {");
		out.println("}");
	}

	public void genConstructorOptions(Record part, Context ctx, TabbedWriter out) {}

	public void genAccessor(Record part, Context ctx, TabbedWriter out) {
		ctx.invoke(genName, part, ctx, out);
	}

	public void genRuntimeTypeName(Record part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genPartName, part, ctx, out);
	}

	public void genDefaultValue(Record part, Context ctx, TabbedWriter out) {
		out.print("(");
		genRuntimeTypeName(part, ctx, out, TypeNameKind.JavaPrimitive);
		out.print(") null");
	}

	public void genSuperClass(Record part, Context ctx, TabbedWriter out) {
		Stereotype stereotype = part.getStereotype();
		if (stereotype == null || stereotype.getEClass().getName().equals("BasicRecord"))
			out.print("org.eclipse.edt.runtime.java.egl.lang.AnyValue");
		else
			ctx.invoke(genSuperClass, stereotype, ctx, out);
	}

	public void genAssignment(Record type, Context ctx, TabbedWriter out, Expression arg1, Expression arg2, String arg3) {
		if (TypeUtils.isValueType(type)) {
			if (arg1.isNullable()) {
				ctx.invoke(genExpression, arg1, ctx, out);
				out.print(arg3);
			}
			out.print("org.eclipse.edt.runtime.java.egl.lang.AnyValue.ezeCopyTo(");
			ctx.invoke(genExpression, arg2, ctx, out);
			out.print(", ");
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(")");
		} else {
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(" = ");
			ctx.invoke(genExpression, arg2, ctx, out);
		}
	}

	public void genGetterSetter(Record part, Context ctx, TabbedWriter out, Field arg) {
		ctx.invoke(genGetter, arg, ctx, out);
		ctx.invoke(genSetter, arg, ctx, out);
	}

	public void genXmlTransient(Record part, TabbedWriter out) {
		out.println("@javax.xml.bind.annotation.XmlTransient");
	}

	public void genAnnotations(Record part, Context ctx, TabbedWriter out, Field field) {}
}
