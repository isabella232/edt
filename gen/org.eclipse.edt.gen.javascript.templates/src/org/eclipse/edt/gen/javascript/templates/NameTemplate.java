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

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Annotation;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.Name;

public class NameTemplate extends LHSExpressionTemplate {
	public static final String AnnotationType_EGLProperty = "egl.core.eglproperty";

	public boolean isProperty(Name expr) {
		return getProperty(expr) != null;
	}
	public Annotation getProperty(Name expr) {
		return expr.getNamedElement().getAnnotation(AnnotationType_EGLProperty);
	}

	public boolean isWrapped(Member mbr, Context ctx) {
		return (Integer)ctx.getAttribute(mbr, Constants.BOX_ANY_ANNOTATION) != null;
	}

	public void genLHSExpression(Name expr, Context ctx, TabbedWriter out, Object... args) {
		Annotation prop = getProperty(expr);
		if (prop == null) {
			ctx.gen(genExpression, expr, ctx, out, args );
		}
		else {
			ctx.gen(genExpression, expr, ctx, out, args );
			out.print(".");
			out.print((String)prop.getValue("setMethod"));
		}
	}
	
	public void genRHSAssignment(Name expr, Context ctx, TabbedWriter out, Object... args) {
		Expression rhs = (Expression)args[0];
		if (isProperty(expr)) {
			out.print('(');
			ctx.gen(genRHSExpression, rhs, ctx, out, expr);
			out.print(')');
		}
		else {
			super.genRHSAssignment(expr, ctx, out, args);		
		}
	}


}
