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

import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FixedPrecisionType;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class FloatTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(EGLClass type, Context ctx, TabbedWriter out) {
		out.print("0");
	}

	public void genSignature(EGLClass type, Context ctx, TabbedWriter out) {
		String signature = "F;";
		out.print(signature);
	}

	protected boolean needsConversion(Operation conOp) {
		boolean result = true;
		Type fromType = conOp.getParameters().get(0).getType();
		Type toType = conOp.getReturnType();
		// don't convert matching types
		if (CommonUtilities.getEglNameForTypeCamelCase(toType).equals(CommonUtilities.getEglNameForTypeCamelCase(fromType)))
			result = false;
		if (toType.getTypeSignature().equalsIgnoreCase("eglx.lang.ENumber"))
			result = true;
		else if (TypeUtils.isNumericType(fromType) && CommonUtilities.isJavaScriptNumber(fromType))
			result = conOp.isNarrowConversion();
		return result;
	}

	public void genConversionOperation(EGLClass type, Context ctx, TabbedWriter out, AsExpression arg) {
		if (arg.getConversionOperation() != null && !needsConversion(arg.getConversionOperation())) {
			ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
		} else {
			// we need to invoke the logic in type template to call back to the other conversion situations
			ctx.invokeSuper(this, genConversionOperation, type, ctx, out, arg);
		}
	}

	protected boolean needsConversion(Type fromType, Type toType) {
		boolean result = true;
		if (TypeUtils.isNumericType(fromType) && !CommonUtilities.needsConversion(fromType, toType))
			result = false;
		return result;
	}

	public void genConversionOperation(FixedPrecisionType type, Context ctx, TabbedWriter out, AsExpression arg) {
		Type toType = arg.getEType();
		Type fromType = arg.getObjectExpr().getType();
		if ((arg.getConversionOperation() == null) && TypeUtils.isNumericType(fromType)) {
			if (needsConversion(fromType, toType)) {
				out.print(ctx.getNativeImplementationMapping(toType) + '.');
				out.print(CommonUtilities.getOpName(ctx, arg.getConversionOperation()));
				out.print("(");
				Expression objectExpr = arg.getObjectExpr();
				if (objectExpr instanceof BoxingExpression){
					objectExpr = ((BoxingExpression)objectExpr).getExpr();
				}
				ctx.invoke(genExpression, objectExpr, ctx, out);
				ctx.invoke(genTypeDependentOptions, arg.getEType(), ctx, out, arg);
				out.print(")");
			} else {
				ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
			}
		} else {
			// we need to invoke the logic in type template to call back to the other conversion situations
			ctx.invokeSuper(this, genConversionOperation, type, ctx, out, arg);
		}
	}

	public void genTypeDependentOptions(ParameterizableType type, Context ctx, TabbedWriter out, AsExpression arg) {
		out.print(", ");
		// if we get here, then we have been given an integer literal, to be represented as a FixedPrecisionType. So, we must
		// set the dependend options to be a list of nines
		if (arg.getObjectExpr() instanceof IntegerLiteral) {
			String value = ((IntegerLiteral) arg.getObjectExpr()).getValue();
			if (value.startsWith("-"))
				value = value.substring(1);
			if (value.length() > 4)
				out.print("egl.javascript.BigDecimal.prototype.NINES[8]");
			else
				out.print("egl.javascript.BigDecimal.prototype.NINES[3]");
		} else
			out.print("egl.javascript.BigDecimal.prototype.NINES[8]");
	}

	public void genTypeDependentOptions(ParameterizableType type, Context ctx, TabbedWriter out) {
		out.print(", ");
		out.print("egl.javascript.BigDecimal.prototype.NINES[8]");
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
		if (op.equals(expr.Op_NE))
			return "";
		if (op.equals(expr.Op_MODULO))
			return "egl.remainder";
		if (op.equals(expr.Op_POWER))
			return "egl.eglx.lang.EFloat64.pow";
		if (op.equals(expr.Op_DIVIDE))
			return "egl.divide";
		return "";
	}

	@SuppressWarnings("static-access")
	protected String getNativeStringOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		// these are the defaults for what can be handled by the java string class
		if (op.equals(expr.Op_PLUS))
			return " + ";
		if (op.equals(expr.Op_MINUS))
			return " - ";
		if (op.equals(expr.Op_MULTIPLY))
			return " * ";
		if (op.equals(expr.Op_DIVIDE))
			return " ,";
		if (op.equals(expr.Op_EQ))
			return " == ";
		if (op.equals(expr.Op_NE))
			return " != ";
		if (op.equals(expr.Op_LT))
			return " < ";
		if (op.equals(expr.Op_GT))
			return " > ";
		if (op.equals(expr.Op_LE))
			return " <= ";
		if (op.equals(expr.Op_GE))
			return " >= ";
		if (op.equals(expr.Op_AND))
			return " && ";
		if (op.equals(expr.Op_OR))
			return " || ";
		if (op.equals(expr.Op_CONCAT))
			return " + ";
		if (op.equals(expr.Op_MODULO))
			return ",";
		if (op.equals(expr.Op_POWER))
			return ",";
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
			return "";
		if (op.equals(expr.Op_GT))
			return "";
		if (op.equals(expr.Op_LE))
			return "";
		if (op.equals(expr.Op_GE))
			return "";
		if (op.equals(expr.Op_MODULO))
			return "";
		return "";
	}

}
