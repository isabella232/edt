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
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IsAExpression;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.Type;

public class DateTypeTemplate extends JavaTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".defaultValue()");
	}

	public void genContainerBasedNewExpression(EGLClass type, Context ctx, TabbedWriter out, NewExpression arg) throws GenerationException {
		ctx.invoke(genRuntimeTypeName, arg.getType(), ctx, out, TypeNameKind.EGLImplementation);
		out.print(".defaultValue(");
		if (arg.getArguments() != null && arg.getArguments().size() > 0)
			ctx.foreach(arg.getArguments(), ',', genExpression, ctx, out);
		else
			ctx.invoke(genConstructorOptions, arg.getType(), ctx, out);
		out.print(")");
	}

	public void genBinaryExpression(EGLClass type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// for date type, always use the runtime
		out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
		out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
		out.print("(");
		ctx.invoke(genExpression, arg.getLHS(), ctx, out);
		out.print(", ");
		ctx.invoke(genExpression, arg.getRHS(), ctx, out);
		out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
	}

	public void genContainerBasedInvocation(EGLClass type, Context ctx, TabbedWriter out, InvocationExpression expr) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".");
		ctx.invoke(genName, expr.getTarget(), ctx, out);
		out.print("(");
		// do a callout to allow certain source types to decide to create a boxing expression
		ctx.invoke(genContainerBasedInvocationBoxing, type, ctx, out, expr);
		// then process the expression
		ctx.invoke(genExpression, expr.getQualifier(), ctx, out);
		if (expr.getArguments() != null && expr.getArguments().size() > 0)
			out.print(", ");
		ctx.invoke(genInvocationArguments, expr, ctx, out);
		out.print(")");
	}

	public void genAsExpressionBoxing(EGLClass type, Context ctx, TabbedWriter out, AsExpression arg) {
		if (!(arg.getObjectExpr() instanceof BoxingExpression)) {
			BoxingExpression box = factory.createBoxingExpression();
			box.setExpr(arg.getObjectExpr());
			arg.setObjectExpr(box);
		}
	}

	public void genIsaExpressionBoxing(EGLClass type, Context ctx, TabbedWriter out, IsAExpression arg) {
		if (!(arg.getObjectExpr() instanceof BoxingExpression)) {
			BoxingExpression box = factory.createBoxingExpression();
			box.setExpr(arg.getObjectExpr());
			arg.setObjectExpr(box);
		}
	}

	public void genContainerBasedInvocationBoxing(Type type, Context ctx, TabbedWriter out, QualifiedFunctionInvocation arg) {
		if (!(arg.getQualifier() instanceof BoxingExpression)) {
			BoxingExpression box = factory.createBoxingExpression();
			box.setExpr(arg.getQualifier());
			arg.setQualifier(box);
		}
		if (arg.getArguments() != null && arg.getArguments().size() > 0) {
			// check each of the arguments and box if necessary
			for (int i = 0; i < arg.getArguments().size(); i++) {
				// do a callout to allow certain source types to decide to create a boxing expression
				ctx.invoke(genInvocationArgumentBoxing, arg.getArguments().get(i).getType(), ctx, out, arg, new Integer(i));
			}
		}
	}

	public void genInvocationArgumentBoxing(Type type, Context ctx, TabbedWriter out, QualifiedFunctionInvocation arg1, Integer arg2) {
		if (!(arg1.getArguments().get(arg2) instanceof BoxingExpression)) {
			BoxingExpression box = factory.createBoxingExpression();
			box.setExpr(arg1.getArguments().get(arg2));
			arg1.getArguments().set(arg2, box);
		}
	}
}
