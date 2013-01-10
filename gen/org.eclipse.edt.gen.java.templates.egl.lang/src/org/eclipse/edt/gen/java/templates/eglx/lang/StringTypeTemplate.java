/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
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
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.Type;

public class StringTypeTemplate extends JavaTemplate {

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out) {
		out.print("\"\"");
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// if either side of this expression is nullable, or if there is no direct java operation, we need to use the runtime
		if ((arg.getLHS().isNullable() || arg.getRHS().isNullable()) || getNativeStringOperation(arg).length() == 0) {
			out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
			out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
			out.print("(");
			ctx.invoke(genExpression, arg.getLHS(), ctx, out, arg.getOperation().getParameters().get(0));
			out.print(", ");
			ctx.invoke(genExpression, arg.getRHS(), ctx, out, arg.getOperation().getParameters().get(1));
			out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
		} else {
			out.print(getNativeStringPrefixOperation(arg));
			out.print("(");
			ctx.invoke(genExpression, arg.getLHS(), ctx, out);
			out.print(")");
			out.print(getNativeStringOperation(arg));
			ctx.invoke(genExpression, arg.getRHS(), ctx, out);
			out.print(getNativeStringComparisionOperation(arg));
		}
	}

	public void genContainerBasedInvocation(Type type, Context ctx, TabbedWriter out, InvocationExpression expr) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".");
		ctx.invoke(genName, expr.getTarget(), ctx, out);
		out.print("(");
		ctx.invoke(genInvocationNonstaticArgument, expr, ctx, out);
		ctx.invoke(genInvocationArguments, expr, ctx, out);
		out.print(")");
	}

	protected String getNativeStringPrefixOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(BinaryExpression.Op_NE))
			return "!";
		return "";
	}

	protected String getNativeStringOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		// these are the defaults for what can be handled by the java string class
		if (op.equals(BinaryExpression.Op_PLUS))
			return " + ";
		if (op.equals(BinaryExpression.Op_EQ))
			return ".equals(";
		if (op.equals(BinaryExpression.Op_NE))
			return ".equals(";
		if (op.equals(BinaryExpression.Op_LT))
			return ".compareTo(";
		if (op.equals(BinaryExpression.Op_GT))
			return ".compareTo(";
		if (op.equals(BinaryExpression.Op_LE))
			return ".compareTo(";
		if (op.equals(BinaryExpression.Op_GE))
			return ".compareTo(";
		if (op.equals(BinaryExpression.Op_AND))
			return " && ";
		if (op.equals(BinaryExpression.Op_OR))
			return " || ";
		if (op.equals(BinaryExpression.Op_CONCAT))
			return " + ";
		return "";
	}

	protected String getNativeStringComparisionOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(BinaryExpression.Op_EQ))
			return ")";
		if (op.equals(BinaryExpression.Op_NE))
			return ")";
		if (op.equals(BinaryExpression.Op_LT))
			return ") < 0";
		if (op.equals(BinaryExpression.Op_GT))
			return ") > 0";
		if (op.equals(BinaryExpression.Op_LE))
			return ") <= 0";
		if (op.equals(BinaryExpression.Op_GE))
			return ") >= 0";
		return "";
	}
}
