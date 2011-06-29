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
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.ParameterizableType;
import org.eclipse.edt.mof.egl.SequenceType;
import org.eclipse.edt.mof.egl.SubstringAccess;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.TypedElement;

public class AnyStringTypeTemplate extends JavaScriptTemplate {

	// this method gets invoked when there is a limited string needed
	public void genDefaultValue(SequenceType type, Context ctx, TabbedWriter out, Object... args) {
		processDefaultValue(type, ctx, out, args);
	}

	// this method gets invoked when there is a string needed
	public void genDefaultValue(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		processDefaultValue(type, ctx, out, args);
	}

	public void processDefaultValue(Type type, Context ctx, TabbedWriter out, Object... args) {
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			out.print("null");
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			out.print("null");
		else
			out.print(quoted(""));
	}

	// this method gets invoked when there is a limited string needed
	public void genSignature(SequenceType type, Context ctx, TabbedWriter out, Object... args) {
		String signature = "";
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			signature += "?";
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			signature += "?";
		signature += "S;";
		out.print(signature);
	}

	// this method gets invoked when there is a string needed
	public void genSignature(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		String signature = "";
		if (args.length > 0 && args[0] instanceof TypedElement && ((TypedElement) args[0]).isNullable())
			signature += "?";
		else if (args.length > 0 && args[0] instanceof Expression && ((Expression) args[0]).isNullable())
			signature += "?";
		signature += "S;";
		out.print(signature);
	}

	// this method gets invoked when there is a limited string needed
	public void genSubstringAssignment(SequenceType type, Context ctx, TabbedWriter out, Object... args) {
		processSubstringAssignment(type, ctx, out, args);
	}

	// this method gets invoked when there is a string needed
	public void genSubstringAssignment(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		processSubstringAssignment(type, ctx, out, args);
	}

	public void processSubstringAssignment(Type type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genExpression, ((SubstringAccess) args[0]).getStringExpression(), ctx, out, args);
		out.print(" = ");
		out.print(ctx.getNativeImplementationMapping(((SubstringAccess) args[0]).getType()) + ".substringAssign(");
		ctx.gen(genExpression, ((SubstringAccess) args[0]).getStringExpression(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, (Expression) args[1], ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, ((SubstringAccess) args[0]).getStart(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, ((SubstringAccess) args[0]).getEnd(), ctx, out, args);
		out.print(")");
	}

	// this method gets invoked when there is a limited string needed
	public void genSubstringAccess(SequenceType type, Context ctx, TabbedWriter out, Object... args) {
		processSubstringAccess(type, ctx, out, args);
	}

	// this method gets invoked when there is a string needed
	public void genSubstringAccess(ParameterizableType type, Context ctx, TabbedWriter out, Object... args) {
		processSubstringAccess(type, ctx, out, args);
	}

	public void processSubstringAccess(Type type, Context ctx, TabbedWriter out, Object... args) {
		out.print(ctx.getNativeImplementationMapping(((SubstringAccess) args[0]).getType()) + ".substring(");
		ctx.gen(genExpression, ((SubstringAccess) args[0]).getStringExpression(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, ((SubstringAccess) args[0]).getStart(), ctx, out, args);
		out.print(", ");
		ctx.gen(genExpression, ((SubstringAccess) args[0]).getEnd(), ctx, out, args);
		out.print(")");
	}

	public void genBinaryExpression(EGLClass type, Context ctx, TabbedWriter out, Object... args) throws GenerationException {
		// if either side of this expression is nullable, or if there is no direct java operation, we need to use the runtime
		if ((((BinaryExpression) args[0]).getLHS().isNullable() || ((BinaryExpression) args[0]).getRHS().isNullable())
			|| getNativeStringOperation((BinaryExpression) args[0]).length() == 0) {
			out.print(ctx.getNativeImplementationMapping((Type) ((BinaryExpression) args[0]).getOperation().getContainer()) + '.');
			out.print(CommonUtilities.getNativeRuntimeOperationName((BinaryExpression) args[0]));
			out.print("(ezeProgram, ");
			ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, args);
			out.print(", ");
			ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, args);
			out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation((BinaryExpression) args[0]));
		} else {
			out.print(getNativeStringPrefixOperation((BinaryExpression) args[0]));
			out.print("(");
			ctx.gen(genExpression, ((BinaryExpression) args[0]).getLHS(), ctx, out, args);
			out.print(")");
			out.print(getNativeStringOperation((BinaryExpression) args[0]));
			ctx.gen(genExpression, ((BinaryExpression) args[0]).getRHS(), ctx, out, args);
			out.print(getNativeStringComparisionOperation((BinaryExpression) args[0]));
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
		return "";
	}
}
