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
package org.eclipse.edt.gen.javascript.templates.eglx.lang;

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.Classifier;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Interface;
import org.eclipse.edt.mof.egl.IrFactory;
import org.eclipse.edt.mof.egl.Part;
import org.eclipse.edt.mof.egl.Service;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class AnyTypeTemplate extends JavaScriptTemplate {

	//TODO sbg This method should be more like the one in Java gen
	public void genConversionOperation(Type type, Context ctx, TabbedWriter out, AsExpression arg) {
		// check to see if a conversion is required
		if (arg.getEType() instanceof Interface || arg.getEType() instanceof Service) {
			ctx.invokeSuper(this, genConversionOperation, type, ctx, out, arg);
		} else if (arg.getConversionOperation() != null) {
			out.print(ctx.getNativeImplementationMapping((Classifier) arg.getConversionOperation().getContainer()) + '.');
			out.print("from");
			out.print(ctx.getNativeTypeName(arg.getConversionOperation().getParameters().get(0).getType()));
			out.print("(");
			Expression objectExpr = arg.getObjectExpr();
			if (objectExpr instanceof BoxingExpression){
				objectExpr = ((BoxingExpression)objectExpr).getExpr();
			}
			ctx.invoke(genExpression, objectExpr, ctx, out);
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
			if (arg.getObjectExpr().getType() != TypeUtils.Type_ANY) {
				BoxingExpression boxingExpr = IrFactory.INSTANCE.createBoxingExpression();
				boxingExpr.setExpr(arg.getObjectExpr());
				ctx.invoke(genExpression, boxingExpr, ctx, out);
			}
			else {
				ctx.putAttribute(arg.getObjectExpr(), Constants.DONT_UNBOX, Boolean.TRUE);
				ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
				ctx.putAttribute(arg.getObjectExpr(), Constants.DONT_UNBOX, Boolean.FALSE);
			}
			out.print(",");
			out.print(arg.getObjectExpr().isNullable());
			ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out, arg);
			out.print(")");
		} else {
			out.print(eglnamespace + "eglx.lang.EAny.ezeCast("); // TODO sbg need to dynamically get class name
			if (arg.getObjectExpr().getType() != TypeUtils.Type_ANY) {
				BoxingExpression boxingExpr = IrFactory.INSTANCE.createBoxingExpression();
				boxingExpr.setExpr(arg.getObjectExpr());
				ctx.invoke(genExpression, boxingExpr, ctx, out);
			}
			else {
				ctx.putAttribute(arg.getObjectExpr(), Constants.DONT_UNBOX, Boolean.TRUE);
				ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
				ctx.putAttribute(arg.getObjectExpr(), Constants.DONT_UNBOX, Boolean.FALSE);
			}
			out.print(", ");
			ctx.invoke(genRuntimeTypeName, arg.getEType(), ctx, out, TypeNameKind.JavascriptImplementation);
			out.print(")");
		}
	}

	public void genSignature(Type type, Context ctx, TabbedWriter out) {
		out.print("T");
		out.print(type.getTypeSignature().toLowerCase().replaceAll("\\.", "/"));
		out.print(";");
	}

	public void genFieldInfoTypeName(Part part, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genRuntimeTypeName, part, ctx, out, arg);
	}
}
