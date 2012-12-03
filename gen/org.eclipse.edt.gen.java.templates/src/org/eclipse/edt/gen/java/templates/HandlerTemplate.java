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
import org.eclipse.edt.mof.egl.Handler;

public class HandlerTemplate extends JavaTemplate {

	public void genPart(Handler part, Context ctx, TabbedWriter out) {
		// Avoid generating RUI stuff.
		if ( part.getAnnotation( "eglx.ui.rui.RUIWidget" ) == null
				&& part.getAnnotation( "eglx.ui.rui.RUIHandler" ) == null )
		{
			ctx.invokeSuper( this, genPart, part, ctx, out );
		}
	}
	
	public void genSuperClass(Handler type, Context ctx, TabbedWriter out) {
		out.print("ExecutableBase");
	}

	public void genConstructor(Handler type, Context ctx, TabbedWriter out) {
		ctx.invoke(genInstanceInitializer, type, ctx, out);
		out.println();
		out.print("public ");
		ctx.invoke(genClassName, type, ctx, out);
		out.print("(");
		ctx.invoke(genAdditionalConstructorParams, type, ctx, out);
		out.println(") {");
		out.print("super(");
		ctx.invoke(genAdditionalSuperConstructorArgs, type, ctx, out);
		out.println(");");
		out.println("}");
	}

	public void genRuntimeTypeName(Handler type, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genPartName, type, ctx, out);
	}
	
	public void genImplements(Handler part, Context ctx, TabbedWriter out) {
		String interfaceList = StructPartTemplate.getInterfaces(part, ctx);
		if (!interfaceList.isEmpty()) {
			out.print(" implements ");
			out.print(interfaceList);
		}
	}
	
}
