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
package org.eclipse.edt.gen.java.templates;

import java.util.List;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Service;

public class ServiceTemplate extends JavaTemplate {

	public void preGenPart(Service service, Context ctx) {
		ctx.invokeSuper(this, preGenPart, service, ctx);
		ctx.invoke(preGenPartImport, service, ctx);
	}

	@SuppressWarnings("unchecked")
	public void preGenPartImport(Service service, Context ctx) {
		ctx.invokeSuper(this, preGenPartImport, service, ctx);
		List<String> typesImported = (List<String>) ctx.getAttribute(ctx.getClass(), org.eclipse.edt.gen.java.Constants.SubKey_partTypesImported);
		if (!typesImported.contains("org.eclipse.edt.javart.services.*"))
			typesImported.add("org.eclipse.edt.javart.services.*");
		if (!typesImported.contains("org.eclipse.edt.javart.Runtime"))
			typesImported.add("org.eclipse.edt.javart.Runtime");
	}

	public void genSuperClass(Service service, Context ctx, TabbedWriter out) {
		out.print("ServiceBase");
	}
	
	public void genInstanceInitializerBody(Service service, Context ctx, TabbedWriter out) {
		out.println("if(Runtime.getRunUnit().getActiveExecutable() == null)");
		out.println("Runtime.getRunUnit().setActiveExecutable(this);");
		
		ctx.invokeSuper(this, genInstanceInitializerBody, service, ctx, out);
	}

	public void genConstructor(Service service, Context ctx, TabbedWriter out) {
		ctx.invoke(genInstanceInitializer, service, ctx, out);
		out.println();
		out.print("public ");
		ctx.invoke(genClassName, service, ctx, out);
		out.print("(");
		ctx.invoke(genAdditionalConstructorParams, service, ctx, out);
		out.println(") {");
		out.print("super(");
		ctx.invoke(genAdditionalSuperConstructorArgs, service, ctx, out);
		out.println(");");
		out.println("}");
	}

	public void genRuntimeTypeName(Service service, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genPartName, service, ctx, out);
	}

	public void genImplements(Service part, Context ctx, TabbedWriter out) {
		String interfaceList = StructPartTemplate.getInterfaces(part, ctx);
		if (!interfaceList.isEmpty()) {
			out.print(" implements ");
			out.print(interfaceList);
		}
	}
}
