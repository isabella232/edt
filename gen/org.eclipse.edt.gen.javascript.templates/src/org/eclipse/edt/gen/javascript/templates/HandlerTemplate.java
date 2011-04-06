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
import org.eclipse.edt.mof.egl.Handler;

public class HandlerTemplate extends JavaScriptTemplate {

	public void genSuperClass(Handler type, Context ctx, TabbedWriter out, Object... args) {
		out.print("ExecutableBase");
	}

	public void genConstructor(Handler type, Context ctx, TabbedWriter out, Object... args) {}

	public void genConstructorOptions(Handler type, Context ctx, TabbedWriter out, Object... args) {}

	public void genRuntimeTypeName(Handler type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genPartName, type, ctx, out, args);
	}
}
