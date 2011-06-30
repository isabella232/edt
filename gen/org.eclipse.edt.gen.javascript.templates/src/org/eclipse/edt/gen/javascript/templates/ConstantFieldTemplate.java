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
import org.eclipse.edt.mof.egl.ConstantField;

public class ConstantFieldTemplate extends JavaScriptTemplate {

	public void genDeclaration(ConstantField field, Context ctx, TabbedWriter out) {
		// process the field
		out.print("private static final ");
		ctx.invoke(genRuntimeTypeName, field, ctx, out, TypeNameKind.JavascriptPrimitive);
		out.print(" ezeConst_");
		ctx.invoke(genName, field, ctx, out);
		out.print(" = ");
		ctx.invoke(genInstantiation, field.getType(), ctx, out, field);
		out.println(";");
		out.print("public ");
		ctx.invoke(genRuntimeTypeName, field, ctx, out, TypeNameKind.JavascriptPrimitive);
		out.print(" ");
		ctx.invoke(genName, field, ctx, out);
		out.print(" = ezeConst_");
		ctx.invoke(genName, field, ctx, out);
		out.println(";");
	}
}
