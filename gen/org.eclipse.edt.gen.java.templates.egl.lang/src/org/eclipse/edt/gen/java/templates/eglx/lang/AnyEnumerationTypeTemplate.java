/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.gen.java.templates.eglx.lang;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.Type;

public class AnyEnumerationTypeTemplate extends JavaTemplate {

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		ctx.invoke(genExpression, arg.getLHS(), ctx, out);
		out.print(getNativeEnumerationOperation(arg, ctx));
		ctx.invoke(genExpression, arg.getRHS(), ctx, out);
	}

	protected String getNativeEnumerationOperation(BinaryExpression expr, Context ctx) {
		String op = expr.getOperator();
		// if we are to use egl overflow checking, then don't pass back that we can do the mathematical operations in java
		if (expr.isNullable() || (Boolean) ctx.getParameter(Constants.parameter_checkOverflow)) {
			if (op.equals(BinaryExpression.Op_EQ))
				return " == ";
			if (op.equals(BinaryExpression.Op_NE))
				return " != ";
			if (op.equals(BinaryExpression.Op_LT))
				return " < ";
			if (op.equals(BinaryExpression.Op_GT))
				return " > ";
			if (op.equals(BinaryExpression.Op_LE))
				return " <= ";
			if (op.equals(BinaryExpression.Op_GE))
				return " >= ";
			if (op.equals(BinaryExpression.Op_AND))
				return " && ";
			if (op.equals(BinaryExpression.Op_OR))
				return " || ";
			if (op.equals(BinaryExpression.Op_XOR))
				return " ^ ";
			if (op.equals(BinaryExpression.Op_CONCAT))
				return " + ";
			if (op.equals(BinaryExpression.Op_BITAND))
				return " & ";
			if (op.equals(BinaryExpression.Op_BITOR))
				return " | ";
			if (op.equals(BinaryExpression.Op_LEFTSHIFT))
				return " << ";
			if (op.equals(BinaryExpression.Op_RIGHTSHIFTARITHMETIC))
				return " >> ";
			if (op.equals(BinaryExpression.Op_RIGHTSHIFTLOGICAL))
				return " >>> ";
			return "";
		}
		// these are the defaults for all other types
		// division is intentionally left off as all division must be done through the egl runtime
		if (op.equals(BinaryExpression.Op_PLUS))
			return " + ";
		if (op.equals(BinaryExpression.Op_MINUS))
			return " - ";
		if (op.equals(BinaryExpression.Op_MULTIPLY))
			return " * ";
		if (op.equals(BinaryExpression.Op_MODULO))
			return " % ";
		if (op.equals(BinaryExpression.Op_EQ))
			return " == ";
		if (op.equals(BinaryExpression.Op_NE))
			return " != ";
		if (op.equals(BinaryExpression.Op_LT))
			return " < ";
		if (op.equals(BinaryExpression.Op_GT))
			return " > ";
		if (op.equals(BinaryExpression.Op_LE))
			return " <= ";
		if (op.equals(BinaryExpression.Op_GE))
			return " >= ";
		if (op.equals(BinaryExpression.Op_AND))
			return " && ";
		if (op.equals(BinaryExpression.Op_OR))
			return " || ";
		if (op.equals(BinaryExpression.Op_XOR))
			return " ^ ";
		if (op.equals(BinaryExpression.Op_CONCAT))
			return " + ";
		if (op.equals(BinaryExpression.Op_BITAND))
			return " & ";
		if (op.equals(BinaryExpression.Op_BITOR))
			return " | ";
		if (op.equals(BinaryExpression.Op_LEFTSHIFT))
			return " << ";
		if (op.equals(BinaryExpression.Op_RIGHTSHIFTARITHMETIC))
			return " >> ";
		if (op.equals(BinaryExpression.Op_RIGHTSHIFTLOGICAL))
			return " >>> ";
		return "";
	}

}
