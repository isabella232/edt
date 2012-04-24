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

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.Field;

public class ConstantFieldTemplate extends JavaTemplate {

	public void genDeclaration(ConstantField field, Context ctx, TabbedWriter out) {
		// write out the debug extension data
		CommonUtilities.generateSmapExtension((Field) field, ctx);
		out.print("private static final ");
		ctx.invoke(genRuntimeTypeName, field, ctx, out, TypeNameKind.JavaPrimitive);
		out.print(" ezeConst_");
		ctx.invoke(genStatementNoBraces, field.getInitializerStatements(), ctx, out);
		out.print("public final ");
		ctx.invoke(genRuntimeTypeName, field, ctx, out, TypeNameKind.JavaPrimitive);
		out.print(" ");
		ctx.invoke(genName, field, ctx, out);
		out.print(" = ezeConst_");
		ctx.invoke(genName, field, ctx, out);
		out.println(";");
	}

	public void genSetter(ConstantField field, Context ctx, TabbedWriter out) {
		// Don't generate a setter for consts.
	}
}
