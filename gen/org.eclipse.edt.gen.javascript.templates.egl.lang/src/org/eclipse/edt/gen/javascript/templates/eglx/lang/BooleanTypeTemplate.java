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
package org.eclipse.edt.gen.javascript.templates.eglx.lang;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.UnaryExpression;

public class BooleanTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out) {
		out.print("false");
	}

	public void genSignature(EGLClass type, Context ctx, TabbedWriter out) {
		String signature = "0;";
		out.print(signature);
	}

	public void genUnaryExpression(EGLClass type, Context ctx, TabbedWriter out, UnaryExpression arg) {
		// TODO sbg Confirm with Jeff, possibly incorporate into TypeTypeTemplate.genUnary....
		out.print(arg.getOperator() + "(");
		ctx.invoke(genExpression, arg.getExpression(), ctx, out);
		out.print(")");
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, BinaryExpression arg) {
		if (false) { // TODO sbg other impls of genBinaryExpression consider nullables
		} else {
			out.print(getNativeStringPrefixOperation(arg));
			out.print("(");
			ctx.invoke(genExpression, arg.getLHS(), ctx, out);
			out.print(getNativeStringOperation(arg));
			ctx.invoke(genExpression, arg.getRHS(), ctx, out);
			out.print(getNativeStringComparisionOperation(arg));
			out.print(")");
		}
	}
	
	@SuppressWarnings("static-access")
	protected String getNativeStringPrefixOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(expr.Op_NE))
			return "";
		return "";
	}

	@SuppressWarnings("static-access")
	protected String getNativeStringOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		// these are the defaults for what can be handled by the java string class
		if (op.equals(expr.Op_PLUS))
			return " + ";
		if (op.equals(expr.Op_EQ))
			return " == ";
		if (op.equals(expr.Op_NE))
			return " != ";
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
		return "";
	}

	@SuppressWarnings("static-access")
	protected String getNativeStringComparisionOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(expr.Op_EQ))
			return "";
		if (op.equals(expr.Op_NE))
			return "";
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
