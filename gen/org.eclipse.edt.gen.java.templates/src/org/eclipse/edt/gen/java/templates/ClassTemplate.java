/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
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
import org.eclipse.edt.mof.egl.Class;
import org.eclipse.edt.mof.egl.Constructor;

public class ClassTemplate extends JavaTemplate
{
	public void genSuperClass(Class type, Context ctx, TabbedWriter out) {
		out.print("ExecutableBase"); //TODO only use this if it doesn't have an extends clause
	}

	public void genConstructor(Class type, Context ctx, TabbedWriter out) { }

	public void genConstructor(Class type, Context ctx, TabbedWriter out, Constructor constructor) {
		out.println("");
		out.print("public ");
		ctx.invoke(genClassName, type, ctx, out);
		out.print("(");
		ctx.invoke(genAdditionalConstructorParams, type, ctx, out);
		out.println(") {");
		out.print("super(");
		ctx.invoke(genAdditionalSuperConstructorArgs, type, ctx, out);
		out.println(");");
		out.println("}");

		out.println("{");
		out.println("ezeInitialize();");
		out.println("}");
	}

	public void genRuntimeTypeName(Class type, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genPartName, type, ctx, out);
	}
	
	public void genImplements(Class part, Context ctx, TabbedWriter out) {
		String interfaceList = StructPartTemplate.getInterfaces(part, ctx);
		if (!interfaceList.isEmpty()) {
			out.print(" implements ");
			out.print(interfaceList);
		}
	}
}
