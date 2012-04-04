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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Program;

public class ProgramTemplate extends JavaScriptTemplate {

	public void genSuperClass(Program program, Context ctx, TabbedWriter out) {
		out.print("ProgramBase");
	}

	public void genConstructor(Program program, Context ctx, TabbedWriter out) {}

	public void genRuntimeTypeName(Program program, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genPartName, program, ctx, out);
	}
}
