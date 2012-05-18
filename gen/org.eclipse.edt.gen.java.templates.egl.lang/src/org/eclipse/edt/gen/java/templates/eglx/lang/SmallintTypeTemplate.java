/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates.eglx.lang;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Type;

public class SmallintTypeTemplate extends JavaTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out) {
		out.print("0");
	}

	public void genConstructorOptions(EGLClass type, Context ctx, TabbedWriter out) {
		genDefaultValue(type, ctx, out);
	}
	
	public void genConversionOperation(EGLClass type, Context ctx, TabbedWriter out, AsExpression arg) {
		if (CommonUtilities.isHandledByJavaWithoutCast(arg.getObjectExpr(), arg, ctx)) {
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out, arg.getType());
		} else if (CommonUtilities.isHandledByJavaWithCast(arg.getObjectExpr(), arg, ctx)) {
			out.print("(" + ctx.getPrimitiveMapping(arg.getType()) + ")");
			out.print("(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			out.print(")");
		} else
			ctx.invokeSuper(this, genConversionOperation, type, ctx, out, arg);
	}
	
	public Boolean isAssignmentBreakupWanted(Type type, Context ctx, Assignment expr) {
		//if the left side is a Short the statement must be broken into LHS = LSH op RHS
		return expr.getLHS().isNullable() || (Boolean)ctx.invokeSuper(this, "isAssignmentBreakupWanted", type, ctx, expr);
	}

}
