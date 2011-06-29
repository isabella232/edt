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
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.UnaryExpression;

public class BooleanTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else
			out.print("false");
	}

	public void genSignature(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		String signature = "";
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			signature += "?";
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			signature += "?";
		signature += "0;";
		out.print(signature);
	}

	public void genUnaryExpression(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		// TODO sbg Confirm with Jeff, possibly incorporate into TypeTypeTemplate.genUnary....
		out.print(((UnaryExpression) args[0]).getOperator() + "(");
		ctx.gen(genExpression, ((UnaryExpression) args[0]).getExpression(), ctx, out, org.eclipse.edt.gen.CommonUtilities.genWithoutTypeList(args));
		out.print(")");
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) {
		if (false) { // TODO sbg other impls of genBinaryExpression consider nullables
		} else {
			out.print(getNativeStringPrefixOperation((BinaryExpression) args[0]));
			out.print("(");
			ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, args);
			out.print(getNativeStringOperation((BinaryExpression) args[0]));
			ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, args);
			out.print(getNativeStringComparisionOperation((BinaryExpression) args[0]));
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
