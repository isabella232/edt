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
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.TypedElement;

public class ArrayTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(ArrayType generic, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else if (generic.getInitialSize() != null || args.length > 0 && args[0] instanceof Field && ((Field) args[0]).getInitializerStatements() != null) {
			String temporary = ctx.nextTempName();
			out.print("(function() { var ");
			out.print(temporary);
			out.print(" = []; ");
			out.print(temporary);
			out.print(".setType(");
			out.print("\"");
			genSignature(generic, ctx, out, args);
			out.print("\"");
			if (generic.getInitialSize() == null)
				out.println(");");
			else {
				out.println("); for (var i = 0; i < " + generic.getInitialSize() + "; i++) {");
				out.print(temporary);
				out.print("[i] = ");
				ctx.gen(genDefaultValue, generic.getElementType(), ctx, out, args);
				out.println(";}");
			}
			out.print("return ");
			out.print(temporary);
			out.print(";})()");
		} else
			out.print("null");
	}

	public void genSignature(ArrayType generic, Context ctx, TabbedWriter out, Object... args) {
		if (!generic.getTypeArguments().isEmpty()) {
			for (int i = 0; i < generic.getTypeArguments().size(); i++)
				out.print("[");
		}
		ctx.gen(genSignature, generic.getElementType(), ctx, out, args);
	}

	public void genRuntimeTypeName(ArrayType generic, Context ctx, TabbedWriter out, Object... args) {
		out.print("Array");
	}
}
