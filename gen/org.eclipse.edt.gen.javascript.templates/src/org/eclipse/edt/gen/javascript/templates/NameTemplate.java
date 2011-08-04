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

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Name;

public class NameTemplate extends JavaScriptTemplate {

	public void genAssignment(Name expr, Context ctx, TabbedWriter out, Expression arg1, String arg2) {
		Annotation property = CommonUtilities.getPropertyAnnotation(expr.getNamedElement());
		if (property != null) {
			if (expr.getQualifier() != null) {
				ctx.invoke(genExpression, expr.getQualifier(), ctx, out);
				out.print(".");
				
				String propertyFn = (String)property.getValue("setMethod");
				
				if (propertyFn != null && propertyFn.length() > 0) {
					out.print(CommonUtilities.getPropertyFunction(propertyFn));
				} else {
					out.print("set");
					out.print(expr.getNamedElement().getName().substring(0, 1).toUpperCase());
					if (expr.getNamedElement().getName().length() > 1)
						out.print(expr.getNamedElement().getName().substring(1));
				}
				out.print("(");
				ctx.invoke(genExpression, arg1, ctx, out);
				out.print(")");
			} else {
				ctx.invoke(genQualifier, expr.getNamedElement(), ctx, out);
				ctx.invoke(genName, expr.getNamedElement(), ctx, out);
				out.print(arg2);
				ctx.invoke(genExpression, arg1, ctx, out);
			}
		} else
			ctx.invoke(genAssignment, expr.getType(), ctx, out, expr, arg1, arg2);
	}
}
