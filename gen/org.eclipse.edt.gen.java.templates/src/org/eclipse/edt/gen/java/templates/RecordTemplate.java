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

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.FunctionMember;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.ReturnStatement;
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
		out.println("public void ezeCopy(eglx.lang.AnyValue source) {");
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
					out.print("this.");
					ctx.invoke(genName, field, ctx, out);
					out.print(" = ");
					out.print("org.eclipse.edt.runtime.java.eglx.lang.AnyValue.ezeCopyTo(");
					out.print("((");
					ctx.invoke(genClassName, part, ctx, out);
					out.print(") source).");
					ctx.invoke(genName, field, ctx, out);
					out.print(", ");
					out.print("this.");
					ctx.invoke(genName, field, ctx, out);
					out.print(")");
					out.println(";");
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
					out.println(" != null)");
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

	public void genSuperClass(Record part, Context ctx, TabbedWriter out) {
		out.print("org.eclipse.edt.runtime.java.eglx.lang.AnyValue");
	}

	public void genAssignment(Record type, Context ctx, TabbedWriter out, Expression arg1, Expression arg2, String arg3) {
		if (TypeUtils.isValueType(type)) {
			// because of initialization logic for field declarations, we must always assign as well as ezecopyto
			// otherwise, if a null is passed in as the to arg (2nd) and it doesn't get updated on return
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(arg3);
			CommonUtilities.genEzeCopyTo(arg2, ctx, out);
			ctx.invoke(genExpression, arg2, ctx, out);
			out.print(", ");
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(")");
		} else {
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(arg3);
			ctx.invoke(genExpression, arg2, ctx, out);
		}
	}

	public void genReturnStatement(Record type, Context ctx, TabbedWriter out, ReturnStatement arg) {
		if (TypeUtils.isValueType(type) && arg.getExpression() != null) {
			String temporary = ctx.nextTempName();
			ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.JavaPrimitive);
			out.println(" " + temporary + " = null;");
			out.print("return (");
			CommonUtilities.genEzeCopyTo(arg.getExpression(), ctx, out);
			ctx.invoke(genExpression, IRUtils.makeExprCompatibleToType(arg.getExpression(), ((FunctionMember) arg.getContainer()).getType()), ctx, out);
			out.print(", ");
			out.print(temporary);
			out.print(")");
			out.print(")");
		} else
			ctx.invoke(genReturnStatement, arg, ctx, out);
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
