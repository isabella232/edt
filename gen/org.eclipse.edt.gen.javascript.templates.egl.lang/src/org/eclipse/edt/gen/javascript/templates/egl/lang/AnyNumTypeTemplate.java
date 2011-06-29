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
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class AnyNumTypeTemplate extends JavaScriptTemplate {

	// this method gets invoked when there is a specific fixed precision needed
	public void genDefaultValue(FixedPrecisionType type, Context ctx, TabbedWriter out, Object... args) {
		processDefaultValue(type, ctx, out, args);
	}

	// this method gets invoked when there is a generic (unknown) fixed precision needed
	public void genDefaultValue(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		processDefaultValue(type, ctx, out, args);
	}

	public void processDefaultValue(Type type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else
			out.print("egl.javascript.BigDecimal.prototype.ZERO");
	}

	// this method gets invoked when there is a specific fixed precision needed
	public void genSignature(FixedPrecisionType type, Context ctx, TabbedWriter out, Object... args) {
		String signature = "";
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			signature += "?";
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			signature += "?";
		signature += "N" + type.getLength() + ":" + type.getDecimals() + ";";
		out.print(signature);
	}

	// this method gets invoked when there is a generic (unknown) fixed precision needed
	public void genSignature(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		String signature = "";
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			signature += "?";
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			signature += "?";
		signature += "N;";
		out.print(signature);
	}

	protected boolean needsConversion(Type fromType, Type toType) {
		boolean result = true;
		if (TypeUtils.isNumericType(fromType) && !CommonUtilities.needsConversion(fromType, toType))
			result = CommonUtilities.isJavaScriptBigDecimal(toType);
		return result;
	}

	public void genConversionOperation(FixedPrecisionType type, Context ctx, TabbedWriter out, Object... args) {
		AsExpression asExpr = (AsExpression) args[0];
		Type toType = asExpr.getEType();
		Type fromType = asExpr.getObjectExpr().getType();
		if ((asExpr.getConversionOperation() == null) && TypeUtils.isNumericType(fromType)) {
			if (needsConversion(fromType, toType)) {
				out.print(ctx.getNativeImplementationMapping(toType) + '.');
				out.print("from");
				out.print(ctx.getNativeTypeName(fromType));
				out.print("(");
				ctx.gen(genExpression, ((AsExpression) args[0]).getObjectExpr(), ctx, out, args);
				ctx.gen(genTypeDependentOptions, ((AsExpression) args[0]).getEType(), ctx, out, args);
				out.print(")");
			} else {
				ctx.gen(genExpression, asExpr.getObjectExpr(), ctx, out, args);
			}
		} else {
			// we need to invoke the logic in type template to call back to the other conversion situations
			ctx.genSuper(genConversionOperation, FixedPrecisionType.class, type, ctx, out, args);
		}
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
