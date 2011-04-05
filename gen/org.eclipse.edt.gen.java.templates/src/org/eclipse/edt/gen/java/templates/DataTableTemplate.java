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

import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.DataTable;

public class DataTableTemplate extends JavaTemplate {

	public void genClassBody(DataTable dataTable, Context ctx, TabbedWriter out, Object... args) {
	}

	public void genSuperClass(DataTable dataTable, Context ctx, TabbedWriter out, Object... args) {
		out.print("ExecutableBase");
	}

	public void genAccessor(DataTable dataTable, Context ctx, TabbedWriter out, Object... args) {
		out.print(Constants.LIBRARY_PREFIX + dataTable.getFullyQualifiedName().replace('.', '_') + "()");
	}

	public void genConstructor(DataTable dataTable, Context ctx, TabbedWriter out, Object... args) {
		out.print("public ");
		ctx.gen(genClassName, dataTable, ctx, out, args);
		out.print("( RunUnit ru");
		// genAdditionalConstructorParams(dataTable, ctx, out, args);
		out.println(" ) {");
		out.print("super( ru");
		// genAdditionalSuperConstructorArgs(dataTable, ctx, out, args);
		out.println(" );");
		out.println("ezeInitialize();");
		out.println("}");

		out.print("public ");
		ctx.gen(genRuntimeTypeName, dataTable, ctx, out, TypeNameKind.EGLImplementation);
		out.println(" " + Constants.LIBRARY_PREFIX + dataTable.getFullyQualifiedName().replace('.', '_') + "() {");
		out.println("return this;");
		out.println("}");
	}
}
