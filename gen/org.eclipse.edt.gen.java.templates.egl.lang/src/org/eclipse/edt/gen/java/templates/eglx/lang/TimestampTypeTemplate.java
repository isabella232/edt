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
package org.eclipse.edt.gen.java.templates.eglx.lang;

import org.eclipse.edt.gen.GenerationException;
import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.AsExpression;
import org.eclipse.edt.mof.egl.BinaryExpression;
import org.eclipse.edt.mof.egl.BoxingExpression;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.IsAExpression;
import org.eclipse.edt.mof.egl.NewExpression;
import org.eclipse.edt.mof.egl.QualifiedFunctionInvocation;
import org.eclipse.edt.mof.egl.TimestampType;
import org.eclipse.edt.mof.egl.Type;

public class TimestampTypeTemplate extends JavaTemplate {

	public void genDefaultValue(Type type, Context ctx, TabbedWriter out) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".defaultValue(");
		ctx.invoke(genConstructorOptions, type, ctx, out);
		out.print(")");
	}

	public void genContainerBasedNewExpression(Type type, Context ctx, TabbedWriter out, NewExpression arg) throws GenerationException {
		ctx.invoke(genRuntimeTypeName, arg.getType(), ctx, out, TypeNameKind.EGLImplementation);
		out.print(".defaultValue(");
		if (arg.getArguments() != null && arg.getArguments().size() > 0) {
			for (Expression argument : arg.getArguments()) {
				ctx.invoke(genExpression, argument, ctx, out);
			}
		} else
			ctx.invoke(genConstructorOptions, arg.getType(), ctx, out);
		out.print(")");
	}

	public void genBinaryExpression(Type type, Context ctx, TabbedWriter out, BinaryExpression arg) throws GenerationException {
		// for timestamp type, always use the runtime
		out.print(ctx.getNativeImplementationMapping((Type) arg.getOperation().getContainer()) + '.');
		out.print(CommonUtilities.getNativeRuntimeOperationName(arg));
		out.print("(");
		ctx.invoke(genExpression, arg.getLHS(), ctx, out);
		out.print(", ");
		ctx.invoke(genExpression, arg.getRHS(), ctx, out);
		out.print(")" + CommonUtilities.getNativeRuntimeComparisionOperation(arg));
	}

	public void genContainerBasedInvocation(Type type, Context ctx, TabbedWriter out, InvocationExpression expr) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".");
		ctx.invoke(genName, expr.getTarget(), ctx, out);
		out.print("(");
		// do a callout to allow certain source types to decide to create a boxing expression
		ctx.invoke(genContainerBasedInvocationBoxing, type, ctx, out, expr);
		// then process the expression
		ctx.invoke(genExpression, expr.getQualifier(), ctx, out);
		if (expr.getArguments() != null && expr.getArguments().size() > 0)
			out.print(", ");
		ctx.foreach(expr.getArguments(), ',', genExpression, ctx, out);
		out.print(")");
	}

	public void genAsExpressionBoxing(Type type, Context ctx, TabbedWriter out, AsExpression arg) {
		if (!(arg.getObjectExpr() instanceof BoxingExpression)) {
			BoxingExpression box = factory.createBoxingExpression();
			box.setExpr(arg.getObjectExpr());
			arg.setObjectExpr(box);
		}
	}

	public void genIsaExpressionBoxing(Type type, Context ctx, TabbedWriter out, IsAExpression arg) {
		if (!(arg.getObjectExpr() instanceof BoxingExpression)) {
			BoxingExpression box = factory.createBoxingExpression();
			box.setExpr(arg.getObjectExpr());
			arg.setObjectExpr(box);
		}
	}

	public void genContainerBasedInvocationBoxing(Type type, Context ctx, TabbedWriter out, QualifiedFunctionInvocation arg) {
		if (!(arg.getQualifier() instanceof BoxingExpression)) {
			BoxingExpression box = factory.createBoxingExpression();
			box.setExpr(arg.getQualifier());
			arg.setQualifier(box);
		}
		if (arg.getArguments() != null && arg.getArguments().size() > 0) {
			// check each of the arguments and box if necessary
			for (int i = 0; i < arg.getArguments().size(); i++) {
				// do a callout to allow certain source types to decide to create a boxing expression
				ctx.invoke(genInvocationArgumentBoxing, arg.getArguments().get(i).getType(), ctx, out, arg, new Integer(i));
			}
		}
	}

	public void genInvocationArgumentBoxing(Type type, Context ctx, TabbedWriter out, QualifiedFunctionInvocation arg1, Integer arg2) {
		if (!(arg1.getArguments().get(arg2) instanceof BoxingExpression)) {
			BoxingExpression box = factory.createBoxingExpression();
			box.setExpr(arg1.getArguments().get(arg2));
			arg1.getArguments().set(arg2, box);
		}
	}

	public void genConstructorOptions(TimestampType type, Context ctx, TabbedWriter out) {
		generateOptions(type, ctx, out);
	}

	public void genTypeDependentOptions(TimestampType type, Context ctx, TabbedWriter out) {
		out.print(", ");
		generateOptions(type, ctx, out);
	}

	public void genJsonTypeDependentOptions(TimestampType type, Context ctx, TabbedWriter out) {
		String pattern = "yyyyMMddhhmmss";
		if (type.getPattern() != null && !type.getPattern().equalsIgnoreCase("null"))
			pattern = type.getPattern();
		out.print("\"");
		out.print(getStartPattern(pattern));
		out.print("\", \"");
		out.print(getEndPattern(pattern));
		out.print("\"");
	}

	public void generateOptions(TimestampType type, Context ctx, TabbedWriter out) {
		String pattern = "yyyyMMddhhmmss";
		if (type.getPattern() != null && !type.getPattern().equalsIgnoreCase("null"))
			pattern = type.getPattern();
		generateOptions(type, ctx, out, pattern);
	}

	public void generateOptions(Type type, Context ctx, TabbedWriter out, String pattern) {
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".");
		out.print(getStartPattern(pattern));
		out.print(", ");
		ctx.invoke(genRuntimeTypeName, type, ctx, out, TypeNameKind.EGLImplementation);
		out.print(".");
		out.print(getEndPattern(pattern));
	}

	public static String getStartPattern(String pattern) {
		if (pattern.startsWith("yyyy"))
			return "YEAR_CODE";
		else if (pattern.startsWith("MM"))
			return "MONTH_CODE";
		else if (pattern.startsWith("dd"))
			return "DAY_CODE";
		else if (pattern.startsWith("HH"))
			return "HOUR_CODE";
		else if (pattern.startsWith("mm"))
			return "MINUTE_CODE";
		else if (pattern.startsWith("ss"))
			return "SECOND_CODE";
		else if (pattern.startsWith("f"))
			return "FRACTION1_CODE";
		return "";
	}

	public static String getEndPattern(String pattern) {
		if (pattern.endsWith("yyyy"))
			return "YEAR_CODE";
		else if (pattern.endsWith("MM"))
			return "MONTH_CODE";
		else if (pattern.endsWith("dd"))
			return "DAY_CODE";
		else if (pattern.endsWith("HH"))
			return "HOUR_CODE";
		else if (pattern.endsWith("mm"))
			return "MINUTE_CODE";
		else if (pattern.endsWith("ss"))
			return "SECOND_CODE";
		else if (pattern.endsWith("ffffff"))
			return "FRACTION6_CODE";
		else if (pattern.endsWith("fffff"))
			return "FRACTION5_CODE";
		else if (pattern.endsWith("ffff"))
			return "FRACTION4_CODE";
		else if (pattern.endsWith("fff"))
			return "FRACTION3_CODE";
		else if (pattern.endsWith("ff"))
			return "FRACTION2_CODE";
		else if (pattern.endsWith("f"))
			return "FRACTION1_CODE";
		return "";
	}
}
