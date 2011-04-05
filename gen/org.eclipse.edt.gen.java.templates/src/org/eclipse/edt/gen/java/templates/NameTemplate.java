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

	public void genAssignment(Name expr, Context ctx, TabbedWriter out, Object... args) {
		Annotation property = getPropertyAnnotation(expr);
		if (property != null) {
			if (expr.getQualifier() != null) {
				ctx.gen(genExpression, expr.getQualifier(), ctx, out, args);
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
				ctx.gen(genExpression, (Expression) args[0], ctx, out, args);
				out.print(")");
			} else {
				ctx.gen(genName, expr.getNamedElement(), ctx, out, args);
				out.print(" = ");
				ctx.gen(genExpression, (Expression) args[0], ctx, out, args);
			}
		} else {
			Object[] objects = new Object[args.length + 1];
			objects[0] = expr;
			for (int i = 0; i < args.length; i++) {
				objects[i + 1] = args[i];
			}
			ctx.gen(genAssignment, expr.getType(), ctx, out, objects);
		}
	}
}
