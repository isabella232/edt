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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class InterfaceTemplate extends JavaTemplate {

	public void preGen(Type type, Context ctx) {}

	public void preGenClassBody(Interface part, Context ctx) {}

	public void genClassHeader(Interface part, Context ctx, TabbedWriter out) {
		out.print("public interface ");
		ctx.invoke(genClassName, part, ctx, out);
		String interfaceList = StructPartTemplate.getInterfaces(part, ctx);
		if (!interfaceList.isEmpty()) {
			out.print(" extends ");
			out.print(interfaceList);
		}
		out.println(" {");
	}

	public void genClassBody(Interface part, Context ctx, TabbedWriter out) {
		// Add this part's file to the smap files list, so that this part is always the first file listed.
		String file = IRUtils.getQualifiedFileName(part);
		ctx.setCurrentFile(file);
		if (ctx.getSmapFiles().indexOf(file) < 0) {
			ctx.getSmapFiles().add(file);
		}

		ctx.invoke(genFields, part, ctx, out);
		ctx.invoke(genConstructors, part, ctx, out);
		ctx.invoke(genFunctions, part, ctx, out);
	}

	public void genFields(Interface part, Context ctx, TabbedWriter out) {
		for (Field field : part.getFields()) {
			ctx.invoke(genField, part, ctx, out, field);
		}
	}

	public void genField(Interface part, Context ctx, TabbedWriter out, Field arg) {
		ctx.invoke(genDeclaration, arg, ctx, out);
	}

	public void genFunction(Interface part, Context ctx, TabbedWriter out, Function arg) {
		ctx.invoke(genRuntimeTypeName, arg, ctx, out, TypeNameKind.JavaPrimitive);
		out.print(" ");
		ctx.invoke(genName, arg, ctx, out);
		out.print("(");
		ctx.foreach(arg.getParameters(), ',', genDeclaration, ctx, out);
		out.println(");");
	}
}
