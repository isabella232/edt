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
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;

public class QualifiedFunctionInvocationTemplate extends JavascriptTemplate {

	public void genExpression(QualifiedFunctionInvocation expr, Context ctx, TabbedWriter out, Object... args) {
		// first, make this expression's arguments compatible
		IRUtils.makeCompatible(expr);
		// if this is an egl system library, delegate to the template associated with the target function's container
		if (expr.getTarget().getContainer() != null && ctx.mapsToNativeType((Type) expr.getTarget().getContainer()))
			ctx.gen(genInvocation, (Type) expr.getTarget().getContainer(), ctx, out, expr);
		else
			ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
