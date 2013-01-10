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
package org.eclipse.edt.gen.javascript.templates.eglx.lang;

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Type;

public class BigintTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out) {
		out.print(Constants.JSRT_EGL_NAMESPACE + ctx.getNativeMapping("eglx.lang.EBigint") + ".ZERO");
	}

	public void genSignature(EGLClass type, Context ctx, TabbedWriter out) {
		String signature = "B;";
		out.print(signature);
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, BinaryExpression arg) {
		out.print(getNativeStringPrefixOperation(arg));
		out.print("(");
		ctx.invoke(genExpression, arg.getLHS(), ctx, out, arg.getOperation().getParameters().get(0));
		out.print(getNativeStringOperation(arg));
		ctx.invoke(genExpression, arg.getRHS(), ctx, out, arg.getOperation().getParameters().get(1));
		out.print(getNativeStringComparisionOperation(arg));
		out.print(")");
	}

	@SuppressWarnings("static-access")
	protected String getNativeStringPrefixOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(expr.Op_POWER))
			return "egl.eglx.lang.EInt64.pow";
		if (op.equals(expr.Op_EQ))
			return "egl.eglx.lang.EInt64.equals";
		if (op.equals(expr.Op_NE))
			return "egl.eglx.lang.EInt64.notEquals";
		return "";
	}

	@SuppressWarnings("static-access")
	protected String getNativeStringOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		// these are the defaults for what can be handled by the java string class
		if (op.equals(expr.Op_PLUS))
			return ".add(";
		if (op.equals(expr.Op_MINUS))
			return ".subtract(";
		if (op.equals(expr.Op_MULTIPLY))
			return ".multiply(";
		if (op.equals(expr.Op_DIVIDE))
			return ".divide(";
		if (op.equals(expr.Op_MODULO))
			return ".remainder(";
		if (op.equals(expr.Op_EQ))
			return ",";
		if (op.equals(expr.Op_NE))
			return ",";
		if (op.equals(expr.Op_LT))
			return ".compareTo(";
		if (op.equals(expr.Op_GT))
			return ".compareTo(";
		if (op.equals(expr.Op_LE))
			return ".compareTo(";
		if (op.equals(expr.Op_GE))
			return ".compareTo(";
		if (op.equals(expr.Op_AND))
			return " && ";
		if (op.equals(expr.Op_OR))
			return " || ";
		if (op.equals(expr.Op_CONCAT))
			return " + ";
		if (op.equals(expr.Op_POWER))
			return ",";
		return "";
	}

	@SuppressWarnings("static-access")
	protected String getNativeStringComparisionOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(expr.Op_PLUS))
			return ")";
		if (op.equals(expr.Op_MINUS))
			return ")";
		if (op.equals(expr.Op_MULTIPLY))
			return ")";
		if (op.equals(expr.Op_DIVIDE))
			return ")";
		if (op.equals(expr.Op_MODULO))
			return ")";
		if (op.equals(expr.Op_LT))
			return ") < 0";
		if (op.equals(expr.Op_GT))
			return ") > 0";
		if (op.equals(expr.Op_LE))
			return ") <= 0";
		if (op.equals(expr.Op_GE))
			return ") >= 0";
		return "";
	}
}
