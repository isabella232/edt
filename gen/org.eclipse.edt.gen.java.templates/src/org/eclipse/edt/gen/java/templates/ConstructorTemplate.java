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

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Constructor;

public class ConstructorTemplate extends JavaTemplate {

	public void preGen(Constructor constructor, Context ctx) {
		ctx.invoke(preGen, constructor.getStatementBlock(), ctx);
	}

	public void genFunctionHeader(Constructor constructor, Context ctx, TabbedWriter out) {
		// process the function
		ctx.invokeSuper(this, genDeclaration, constructor, ctx, out);
		// remember what function we are processing
		ctx.setCurrentFunction(constructor);
		ctx.invoke(genClassName, constructor.getType(), ctx, out);
		out.print("(");
		ctx.foreach(constructor.getParameters(), ',', genDeclaration, ctx, out);
		out.println(") {");
	}

	public void genFunctionBody(Constructor constructor, Context ctx, TabbedWriter out) {
		ctx.invoke(genStatementNoBraces, constructor.getStatementBlock(), ctx, out);
	}

	public void genDeclaration(Constructor constructor, Context ctx, TabbedWriter out) {
		// write out the debug extension data
		CommonUtilities.generateSmapExtension(constructor, ctx);
		ctx.invoke(genFunctionHeader, constructor, ctx, out);
		ctx.invoke(genFunctionBody, constructor, ctx, out);
		// we always write out smap data for the final brace, just in case there is no return statement
		ctx.genSmapEnd(constructor, out);
		// write out the method ending brace
		out.println("}");
	}
}
