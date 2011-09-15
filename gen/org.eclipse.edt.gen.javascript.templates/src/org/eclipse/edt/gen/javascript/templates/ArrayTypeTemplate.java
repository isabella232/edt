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

import java.util.List;

import org.eclipse.edt.gen.javascript.Constants;
import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.ArrayType;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.IntegerLiteral;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.Type;

public class ArrayTypeTemplate extends JavaScriptTemplate {

	public void genDefaultValue(ArrayType generic, Context ctx, TabbedWriter out, Field arg) {
		processDefaultValue(generic, ctx, out);
	}

	public void genDefaultValue(ArrayType generic, Context ctx, TabbedWriter out) {
		processDefaultValue(generic, ctx, out);
	}

	public void processDefaultValue(ArrayType generic, Context ctx, TabbedWriter out) {
		out.print(" []"); // TODO sbg Nullable support
	}

	public void genSignature(ArrayType generic, Context ctx, TabbedWriter out) {
		if (!generic.getTypeArguments().isEmpty()) {
			for (int i = 0; i < generic.getTypeArguments().size(); i++)
				out.print("[");
		}
		if (generic.elementsNullable())
			out.print("?");
		ctx.invoke(genSignature, generic.getElementType(), ctx, out);
	}

	public void genTypeBasedAssignment(Type type, Context ctx, TabbedWriter out, Assignment arg) {
		String operator = "=";
		if (arg.getOperator() != null && arg.getOperator().length() > 0)
			operator = arg.getOperator();
		if ("::=".equals(operator)) {
			ctx.putAttribute(arg.getLHS(), Constants.EXPR_LHS, Boolean.FALSE); // Ensure we don't use a getter for the
																				// accessor
			if (arg.getLHS() instanceof MemberAccess) {
				/* Ensure we don't use a getter for the accessor */
				ctx.putAttribute(((MemberAccess) arg.getLHS()).getNamedElement(), Constants.EXPR_LHS, Boolean.FALSE); 
			}
			ctx.invoke(genExpression, arg.getLHS(), ctx, out);
			
			Expression rhs = arg.getRHS();
			
			// Normally, the RHS will be encased in an AsExpression and we'll want to unwrap it....
			if (rhs instanceof AsExpression)
				rhs = ((AsExpression)rhs).getObjectExpr();
			
			if (rhs.getType() instanceof ArrayType)
				out.print(".appendAll(");
			else
				out.print(".appendElement(");
			if (rhs instanceof BoxingExpression)
				ctx.invoke(genExpression, ((BoxingExpression)rhs).getExpr(), ctx, out);
			else
				ctx.invoke(genExpression, rhs, ctx, out);
			out.print(")");
		} 
		else if ("=".equals(operator) && arg.getRHS() instanceof NewExpression){
			int arraySize = 0;
			List<Expression> arguments = ((NewExpression)arg.getRHS()).getArguments();
			if ((arguments != null) && (arguments.size() == 1)) {
				try {
					arraySize = Integer.valueOf(((IntegerLiteral)arguments.get(0)).getValue()); 
				}
				catch (Exception e) {
					arraySize = 0;
				}
			}
			ArrayType generic = (ArrayType)type;
			String temporary = ctx.nextTempName();
			ctx.invoke(genExpression, arg.getLHS(), ctx, out);
			out.print(" "+operator+" ");
			out.print("(function() { var ");
			out.print(temporary);
			out.print(" = []; ");
			out.print(temporary);
			out.print(".setType(");
			out.print("\"");
			genSignature(generic, ctx, out);
			out.print("\"");
			out.println(");");
			out.println("for (var i = 0; i < " + arraySize + "; i++) {");
			out.print(temporary);
			out.print("[i] = ");
			ctx.invoke(genDefaultValue, generic.getElementType(), ctx, out);
			out.println(";}");
			out.print("return ");
			out.print(temporary);
			out.print(";})()");
		}
		else
			ctx.invokeSuper(this, genTypeBasedAssignment, type, ctx, out, arg);
	}

	public void genRuntimeTypeName(ArrayType generic, Context ctx, TabbedWriter out, TypeNameKind arg) {
		out.print("Array");
	}

	public void genFieldInfoTypeName(ArrayType generic, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genFieldInfoTypeName, generic.getElementType(), ctx, out, TypeNameKind.JavascriptImplementation);
	}

	public void genConversionOperation(ArrayType type, Context ctx, TabbedWriter out, AsExpression arg) {
		ctx.invoke(genExpression, arg.getObjectExpr(), ctx, out);
	}
}
