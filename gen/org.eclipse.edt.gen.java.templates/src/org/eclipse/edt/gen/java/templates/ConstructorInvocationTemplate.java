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

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ConstructorInvocation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class ConstructorInvocationTemplate extends JavaTemplate {

	public void genExpression(ConstructorInvocation expr, Context ctx, TabbedWriter out) {
		// first, make this expression's arguments compatible
		IRUtils.makeCompatible(expr);
		// delegate to the template associated with the target function's container
		if (expr.getTarget().getContainer() instanceof Type)
			ctx.invoke(genContainerBasedInvocation, (Type) expr.getTarget().getContainer(), ctx, out, expr);
		else
			ctx.invoke(genInvocation, expr, ctx, out);
	}

	public void genInvocation(ConstructorInvocation expr, Context ctx, TabbedWriter out) {
		ctx.invoke(genExpression, expr.getExpression(), ctx, out);
		out.print("(");
		ctx.invoke(genInvocationArguments, expr, ctx, out);
		out.print(")");
	}
}
