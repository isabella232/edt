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

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Handler;

public class HandlerTemplate extends JavaTemplate {

	public void genSuperClass(Handler type, Context ctx, TabbedWriter out) {
		out.print("ExecutableBase");
	}

	public void genConstructor(Handler type, Context ctx, TabbedWriter out) {
		out.println("");
		// Generate RunUnit constructor
		out.print("public ");
		ctx.invoke(genClassName, type, ctx, out);
		out.print("( RunUnit ru");
		ctx.invoke(genAdditionalConstructorParams, type, ctx, out);
		out.println(" ) {");
		out.print("super( ru");
		ctx.invoke(genAdditionalSuperConstructorArgs, type, ctx, out);
		out.println(" );");
		out.println("ezeInitialize();");
		out.println("}");
	}

	public void genConstructorOptions(Handler type, Context ctx, TabbedWriter out) {
		out.print("_runUnit()");
	}

	public void genRuntimeTypeName(Handler type, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genPartName, type, ctx, out);
	}
}
