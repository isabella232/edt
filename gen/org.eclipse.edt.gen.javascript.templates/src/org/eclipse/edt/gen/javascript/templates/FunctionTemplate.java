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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.JavaScriptAliaser;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Type;

public class FunctionTemplate extends JavaScriptTemplate {

	public void preGen(Function function, Context ctx) {
		if (!function.isAbstract()) {
			ctx.invoke(preGen, function.getStatementBlock(), ctx);
		}
		
		for (Annotation annot : org.eclipse.edt.gen.CommonUtilities.getAnnotations(function, ctx)) {
			ctx.invoke(preGen, annot.getEClass(), ctx, annot, function);
		}
	}

	public void genDeclaration(Function function, Context ctx, TabbedWriter out) {
		ctx.setCurrentFunction(function);
		out.print("\"");
		genName(function, ctx, out);
		out.print("\"");
		out.print(": function(");
		ctx.foreach(function.getParameters(), ',', genDeclaration, ctx, out);
		out.println(") {");
		ctx.invoke(genFunctionBody, function, ctx, out);
		out.println("}");
		ctx.setCurrentFunction(null);
	}
	
	public void genFunctionBody(Function function, Context ctx, TabbedWriter out) {
		ctx.invoke(genStatementNoBraces, function.getStatementBlock(), ctx, out);
	}

	public void genAccessor(Function function, Context ctx, TabbedWriter out) {
		if (function.getContainer() != null && function.getContainer() instanceof Type)
			ctx.invoke(genContainerBasedAccessor, (Type) function.getContainer(), ctx, out, function);
		else
			ctx.invoke(genName, function, ctx, out);
	}

	public void genName(Function function, Context ctx, TabbedWriter out) {
		ctx.invokeSuper(this, genName, JavaScriptAliaser.getAlias(ctx, function), ctx, out);
	}

	public Function getCallbackFunction(Function function, Context ctx) {
		return function;
	}

	public void genQualifier(Function function, Context ctx, TabbedWriter out) {
		/*
		 * TODO sbg Ensure that genQualifier is needed for something (e.g., generating "this" in handlers or widgets) --
		 * otherwise, remove it altogether.
		 */
		if (function.getContainer() != null && function.getContainer() instanceof Type)
			ctx.invoke(genQualifier, function.getContainer(), ctx, out, function);
	}

}
