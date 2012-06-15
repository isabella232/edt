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

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.IsAExpression;
import org.eclipse.edt.mof.egl.Type;

public class AnyTypeTemplate extends JavaTemplate {

	public void genInstantiation(EGLClass type, Context ctx, TabbedWriter out) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.EAny"))
			out.print("null");
		else
			ctx.invokeSuper(this, genInstantiation, type, ctx, out);
	}

	public void genBinaryExpression(EGLClass type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.EAny")) {
			out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
			out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
			out.print("(");
			ctx.invoke(genExpression, arg.getLHS(), ctx, out, arg.getOperation().getParameters().get(0));
			out.print(", ");
			ctx.invoke(genExpression, arg.getRHS(), ctx, out, arg.getOperation().getParameters().get(1));
			out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
		} else
			ctx.invokeSuper(this, genBinaryExpression, type, ctx, out, arg);
	}

	public void genIsaExpression(EGLClass type, Context ctx, TabbedWriter out, IsAExpression arg) {
		if (type.getTypeSignature().equalsIgnoreCase("eglx.lang.EAny")) {
			if (arg.getObjectExpr().getType().getTypeSignature().equalsIgnoreCase(arg.getEType().getTypeSignature())) {
				if (arg.getObjectExpr().isNullable()) {
					out.print("(");
					ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
					out.print(" == null ? false : true)");
				} else
					out.print("true");
			} else if (arg.getObjectExpr().getType().getClassifier() != null
				&& arg.getObjectExpr().getType().getClassifier().getTypeSignature().equalsIgnoreCase(arg.getEType().getTypeSignature())) {
				if (arg.getObjectExpr().isNullable()) {
					out.print("(");
					ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
					out.print(" == null ? false : true)");
				} else
					out.print("true");
			} else if (arg.getObjectExpr().getType().getClassifier() != null && arg.getEType().getClassifier() != null
				&& arg.getObjectExpr().getType().getClassifier().getTypeSignature().equalsIgnoreCase(arg.getEType().getClassifier().getTypeSignature())) {
				out.print("false");
			} else {
				out.print("org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeIsa(");
				ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
				out.print(", ");
				ctx.invoke(genRuntimeClassTypeName, arg.getEType(), ctx, out, TypeNameKind.EGLImplementation);
				out.print(")");
			}
		} else
			ctx.invokeSuper(this, genIsaExpression, type, ctx, out, arg);
	}
}
