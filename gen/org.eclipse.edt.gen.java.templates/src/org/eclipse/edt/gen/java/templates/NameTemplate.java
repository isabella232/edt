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

import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Name;

public class NameTemplate extends JavaTemplate {

	public Annotation getPropertyAnnotation(Name expr) {
		return expr.getNamedElement().getAnnotation(Constants.Annotation_EGLProperty);
	}

	public void genAssignment(Name expr, Context ctx, TabbedWriter out, Expression arg1, String arg2) {
		Annotation property = getPropertyAnnotation(expr);
		if (property != null) {
			if (expr.getQualifier() != null) {
				ctx.invoke(genExpression, expr.getQualifier(), ctx, out);
				out.print(".");
				if (property.getValue("setMethod") != null)
					out.print((String) property.getValue("setMethod"));
				else {
					out.print("set");
					out.print(expr.getNamedElement().getName().substring(0, 1).toUpperCase());
					if (expr.getNamedElement().getName().length() > 1)
						out.print(expr.getNamedElement().getName().substring(1));
				}
				out.print("(");
				ctx.invoke(genExpression, arg1, ctx, out);
				out.print(")");
			} else {
				ctx.invoke(genName, expr.getNamedElement(), ctx, out);
				out.print(arg2);
				ctx.invoke(genExpression, arg1, ctx, out);
			}
		} else
			ctx.invoke(genAssignment, expr.getType(), ctx, out, expr, arg1, arg2);
	}
}
