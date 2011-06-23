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
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class RecordTemplate extends JavaScriptTemplate {

	@SuppressWarnings("unchecked")
	public void validate(Record part, Context ctx, Object... args) {
		// process anything else the superclass needs to do
		ctx.validateSuper(validate, Record.class, part, ctx, args);
		// ignore adding this entry to the list, if it is the part we are currently generating
		if (((Part) ctx.getAttribute(ctx.getClass(), Constants.Annotation_partBeingGenerated)).getFullyQualifiedName().equalsIgnoreCase(
			part.getFullyQualifiedName()))
			return;
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
		out.print(quoted("constructor"));
		out.println(": function() {");
		out.println("this.eze$$setInitial();");
		out.println("}");
		out.println(",");

		out.print(quoted("ezeCopy"));
		out.println(": function(source) {");
		for (Field field : part.getFields()) {
			if (TypeUtils.isReferenceType(field.getType()) || ctx.mapsToPrimitiveType(field.getType())) {
				out.print("this.");
				ctx.gen(genName, field, ctx, out, args);
				out.print(" = ");
				out.print("source.");
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
		out.println("}");
	}

	public void genInitializeMethods(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		out.print(quoted("eze$$setInitial"));
		out.println(": function() {");
		out.println("this.eze$$setEmpty();");
		ctx.gen(genInitializeMethodBody, part, ctx, out, args);
		out.println("}");
	}

	public void genInitializeMethod(EGLClass part, Context ctx, TabbedWriter out, Object... args) {
		if (((Field) args[0]).getInitializerStatements() != null) {
			out.print("this."); // TODO sbg likely NOT the right place
			ctx.gen(genStatementNoBraces, ((Field) args[0]).getInitializerStatements(), ctx, out, args);
		}
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
		ctx.gen(genExpression, (Expression) args[0], ctx, out, args);
		out.print(".ezeCopy(");
		ctx.gen(genExpression, (Expression) args[1], ctx, out, args);
		out.print(")");
	}

	public void genGetterSetters(Record part, Context ctx, TabbedWriter out, Object... args) {}

	public void genQualifier(Library library, Context ctx, TabbedWriter out, Object... args) {
		if ((args.length > 0) && (args[0] instanceof Expression) && (((Expression) args[0]).getQualifier() == null))
			out.print("this.");
	}
}
