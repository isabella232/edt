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
import org.eclipse.edt.mof.EEnumLiteral;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Enumeration;
import org.eclipse.edt.mof.egl.Part;

public class EnumerationTemplate extends PartTemplate {

	public void validateClassBody(Part part, Context ctx, Object... args) {}

	public void genClassBody(Part part, Context ctx, TabbedWriter out, Object... args) {
		// generate enumerated fields
		List<EEnumLiteral> enums = ((Enumeration) part).getEntries();
		if (enums != null && enums.size() != 0) {
			boolean needsSeparator = false;
			for (EEnumLiteral literal : enums) {
				if (needsSeparator)
					out.println(",");
				needsSeparator = true;
				ctx.gen(genName, literal, ctx, out, args);
				out.print("(" + literal.getValue() + ")");
			}
			out.println(";");
		}
		out.println("private final int value;");
		genClassName(part, ctx, out, args);
		out.println("(int value) {");
		out.println("\tthis.value = value;");
		out.println("}");
		out.println("public int getValue() {");
		out.println("\treturn value;");
		out.println("}");
	}

	public void genClassHeader(Part part, Context ctx, TabbedWriter out, Object... args) {
		out.print("public enum ");
		genClassName(part, ctx, out, args);
		out.println(" {");
	}

	public void genAccessor(Part part, Context ctx, TabbedWriter out, Object... args) {
		genClassName(part, ctx, out, args);
	}

	public void genRuntimeTypeName(Part part, Context ctx, TabbedWriter out, Object... args) {
		genClassName(part, ctx, out, args);
	}
}
