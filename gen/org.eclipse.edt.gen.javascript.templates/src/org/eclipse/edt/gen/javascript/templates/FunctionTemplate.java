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
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Type;

public class FunctionTemplate extends JavaScriptTemplate {

	public void validate(Function function, Context ctx, Object... args) {
		ctx.validate(validate, function.getStatementBlock(), ctx, args);
	}

	public void genDeclaration(Function function, Context ctx, TabbedWriter out, Object... args) {
		out.print("\"");
		genName(function, ctx, out, args);
		out.print("\"");
		out.print(": function(");
		ctx.foreach(function.getParameters(), ',', genDeclaration, ctx, out, args);
		for (FunctionParameter parm : function.getParameters()) {
			if (org.eclipse.edt.gen.CommonUtilities.isBoxedParameterType(parm, ctx)) {
				out.print(", ");
				out.print(eze$$func);
				out.print(", ");
				out.print(caller);
				break;
			}
		}
		out.println(") {");
		ctx.gen(genStatementNoBraces, function.getStatementBlock(), ctx, out, args);
		out.println("}");
	}

	public void genAccessor(Function function, Context ctx, TabbedWriter out, Object... args) {
		if (function.getContainer() != null && function.getContainer() instanceof Type)
			ctx.gen(genContainerBasedAccessor, (Type) function.getContainer(), ctx, out, function);
		else
			ctx.gen(genName, function, ctx, out, args);
	}

	public void genName(Function function, Context ctx, TabbedWriter out, Object... args) {
		ctx.genSuper(genName, Function.class, function, ctx, out, args);
	}
}
