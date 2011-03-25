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
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Record;

public class RecordTemplate extends ClassTemplate {

	
	@Override
	public void genClassBody(EGLClass part, Context ctx, TabbedWriter out,
			Object... args) {
		super.genClassBody(part, ctx, out, args);
		out.println(',');
		genEzeCopyMethod((Record)part, ctx, out, args);
	}

	
	public void genEzeCopyMethod(Record part, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		out.print(quoted("ezeCopy"));
		out.println(": function(source) {");
		for (Field field : part.getFields()) {
			if (isReferenceType(field.getType()) || ctx.mapsToPrimitiveType(field.getType())) {
				out.print("this.");
				genName(field, ctx, out, args);
				out.print(" = ((");
				genClassName(part, out, args);
				out.print(") source).");
				genName(field, ctx, out, args);
				out.println(";");
			} 
			else {
				out.print("this.");
				genName(field, ctx, out, args);
				out.print(".ezeCopy(");
				out.print("source.");
				genName(field, ctx, out, args);
				out.println(");");
			}
		}
		out.println("};");
	}
	
	public void genSuperTypePackageName(Part type, Context ctx, TabbedWriter out, Object... args) {
		// TODO: make a constant
		out.print(quoted("egl.jsrt"));
	}
	public void genSuperTypeClassName(Part type, Context ctx, TabbedWriter out, Object... args) {
		// TODO: make a constant
		out.print(quoted("Record"));
	}

	public void genGetterSetter(Record part, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		genSimpleGetter(part, ctx, out, args);
		genSimpleSetter(part, ctx, out, args);
	}

	private void genSimpleGetter(Record part, Context ctx, TabbedWriter out, Object... args) {
		genGetMethodHeader((Field) args[0], ctx, out, args);
		genReturnStatement((Field) args[0], ctx, out, args);
		out.println("}");
	}

	private void genSimpleSetter(Record part, Context ctx, TabbedWriter out, Object... args) {
		genSetMethodHeader((Field) args[0], ctx, out, args);
		genSetStatement((Field) args[0], ctx, out, args);
		out.println("}");
	}

	private void genGetMethodHeader(Field part, Context ctx, TabbedWriter out, Object... args) {
		StringBuilder name = new StringBuilder();
		name.append("get");
		name.append(part.getName().substring(0, 1).toUpperCase());
		if (part.getName().length() > 1)
			name.append(part.getName().substring(1));
		out.print(quoted(name.toString()));
		out.println(": function() {");
	}

	private void genSetMethodHeader(Field part, Context ctx, TabbedWriter out, Object... args) {
		StringBuilder name = new StringBuilder();
		name.append("set");
		name.append(part.getName().substring(0, 1).toUpperCase());
		if (part.getName().length() > 1)
			name.append(part.getName().substring(1));
		out.print(quoted(name.toString()));
		out.println(": function(ezeValue) {");
	}

	private void genReturnStatement(Field field, Context ctx, TabbedWriter out, Object... args) {
		out.print("return ");
		genName(field, ctx, out, args);
		out.println(";");
	}

	private void genSetStatement(Field field, Context ctx, TabbedWriter out, Object... args) {
		out.print("this.");
		genName(field, ctx, out, args);
		out.println(" = ezeValue;");
	}
}
