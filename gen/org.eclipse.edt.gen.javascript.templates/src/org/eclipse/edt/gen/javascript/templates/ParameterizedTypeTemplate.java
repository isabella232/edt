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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.ParameterizedType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.UnaryExpression;

public class ParameterizedTypeTemplate extends JavaScriptTemplate {

	public void genAssignment(ParameterizedType type, Context ctx, TabbedWriter out, Expression arg1, Expression arg2, String arg3) {
		ctx.invoke(genAssignment, (Type) type.getParameterizableType(), ctx, out, arg1, arg2, arg3);
	}

	public void genRuntimeTypeName(ParameterizedType type, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invokeSuper(this, genRuntimeTypeName, type.getParameterizableType(), ctx, out, arg);
	}

	public void genConstructorOptions(ParameterizedType type, Context ctx, TabbedWriter out) {
		ctx.invoke(genTypeDependentOptions, type, ctx, out);
	}

	public void genBinaryExpression(ParameterizedType type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// for interval type, always use the runtime
		out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
		out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
		out.print("(ezeProgram, ");
		ctx.invoke(genExpression, arg.getLHS(), ctx, out, arg.getOperation().getParameters().get(0));
		out.print(", ");
		ctx.invoke(genExpression, arg.getRHS(), ctx, out, arg.getOperation().getParameters().get(0));
		out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
	}

	public void genUnaryExpression(ParameterizedType type, Context ctx, TabbedWriter out, UnaryExpression arg) {
		// we only need to check for minus sign and if found, we need to change it to .negate()
		if (arg.getOperator().equals("-")) {
			ctx.invoke(genExpression, arg.getExpression(), ctx, out, arg.getExpression());
			out.print(".negate()");
		} else if (arg.getOperator().equals("~")) {
			out.print("(");
			ctx.invoke(genExpression, arg.getExpression(), ctx, out, arg.getExpression());
			out.print(".negate()");
			out.print(" - 1)");
		} else
			ctx.invoke(genExpression, arg.getExpression(), ctx, out);
	}
}
