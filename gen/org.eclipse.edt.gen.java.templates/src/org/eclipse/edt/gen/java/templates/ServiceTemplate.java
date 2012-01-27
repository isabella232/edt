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

import java.util.List;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.StructPart;

public class ServiceTemplate extends JavaTemplate {

	public void preGenPart(StructPart part, Context ctx) {
		ctx.invokeSuper(this, preGenPart, part, ctx);
		List<String> typesImported = (List<String>) ctx.getAttribute(ctx.getClass(), org.eclipse.edt.gen.java.Constants.SubKey_partTypesImported);
		typesImported.add("org.eclipse.edt.javart.services.*");
		typesImported.add("org.eclipse.edt.javart.json.Json");
	}
	public void genSuperClass(Service service, Context ctx, TabbedWriter out) {
		out.print("ServiceBase");
	}

	public void genConstructor(Service service, Context ctx, TabbedWriter out) {
		// Generate RunUnit constructor
		out.print("public ");
		ctx.invoke(genClassName, service, ctx, out);
		out.print("(");
		ctx.invoke(genAdditionalConstructorParams, service, ctx, out);
		out.println(") {");
		out.print("super(");
		ctx.invoke(genAdditionalSuperConstructorArgs, service, ctx, out);
		out.println(");");
		out.println("ezeInitialize();");
		out.println("}");
	}

	public void genRuntimeTypeName(Service service, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genPartName, service, ctx, out);
	}
	
	public void genFunction(Service service, Context ctx, TabbedWriter out, Function arg) {
		ctx.invoke(genFunctionParametersSignature, arg, ctx, out);
		ctx.invoke(genDeclaration, arg, ctx, out);
	}

	public void genImplements(Service part, Context ctx, TabbedWriter out) {
		String interfaceList = StructPartTemplate.getInterfaces(part, ctx);
		if (!interfaceList.isEmpty()) {
			out.print(" implements ");
			out.print(interfaceList);
		}
	}
}
