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
package org.eclipse.edt.gen.javascript;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class CommonUtilities {

	public static String getEglNameForType(Type type) {
		switch (TypeUtils.getTypeKind(type)) {
			case TypeUtils.TypeKind_ANY:
				return "any";
			case TypeUtils.TypeKind_BOOLEAN:
				return "boolean";
			case TypeUtils.TypeKind_BIGINT:
				return "bigint";
			case TypeUtils.TypeKind_DATE:
				return "date";
			case TypeUtils.TypeKind_FLOAT:
				return "float";
			case TypeUtils.TypeKind_DECIMAL:
				return "decimal";
			case TypeUtils.TypeKind_INT:
				return "int";
			case TypeUtils.TypeKind_SMALLFLOAT:
				return "smallfloat";
			case TypeUtils.TypeKind_SMALLINT:
				return "smallint";
			case TypeUtils.TypeKind_STRING:
				return "string";
			case TypeUtils.TypeKind_TIME:
				return "time";
			case TypeUtils.TypeKind_TIMESTAMP:
				return "timeStamp";
			default:
				return "undefined";
		}
	}

	public static String getEglNameForTypeCamelCase(Type type) {
		String name = getEglNameForType(type);
		StringBuilder b = new StringBuilder(name);
		b.setCharAt(0, Character.toUpperCase(name.charAt(0)));
		return b.toString();
	}

	@SuppressWarnings("static-access")
	public static String getNativeRuntimeOperationName(BinaryExpression expr) throws GenerationException {
		// safety check to make sure the operation has been defined properly
		if (expr.getOperation() == null || expr.getOperation().getName() == null)
			throw new GenerationException();
		// process the operator
		String op = expr.getOperator();
		if (op.equals(expr.Op_PLUS))
			return "plus";
		if (op.equals(expr.Op_MINUS))
			return "minus";
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
	public static String getNativeRuntimeComparisionOperation(BinaryExpression expr) {
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
	public static String getNativeJavaOperation(BinaryExpression expr, Context ctx) {
		String op = expr.getOperator();
		// if we are to use egl overflow checking, then don't pass back that we can do the mathematical operations in java
		if (expr.isNullable() || (Boolean) ctx.getParameter(Constants.parameter_checkOverflow)) {
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
}
