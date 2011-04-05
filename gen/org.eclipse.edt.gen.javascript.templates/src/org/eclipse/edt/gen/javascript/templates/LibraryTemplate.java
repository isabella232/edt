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
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Library;

public class LibraryTemplate extends JavascriptTemplate {

	public void genSuperClass(Library library, Context ctx, TabbedWriter out, Object... args) {
		out.print("ExecutableBase");
	}

	public void genAccessor(Library library, Context ctx, TabbedWriter out, Object... args) {}

	public void genConstructor(Library library, Context ctx, TabbedWriter out, Object... args) {}

	public void genGetterSetter(Library library, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genGetter, (Field) args[0], ctx, out, args);
		ctx.gen(genSetter, (Field) args[0], ctx, out, args);
	}

	public void genRuntimeTypeName(Library library, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genPartName, library, ctx, out, args);
	}
}
