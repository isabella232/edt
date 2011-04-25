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

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ConstantField;
import org.eclipse.edt.mof.egl.Field;

public class ConstantFieldTemplate extends JavaTemplate {

	public void genDeclaration(ConstantField field, Context ctx, TabbedWriter out, Object... args) {
		// write out the debug extension data
		CommonUtilities.generateSmapExtension((Field) field, ctx);
		// process the field
		out.print("private static final ");
		ctx.gen(genRuntimeTypeName, field, ctx, out, args);
		out.print(" ezeConst_");
		ctx.gen(genName, field, ctx, out, args);
		out.print(" = ");
		ctx.gen(genInstantiation, field.getType(), ctx, out, field);
		out.println(";");
		out.print("public ");
		ctx.gen(genRuntimeTypeName, field, ctx, out, args);
		out.print(" ");
		ctx.gen(genName, field, ctx, out, args);
		out.print(" = ezeConst_");
		ctx.gen(genName, field, ctx, out, args);
		out.println(";");
	}
}
