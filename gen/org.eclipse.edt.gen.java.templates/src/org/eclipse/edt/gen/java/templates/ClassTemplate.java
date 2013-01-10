/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
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

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Class;
import org.eclipse.edt.mof.egl.Constructor;
import org.eclipse.edt.mof.egl.StructPart;

public class ClassTemplate extends JavaTemplate
{
	public void preGenPartImport(Class type, Context ctx) {
		CommonUtilities.processImport("java.io.Serializable", ctx);
		ctx.invokeSuper(this, preGenPartImport, type, ctx);
	}
	
	public void genSuperClass(Class type, Context ctx, TabbedWriter out) {
		for (StructPart superType : type.getSuperTypes()) {
			if (superType instanceof Class) {
				ctx.invoke(genClassName, superType, ctx, out);
				return;
			}
		}
		out.print("Object");
	}

	public void genConstructor(Class type, Context ctx, TabbedWriter out) {
		ctx.invoke(genInstanceInitializer, type, ctx, out);
	}

	public void genConstructor(Class type, Context ctx, TabbedWriter out, Constructor constructor) {
		ctx.invoke(genDeclaration, constructor, ctx, out);
	}

	public void genRuntimeTypeName(Class type, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genPartName, type, ctx, out);
	}
	
	public void genImplements(Class part, Context ctx, TabbedWriter out) {
		out.print(" implements Serializable");
		String interfaceList = StructPartTemplate.getInterfaces(part, ctx);
		if (!interfaceList.isEmpty()) {
			out.print(", ");
			out.print(interfaceList);
		}
	}
	
	public void genInitializeMethodBody(Class part, Context ctx, TabbedWriter out) {
		// invoke super.ezeInitialize() if there is one.
		if (part.getSuperTypes().size() > 0 && part.getSuperTypes().get(0) instanceof Class) {
			out.println("super.ezeInitialize();");
		}
		ctx.invokeSuper(this, genInitializeMethodBody, part, ctx, out);
	}
}
