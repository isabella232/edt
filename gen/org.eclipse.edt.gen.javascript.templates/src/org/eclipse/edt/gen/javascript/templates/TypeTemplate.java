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
package org.eclipse.edt.gen.javascript.templates;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.EObject;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Operation;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;
import org.eclipse.edt.mof.egl.UnaryExpression;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public abstract class TypeTemplate extends JavascriptTemplate {

	public void validate(Type type, Context ctx, Object... args) {
		// types may override this validation for specific checking
		}

	public void genInstantiation(Type type, Context ctx, TabbedWriter out, Object... args) {
		out.print("new ");
		ctx.gen(genRuntimeTypeName, (EObject) type, ctx, out, RuntimeTypeNameKind.JavascriptImplementation);
		out.print("(");
		ctx.gen(genConstructorOptions, (EObject) type, ctx, out, args);
		out.print(")");
	}

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else if (isReferenceType(type))
			out.print("null");
		else
			out.print("\"Invalid default value\"");
	}
	
	public void genConversion(Type type, Context ctx, TabbedWriter out, Object...args) {
		AsExpression asExpr = (AsExpression)args[0];
		if (needsConversion(asExpr)) {
			Operation conOp = asExpr.getConversionOperation();
			Type fromType = conOp.getParameters().get(0).getType();
			Type toType = conOp.getReturnType();
			String fromName = getEglNameForTypeCamelCase(fromType);
			String toName = getEglNameForTypeCamelCase(toType);
			String methodName = "gen";
			methodName += toName;
			methodName += "From";
			methodName += fromName;
			methodName += "Conversion";
			ctx.gen(methodName, (Type)conOp.getContainer(), ctx, out, args);
		}
		else {
			ctx.gen(genExpression, asExpr.getObjectExpr(), ctx, out, args);
		}

	}

	public void genDeclaration(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
	// nothing to do here
	}

	public void genGetterSetter(Type type, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
	// nothing to do here
	}


	public void genConstructorOptions(Type type, Context ctx, TabbedWriter out, Object... args) {
	// nothing to do here
	}

	public void genTypeDependentOptions(Type type, Context ctx, TabbedWriter out, Object... args) {
	// nothing to do here
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		// if either side of this expression is nullable, or if there is no direct java operation, we need to use the runtime
		if ((((BinaryExpression) args[0]).getLHS().isNullable() || ((BinaryExpression) args[0]).getRHS().isNullable())
			|| getNativeJavascriptOperation((BinaryExpression) args[0], ctx).length() == 0) {
			out.print(ctx.getNativeImplementationMapping((Type) ((BinaryExpression) args[0]).getOperation().getContainer()) + '.');
			out.print(getNativeRuntimeOperationName((BinaryExpression) args[0]));
			out.print("(ezeProgram, ");
			ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, args);
			out.print(", ");
			ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, args);
			out.print(")" + getNativeRuntimeComparisionOperation((BinaryExpression) args[0]));
		} else {
			ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, args);
			out.print(getNativeJavascriptOperation((BinaryExpression) args[0], ctx));
			ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, args);
		}
	}

	public void genUnaryExpression(Type type, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		// we only need to check for minus sign and if found, we need to change it to -()
		if (((UnaryExpression) args[0]).getOperator().equals("-"))
			out.print(((UnaryExpression) args[0]).getOperator() + "(");
		ctx.gen(genExpression, ((UnaryExpression) args[0]).getExpression(), ctx, out, args);
		// we only need to check for minus sign and if found, we need to change it to -()
		if (((UnaryExpression) args[0]).getOperator().equals("-"))
			out.print(")");
	}

	@SuppressWarnings("static-access")
	public String getNativeRuntimeOperationName(BinaryExpression expr) throws GenerationException {
		// safety check to make sure the operation has been defined properly
		if (expr.getOperation() == null || expr.getOperation().getName() == null)
			throw new GenerationException();
		// process the operator
		String op = expr.getOperator();
		if (op.equals(expr.Op_PLUS))
			return "add";
		if (op.equals(expr.Op_MINUS))
			return "subtract";
		if (op.equals(expr.Op_DIVIDE))
			return "divide";
		if (op.equals(expr.Op_MULTIPLY))
			return "multiply";
		if (op.equals(expr.Op_MODULO))
			return "modulo";
		if (op.equals(expr.Op_EQ))
			return "equals";
		if (op.equals(expr.Op_NE))
			return "notEquals";
		if (op.equals(expr.Op_LT))
			return "compareTo";
		if (op.equals(expr.Op_GT))
			return "compareTo";
		if (op.equals(expr.Op_LE))
			return "compareTo";
		if (op.equals(expr.Op_GE))
			return "compareTo";
		if (op.equals(expr.Op_AND))
			return "and";
		if (op.equals(expr.Op_OR))
			return "or";
		if (op.equals(expr.Op_XOR))
			return "xor";
		if (op.equals(expr.Op_CONCAT))
			return "concat";
		if (op.equals(expr.Op_NULLCONCAT))
			return "concat";
		if (op.equals(expr.Op_BITAND))
			return "bitand";
		if (op.equals(expr.Op_BITOR))
			return "bitor";
		if (op.equals(expr.Op_POWER))
			return "power";
		if (op.equals(expr.Op_IN))
			return "in";
		if (op.equals(expr.Op_MATCHES))
			return "matches";
		if (op.equals(expr.Op_LIKE))
			return "like";
		return "UnknownOp";
	}

	@SuppressWarnings("static-access")
	public String getNativeRuntimeComparisionOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(expr.Op_LT))
			return " < 0";
		if (op.equals(expr.Op_GT))
			return " > 0";
		if (op.equals(expr.Op_LE))
			return " <= 0";
		if (op.equals(expr.Op_GE))
			return " >= 0";
		return "";
	}

	@SuppressWarnings("static-access")
	public String getNativeJavascriptOperation(BinaryExpression expr, Context ctx) {
		String op = expr.getOperator();
		// if we are to use egl overflow checking, then don't pass back that we can do the mathematical operations in java
		if (expr.isNullable()
			|| (ctx.get(Constants.CONTEXT_USE_EGL_OVERFLOW) != null && ((Boolean) ctx.get(Constants.CONTEXT_USE_EGL_OVERFLOW)).booleanValue())) {
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
			if (op.equals(expr.Op_XOR))
				return " ^ ";
			if (op.equals(expr.Op_CONCAT))
				return " + ";
			if (op.equals(expr.Op_BITAND))
				return " & ";
			if (op.equals(expr.Op_BITOR))
				return " | ";
			return "";
		}
		// these are the defaults for all other types
		// division is intentionally left off as all division must be done through the egl runtime
		if (op.equals(expr.Op_PLUS))
			return " + ";
		if (op.equals(expr.Op_MINUS))
			return " - ";
		if (op.equals(expr.Op_MULTIPLY))
			return " * ";
		if (op.equals(expr.Op_MODULO))
			return " % ";
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
		if (op.equals(expr.Op_XOR))
			return " ^ ";
		if (op.equals(expr.Op_CONCAT))
			return " + ";
		if (op.equals(expr.Op_BITAND))
			return " & ";
		if (op.equals(expr.Op_BITOR))
			return " | ";
		return "";
	}
	
	public String getEglNameForType(Type type) {
		switch (TypeUtils.getTypeKind(type)) {
		case TypeUtils.TypeKind_ANY: return "any";
		case TypeUtils.TypeKind_BOOLEAN: return "boolean";
		case TypeUtils.TypeKind_BIGINT: return "bigint";
		case TypeUtils.TypeKind_DATE: return "date";
		case TypeUtils.TypeKind_FLOAT: return "float";
		case TypeUtils.TypeKind_DECIMAL: return "decimal";
		case TypeUtils.TypeKind_INT: return "int";
		case TypeUtils.TypeKind_SMALLFLOAT: return "smallfloat";
		case TypeUtils.TypeKind_SMALLINT: return "smallint";
		case TypeUtils.TypeKind_STRING: return "string";
		case TypeUtils.TypeKind_TIME: return "time";
		case TypeUtils.TypeKind_TIMESTAMP: return "timeStamp";
		default: return "undefined";
		}
	}
	
	public String getEglNameForTypeCamelCase(Type type) {
		String name = getEglNameForType(type);
		StringBuilder b = new StringBuilder(name);
		b.setCharAt(0, Character.toUpperCase(name.charAt(0)));
		return b.toString();
	}

	/**
	 * Check whether a conversion of the AsExpression is actually necessary.
	 * This is based on the implementations of these types in JavaScript and
	 * the kind of conversion that is defined, i.e. either widening or narrowing
	 * 
	 * @param asExpr
	 * @return
	 */
	public boolean needsConversion(AsExpression asExpr) {
		Operation op = asExpr.getConversionOperation();
		Type fromType = op.getParameters().get(0).getType();
		Type toType = op.getReturnType();
		// Always do conversions if parameterized types are involved
		if (toType.equals(TypeUtils.Type_DECIMAL) 
				|| toType.equals(TypeUtils.Type_TIMESTAMP)
				|| TypeUtils.isTextType(toType)) {
			return true;
		}
		if (op.isWidenConversion()) {
			if (TypeUtils.isNumericType(fromType)) {
				int kind = TypeUtils.getTypeKind(toType);
				return kind == TypeUtils.TypeKind_DECIMAL 
					|| kind == TypeUtils.TypeKind_BIGINT
					|| kind == TypeUtils.TypeKind_DATE
					|| TypeUtils.isTextType(toType);
			}
			if (TypeUtils.isTextType(toType)) {
				return true;
			}
		}
		else {
			if (TypeUtils.isNumericType(fromType)) {
				return !(fromType.equals(TypeUtils.Type_INT)
					&& toType.equals(TypeUtils.Type_SMALLINT));
			}
			else if (fromType.equals(TypeUtils.Type_TIMESTAMP)) {
				return !toType.equals(TypeUtils.Type_TIMESTAMP);

			}
		}
		return true;
	}
}
