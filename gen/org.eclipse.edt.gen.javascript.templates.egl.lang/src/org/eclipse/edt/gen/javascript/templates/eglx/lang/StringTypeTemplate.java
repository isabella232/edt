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

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.javascript.CommonUtilities;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.*;

public class StringTypeTemplate extends JavaScriptTemplate {

	// this method gets invoked when there is a limited string needed
	public void genDefaultValue(SequenceType type, Context ctx, TabbedWriter out) {
		processDefaultValue(type, ctx, out);
	}

	// this method gets invoked when there is a string needed
	public void genDefaultValue(ParameterizableType type, Context ctx, TabbedWriter out) {
		processDefaultValue(type, ctx, out);
	}

	public void processDefaultValue(Type type, Context ctx, TabbedWriter out) {
		out.print(quoted(""));
	}

	// this method gets invoked when there is a limited string needed
	public void genSignature(SequenceType type, Context ctx, TabbedWriter out) {
		StringBuilder signature = new StringBuilder("s");
		if(type.getLength() != null && type.getLength() > 0){
			signature.append(type.getLength());
		}
		signature.append(';');
		out.print(signature.toString());
	}

	// this method gets invoked when there is a string needed
	public void genSignature(ParameterizableType type, Context ctx, TabbedWriter out) {
		String signature = "S;";
		out.print(signature);
	}

	public void genBinaryExpression(EGLClass type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// if either side of this expression is nullable, or if there is no direct java operation, we need to use the runtime
		if ((arg.getLHS().isNullable() || arg.getRHS().isNullable()) || getNativeStringOperation(arg).length() == 0) {
			out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
			out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
			out.print("(");
			ctx.invoke(genExpression, arg.getLHS(), ctx, out, arg.getOperation().getParameters().get(0));
			out.print(", ");
			ctx.invoke(genExpression, arg.getRHS(), ctx, out, arg.getOperation().getParameters().get(1));
			out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
		} else {
			out.print(getNativeStringPrefixOperation(arg));
			out.print("(");
			ctx.invoke(genExpression, arg.getLHS(), ctx, out);
			out.print(")");
			out.print(getNativeStringOperation(arg));
			ctx.invoke(genExpression, arg.getRHS(), ctx, out);
			out.print(getNativeStringComparisionOperation(arg));
		}
	}

	protected String getNativeStringPrefixOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(MultiOperandExpression.Op_NE))
			return "";
		return "";
	}

	protected String getNativeStringOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		// these are the defaults for what can be handled by the java string class
		if (op.equals(MultiOperandExpression.Op_PLUS))
			return " + ";
		if (op.equals(MultiOperandExpression.Op_EQ))
			return " == ";
		if (op.equals(MultiOperandExpression.Op_NE))
			return " != ";
		if (op.equals(MultiOperandExpression.Op_LT))
			return " < ";
		if (op.equals(MultiOperandExpression.Op_GT))
			return " > ";
		if (op.equals(MultiOperandExpression.Op_LE))
			return " <= ";
		if (op.equals(MultiOperandExpression.Op_GE))
			return " >= ";
		if (op.equals(MultiOperandExpression.Op_AND))
			return " && ";
		if (op.equals(MultiOperandExpression.Op_OR))
			return " || ";
		if (op.equals(MultiOperandExpression.Op_CONCAT))
			return " + ";
		return "";
	}

	protected String getNativeStringComparisionOperation(BinaryExpression expr) {
		String op = expr.getOperator();
		if (op.equals(MultiOperandExpression.Op_EQ))
			return "";
		if (op.equals(MultiOperandExpression.Op_NE))
			return "";
		if (op.equals(MultiOperandExpression.Op_LT))
			return "";
		if (op.equals(MultiOperandExpression.Op_GT))
			return "";
		if (op.equals(MultiOperandExpression.Op_LE))
			return "";
		if (op.equals(MultiOperandExpression.Op_GE))
			return "";
		return "";
	}
	
	public void genContainerBasedInvocation(EGLClass type, Context ctx, TabbedWriter out, InvocationExpression expr) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".");
		ctx.invoke(genName, expr.getTarget(), ctx, out);
		out.print("(");
		if (!(expr.getTarget() instanceof Member) || !((Member)expr.getTarget()).isStatic()) {
			ctx.invoke(genExpression, expr.getQualifier(), ctx, out);
			if (expr.getArguments() != null && expr.getArguments().size() > 0) {
				out.print(", ");
			}
		}
		ctx.invoke(genInvocationArguments, expr, ctx, out);
		out.print(")");
	}
}
