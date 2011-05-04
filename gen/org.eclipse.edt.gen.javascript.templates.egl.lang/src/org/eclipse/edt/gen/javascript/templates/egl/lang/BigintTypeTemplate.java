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

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;

public class BigintTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else
			out.print("egl.javascript.BigDecimal.prototype.ZERO");
	}

	public void genBigintFromSmallintConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genBigintFromIntegerConversion(type, ctx, out, args);
	}
	
	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		if (false){ //TODO sbg other impls of genBinaryExpression consider nullables
		}
		else {
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
	private String getNativeStringPrefixOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(expr.Op_NE))
			return "!";
		return "";
	}

	@SuppressWarnings("static-access")
	private String getNativeStringOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		// these are the defaults for what can be handled by the java string class
		if (op.equals(expr.Op_PLUS))
			return " + ";
		if (op.equals(expr.Op_EQ))
			return ".compareTo(";
		if (op.equals(expr.Op_NE))
			return ".compareTo(";
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
	private String getNativeStringComparisionOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(expr.Op_EQ))
			return ") == 0";
		if (op.equals(expr.Op_NE))
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

	
	public void genBigintFromIntConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		genBigintFromIntegerConversion(type, ctx, out, args);
	}

	public void genBigintFromIntegerConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("(new egl.javascript.BigDecimal(String(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(")))");
	}

	public void genBigintFromFloatConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("egl.convertFloatToBigint(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(")");
	}

	public void genBigintFromSmallfloatConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("egl.convertFloatToBigint(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(")");
	}

	public void genBigintFromStringConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		AsExpression expr = (AsExpression) args[0];
		out.print("egl.convertStringToBigint(");
		ctx.gen(genExpression, expr.getObjectExpr(), ctx, out);
		out.print(")");
	}

}
