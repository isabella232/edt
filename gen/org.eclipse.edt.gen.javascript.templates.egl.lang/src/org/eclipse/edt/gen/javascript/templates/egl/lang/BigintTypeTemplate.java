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
import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class BigintTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else
			out.print("egl.javascript.BigDecimal.prototype.ZERO");
	}

	protected boolean needsConversion(Operation conOp) {
		Type fromType = conOp.getParameters().get(0).getType();
		Type toType = conOp.getReturnType();
		// don't convert matching types
		if (CommonUtilities.getEglNameForTypeCamelCase(toType).equals(CommonUtilities.getEglNameForTypeCamelCase(fromType)))
			return false;
		if (TypeUtils.isNumericType(fromType) && !fromType.equals(TypeUtils.Type_INT) && !fromType.equals(TypeUtils.Type_SMALLINT))
			return true;
		return false;
	}

	public void genConversionOperation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		// can we intercept and directly generate this conversion
		if (((AsExpression) args[0]).getConversionOperation() != null && needsConversion(((AsExpression) args[0]).getConversionOperation())) {
			out.print("egl.convert"
				+ CommonUtilities.getEglNameForTypeCamelCase(((AsExpression) args[0]).getConversionOperation().getParameters().get(0).getType()) + "To"
				+ CommonUtilities.getEglNameForTypeCamelCase(type) + "(");
			ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
			out.print(", egl.createRuntimeException)");
		} else {
			// we need to invoke the logic in type template to call back to the other conversion situations
			ctx.genSuper(genConversionOperation, EGLClass.class, type, ctx, out, args);
		}
	}

	public void genBigintConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
	}

	public void genSmallintConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		out.print("(new egl.javascript.BigDecimal(String(");
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
		out.print(")))");
	}

	public void genIntConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		out.print("(new egl.javascript.BigDecimal(String(");
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
		out.print(")))");
	}

	public void genStringConversion(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		out.print("egl.convertStringToBigint(");
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
		out.print(")");
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
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
			return "!";
		return "";
	}

	@SuppressWarnings("static-access")
	protected String getNativeStringOperation(BinaryExpression expr) {
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
	protected String getNativeStringComparisionOperation(BinaryExpression expr) {
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
}
