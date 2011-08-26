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
package org.eclipse.edt.gen.javascript.templates.egl.lang;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Type;

public class AnyTypeTemplate extends JavaScriptTemplate {

	public void genConversionOperation(Type type, Context ctx, TabbedWriter out, AsExpression arg) {
		// check to see if a conversion is required
		if (arg.getEType() instanceof Interface || arg.getEType() instanceof Service) {
			ctx.invokeSuper(this, genConversionOperation, type, ctx, out, arg);
		} else if (arg.getConversionOperation() != null) {
			out.print(ctx.getNativeImplementationMapping((Classifier) arg.getConversionOperation().getContainer()) + '.');
			out.print("from");
			out.print(ctx.getNativeTypeName(arg.getConversionOperation().getParameters().get(0).getType()));
			out.print("(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			if (ctx.getPrimitiveMapping(arg.getObjectExpr().getType().getClassifier().getTypeSignature()) == null) {
				out.print(",\"");
				ctx.invoke(genSignature, arg.getObjectExpr().getType(), ctx, out, arg);
				out.print("\"");
			}
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out, arg);
			out.print(")");
		} else if (ctx.mapsToPrimitiveType(arg.getEType())) {
			ctx.invoke(genRuntimeTypeName, arg.getEType(), ctx, out, TypeNameKind.EGLImplementation);
			out.print(".ezeCast(");
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out, arg);
			out.print(")");
		} else {
			out.print(eglnamespace + "egl.lang.EglAny.ezeCast("); // TODO sbg need to dynamically get class name
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			out.print(", ");
			ctx.invoke(genRuntimeTypeName, arg.getEType(), ctx, out, TypeNameKind.JavascriptImplementation);
			out.print(")");
		}
	}

	public void genSignature(Type type, Context ctx, TabbedWriter out) {
		out.print("T");
		out.print(type.getTypeSignature().replaceAll("\\.", "/"));
		out.print(";");
	}

	public void genFieldInfoTypeName(Part part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genRuntimeTypeName, part, ctx, out, arg);
	}
}
