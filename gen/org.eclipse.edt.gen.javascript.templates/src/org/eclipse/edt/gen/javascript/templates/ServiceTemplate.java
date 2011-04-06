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
import org.eclipse.edt.mof.egl.Service;

public class ServiceTemplate extends JavaScriptTemplate {

	public void genSuperClass(Service service, Context ctx, TabbedWriter out, Object... args) {
		// TODO handle more generally when stereotypes are involved
		out.print("ExecutableBase");
	}

	public void genConstructor(Service service, Context ctx, TabbedWriter out, Object... args) {
		// Generate RunUnit constructor
		out.print("public ");
		ctx.gen(genClassName, service, ctx, out, args);
		out.print("( RunUnit ru");
		ctx.gen(genAdditionalConstructorParams, service, ctx, out, args);
		out.println(" ) {");
		out.print("super( ru");
		ctx.gen(genAdditionalSuperConstructorArgs, service, ctx, out, args);
		out.println(" );");
		out.println("ezeInitialize();");
		out.println("}");
	}

	public void genRuntimeTypeName(Service service, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genPartName, service, ctx, out, args);
	}
}
