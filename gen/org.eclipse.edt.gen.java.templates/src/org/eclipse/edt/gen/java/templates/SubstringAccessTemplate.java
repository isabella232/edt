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

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.SubstringAccess;

public class SubstringAccessTemplate extends JavaTemplate {

	public void genExpression(SubstringAccess expr, Context ctx, TabbedWriter out, Object... args) {
		out.print(ctx.getNativeImplementationMapping(expr.getType()) + ".substring(");
		ctx.gen(genExpression, expr.getStringExpression(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, expr.getStart(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, expr.getEnd(), ctx, out, args);
		out.print(")");
	}

	public void genAssignment(SubstringAccess expr, Context ctx, TabbedWriter out, Object... args) {
		// TODO this needs work
		out.print(ctx.getNativeImplementationMapping(expr.getType()) + ".substring(");
		ctx.gen(genExpression, expr.getStringExpression(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, expr.getStart(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, expr.getEnd(), ctx, out, args);
		out.print(")");
	}
}
