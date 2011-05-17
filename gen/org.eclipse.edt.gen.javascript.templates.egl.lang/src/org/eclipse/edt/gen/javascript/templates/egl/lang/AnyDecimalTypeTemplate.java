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

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class AnyDecimalTypeTemplate extends JavaScriptTemplate {

	// this method gets invoked when there is a specific fixed precision needed
	public void genDefaultValue(FixedPrecisionType type, Context ctx, TabbedWriter out, Object... args) {
		processDefaultValue(type, ctx, out, args);
	}

	// this method gets invoked when there is a generic (unknown) fixed precision needed
	public void genDefaultValue(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		processDefaultValue(type, ctx, out, args);
	}

	public void genSignature(Type type, Context ctx, TabbedWriter out, Object... args) {
		out.print(quoted("d;"));
	}

	public void processDefaultValue(Type type, Context ctx, TabbedWriter out, Object... args) {
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
		if (TypeUtils.isNumericType(fromType))
			return true;
		return false;
	}

	public void genConversionOperation(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		// can we intercept and directly generate this conversion
		if (((AsExpression) args[0]).getConversionOperation() != null && needsConversion(((AsExpression) args[0]).getConversionOperation())) {
			out.print("egl.convert"
				+ CommonUtilities.getEglNameForTypeCamelCase(((AsExpression) args[0]).getConversionOperation().getParameters().get(0).getType()) + "To"
				+ CommonUtilities.getEglNameForTypeCamelCase(type) + "(");
			ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
			ctx.gen(genTypeDependentOptions, ((AsExpression) args[0]).getEType(), ctx, out, args);
			out.print(", egl.createRuntimeException)");
		} else {
			// we need to invoke the logic in type template to call back to the other conversion situations
			ctx.genSuper(genConversionOperation, ParameterizableType.class, type, ctx, out, args);
		}
	}

	public void genDecimalConversion(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
	}

	public void genStringConversion(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		out.print("egl.convertStringToDecimal(");
		ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
		ctx.gen(genTypeDependentOptions, ((AsExpression) args[0]).getEType(), ctx, out, args);
		out.print(")");
	}

	public void genTypeDependentOptions(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		out.print(", ");
		// if we get here, then we have been given an integer literal, to be represented as a FixedPrecisionType. So, we must
		// set the dependend options to be a list of nines
		if (((AsExpression) args[0]).getObjectExpr() instanceof IntegerLiteral) {
			String value = ((IntegerLiteral) ((AsExpression) args[0]).getObjectExpr()).getValue();
			if (value.startsWith("-"))
				value = value.substring(1);
			if (value.length() > 4)
				out.print("egl.javascript.BigDecimal.prototype.NINES[8]");
			else
				out.print("egl.javascript.BigDecimal.prototype.NINES[3]");
		} else
			out.print("egl.javascript.BigDecimal.prototype.NINES[8]");
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
