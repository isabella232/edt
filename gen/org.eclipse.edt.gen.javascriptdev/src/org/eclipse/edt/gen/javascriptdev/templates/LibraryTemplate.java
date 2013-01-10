/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.javascriptdev.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Library;

public class LibraryTemplate extends org.eclipse.edt.gen.javascript.templates.LibraryTemplate {

	public void genGetVariablesEntry(Library lib, Context ctx, TabbedWriter out) {
		out.print("{name: " + quoted(lib.getCaseSensitiveName()) + ", value : ");
		ctx.invoke(genAccessor, lib, ctx, out);
		out.print(", type : " + quoted(lib.getFullyQualifiedName()) + ", jsName : \"");
		ctx.invoke(genAccessor, lib, ctx, out);
		out.print("\"}");
	}

	@Override
	public void genClassHeader(Library library, Context ctx, TabbedWriter out) {
		String name = library.getFullyQualifiedName();
		out.println("if (egl.eze$$userLibs) egl.eze$$userLibs.push('" + name + "');" );
		out.println("else egl.eze$$userLibs = ['" + name + "'];");
		super.genClassHeader(library, ctx, out);
	}
	
	@Override
	public void genContainerBasedAccessorArgs(Library library, Context ctx, TabbedWriter out, Function arg) {
		super.genContainerBasedAccessorArgs(library, ctx, out, arg);
		out.print(", \"");
		ctx.invoke(genName, arg, ctx, out);
		out.print("\"");
	}
}
