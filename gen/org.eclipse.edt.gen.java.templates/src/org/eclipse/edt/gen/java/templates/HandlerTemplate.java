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

public class HandlerTemplate extends ClassTemplate {

	public void genSuperClass(Handler type, Context ctx, TabbedWriter out, Object... args) {
		out.print("ExecutableBase");
	}

	public void genConstructor(Handler type, Context ctx, TabbedWriter out, Object... args) {
		out.println("");
		// Generate RunUnit constructor
		out.print("public ");
		genClassName(type, ctx, out, args);
		out.print("( RunUnit ru");
		genAdditionalConstructorParams(type, ctx, out, args);
		out.println(" ) {");
		out.print("super( ru");
		genAdditionalSuperConstructorArgs(type, ctx, out, args);
		out.println(" );");
		out.println("ezeInitialize();");
		out.println('}');
	}

	public void genConstructorOptions(Handler type, Context ctx, TabbedWriter out, Object... args) {
		out.print("ezeProgram._runUnit()");
	}

	public void genRuntimeTypeName(Handler type, Context ctx, TabbedWriter out, Object... args) {
		genPartName(type, ctx, out, args);
	}
}
