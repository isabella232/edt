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

import java.util.List;

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class RecordTemplate extends JavaScriptTemplate {

	@SuppressWarnings("unchecked")
	public void preGen(Record part, Context ctx) {
		// process anything else the superclass needs to do
		ctx.invokeSuper(this, preGen, part, ctx);
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
		out.print(quoted("constructor"));
		out.println(": function() {");
		ctx.invoke(genLibraries, part, ctx, out);
		out.println("this.eze$$setInitial();");
		out.println("}");
		out.println(",");

		out.print(quoted("ezeCopy"));
		out.println(": function(source) {");
		for (Field field : part.getFields()) {
			if (TypeUtils.isReferenceType(field.getType()) || ctx.mapsToPrimitiveType(field.getType())) {
				out.print("this.");
				ctx.invoke(genName, field, ctx, out);
				out.print(" = ");
				out.print("source.");
				ctx.invoke(genName, field, ctx, out);
				out.println(";");
			} else {
				out.print("this.");
				ctx.invoke(genName, field, ctx, out);
				out.print(".ezeCopy(");
				out.print("source.");
				ctx.invoke(genName, field, ctx, out);
				out.println(");");
			}
		}
		out.println("}");
	}

	public void genInitializeMethods(EGLClass part, Context ctx, TabbedWriter out) {
		out.println(",");
		out.print(quoted("eze$$setInitial"));
		out.println(": function() {");
		out.println("this.eze$$setEmpty();");
		ctx.invoke(genInitializeMethodBody, part, ctx, out);
		out.println("}");
	}

	public void genInitializeMethod(EGLClass part, Context ctx, TabbedWriter out, Field arg) {
		if (arg.getInitializerStatements() != null) {
			ctx.invoke(genStatementNoBraces, arg.getInitializerStatements(), ctx, out);
		}
	}

	public void genAccessor(Record part, Context ctx, TabbedWriter out) {
		ctx.invoke(genName, part, ctx, out);
	}

	public void genRuntimeTypeName(Record part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genPartName, part, ctx, out);
	}

	public void genDefaultValue(Record part, Context ctx, TabbedWriter out) {
		out.print("null");
	}

	public void genSuperClass(Record type, Context ctx, TabbedWriter out) {
		// TODO: make a constant
		out.print(quoted("egl.jsrt"));
		out.print(", ");
		// TODO: make a constant
		out.print(quoted("Record"));
	}

	public void genAssignment(Record type, Context ctx, TabbedWriter out, Expression arg1, Expression arg2, String arg3) {
		/*
		 * TODO sbg Java has all of this, but I'm not sure whether it -- or some form of it -- is necessary.... if (
		 * TypeUtils.isValueType( type ) ) { if (arg1.isNullable()) { ctx.invoke(genExpression, arg1, ctx, out);
		 * out.print(arg3); } out.print("egl.egl.lang.AnyValue.ezeCopyTo("); ctx.invoke(genExpression, arg2, ctx, out);
		 * out.print(", "); ctx.invoke(genExpression, arg1, ctx, out); out.print(")"); } else
		 */
		{
			ctx.invoke(genExpression, arg1, ctx, out);
			out.print(" = ");
			ctx.invoke(genExpression, arg2, ctx, out);
		}
	}

	public void genGetterSetters(Record part, Context ctx, TabbedWriter out) {}
}
