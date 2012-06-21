/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Type;

public class EnumerationTemplate extends JavaTemplate {

	public void preGenClassBody(Enumeration part, Context ctx) {}

	public void genImports(Part part, Context ctx, TabbedWriter out) {}
	
	public void genClassBody(Enumeration part, Context ctx, TabbedWriter out) {
		// generate enumerated fields
		List<EEnumLiteral> enums = part.getEntries();
		if (enums != null && enums.size() != 0) {
			boolean needsSeparator = false;
			for (EEnumLiteral literal : enums) {
				if (needsSeparator)
					out.println(",");
				needsSeparator = true;
				ctx.invoke(genName, literal, ctx, out);
				int value = literal.getValue();
				out.print("(" + value + ")");
			}
			out.println(";");
		}
		out.println("private final int value;");
		ctx.invoke(genConstructors, (Type)part, ctx, out);
		out.println("public int getValue() {");
		out.println("\treturn value;");
		out.println("}");
	}

	public void genConstructors(Enumeration part, Context ctx, TabbedWriter out) {
		out.print("private ");
		ctx.invoke(genClassName, (Type) part, ctx, out);
		out.println("(int value) {");
		out.println("\tthis.value = value;");
		out.println("}");
		//the runtime needs to use reflection so an enum must have a default constructor
		out.print("private ");
		ctx.invoke(genClassName, (Type) part, ctx, out);
		out.println("() {");
		out.println("value = -1;");
		out.println("}");
	}

	public void genDefaultValue(Enumeration part, Context ctx, TabbedWriter out) {
		out.print("null");
	}
	
	public void genClassHeader(Enumeration part, Context ctx, TabbedWriter out) {
		out.print("public enum ");
		ctx.invoke(genClassName, (Type) part, ctx, out);
		out.println(" {");
	}

	public void genAccessor(Enumeration part, Context ctx, TabbedWriter out) {
		ctx.invoke(genClassName, (Type) part, ctx, out);
	}

	public void genRuntimeTypeName(Enumeration part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genClassName, (Type) part, ctx, out);
	}
}
