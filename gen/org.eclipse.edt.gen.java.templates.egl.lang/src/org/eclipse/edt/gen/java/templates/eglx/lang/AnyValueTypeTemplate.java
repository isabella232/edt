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
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Type;

public class AnyValueTypeTemplate extends JavaTemplate {

	public void genConversionOperation(Type type, Context ctx, TabbedWriter out, AsExpression arg) {
		// check to see if a conversion is required
		if (arg.getConversionOperation() != null) {
			out.print(ctx.getNativeImplementationMapping((Classifier) arg.getConversionOperation().getContainer()) + ".");
			out.print(arg.getConversionOperation().getName());
			out.print("(");
			// do a callout to allow certain source types to decide to create a boxing expression
			ctx.invoke(genAsExpressionBoxing, arg.getObjectExpr().getType(), ctx, out, arg);
			// then process the conversion operation
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			if (CommonUtilities.isBoxedOutputTemp(arg.getObjectExpr(), ctx))
				out.print(".ezeUnbox()");
			// normally, we send in the type dependent options of the target, but in the case where we are using asNumber, we
			// need to send in the source
			if (arg.getConversionOperation().getName().equalsIgnoreCase("asNumber"))
				ctx.invoke(genTypeDependentOptions, arg.getObjectExpr().getType(), ctx, out);
			else
				ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out);
			out.print(")");
		} else if (ctx.mapsToPrimitiveType(arg.getEType())) {
			ctx.invoke(genRuntimeTypeName, arg.getEType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".ezeCast(");
			// do a callout to allow certain source types to decide to create a boxing expression
			ctx.invoke(genAsExpressionBoxing, arg.getObjectExpr().getType(), ctx, out, arg);
			// then process the conversion operation
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			if (CommonUtilities.isBoxedOutputTemp(arg.getObjectExpr(), ctx))
				out.print(".ezeUnbox()");
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out);
			out.print(")");
		} else {
			out.print("org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			if (CommonUtilities.isBoxedOutputTemp(arg.getObjectExpr(), ctx))
				out.print(".ezeUnbox()");
			out.print(", ");
			ctx.invoke(genRuntimeClassTypeName, arg.getEType(), ctx, out, TypeNameKind.JavaImplementation);
			out.print(")");
		}
	}
}
