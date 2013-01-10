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

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;

public class NameTemplate extends JavaTemplate {

	public void genAssignment(Name expr, Context ctx, TabbedWriter out, Expression arg1, String arg2) {
		String propertyFunction = CommonUtilities.getPropertyFunction(expr.getNamedElement(), true, ctx);
		if (propertyFunction != null) {
			FunctionMember currentFunction = ctx.getCurrentFunction();
			if (currentFunction != null && propertyFunction.equals(currentFunction.getCaseSensitiveName())) {
				if (expr.getQualifier() != null) {
					ctx.invoke(genExpression, expr.getQualifier(), ctx, out, expr.getQualifier());
					out.print('.');
				}
				ctx.invoke(genName, expr.getNamedElement(), ctx, out);
				out.print(arg2);
				ctx.invoke(genExpression, arg1, ctx, out);
			} else {
				if (expr.getQualifier() != null) {
					ctx.invoke(genExpression, expr.getQualifier(), ctx, out, expr.getQualifier());
					out.print('.');
				}
				out.print(propertyFunction);
				out.print('(');
				// if we are doing some type of complex assignment, we need to place that in the argument
				if (arg2.length() > 3 && arg2.indexOf("=") > 1) {
					ctx.invoke(genExpression, expr, ctx, out);
					out.print(arg2.substring(0, arg2.indexOf("=")) + arg2.substring(arg2.indexOf("=") + 1));
				}
				ctx.invoke(genExpression, arg1, ctx, out);
				out.print(')');
			}
		} else
			ctx.invoke(genAssignment, expr.getType(), ctx, out, expr, arg1, arg2);
	}
}
