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

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.codegen.api.TemplateException;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.IRUtils;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class AssignmentTemplate extends ExpressionTemplate {

	public void genExpression(Assignment expr, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
		// first, make this expression compatible
		IRUtils.makeCompatible(expr);
		ctx.gen(genLHSExpression, expr.getLHS(), ctx, out);
		// analyze the expression by first breaking it down by the lhs
		Type lhsType = expr.getLHS().getType();
		if (TypeUtils.isReferenceType(lhsType) 
				|| ctx.mapsToPrimitiveType(lhsType)) {
			ctx.gen(genRHSAssignment, expr.getLHS(), ctx, out, expr.getRHS());
		}
		else {
			out.print('.');
			out.print(eze$$copy);
			out.print('(');
			ctx.gen(genRHSExpression, expr.getRHS(), ctx, out, expr.getLHS());
			out.print(')');
		}
	}

//	public void genArrayAssignment(Assignment expr, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
//		// are we dealing with a nullable array
//		if (expr.getLHS().isNullable()) {
//			// if this is a well-behaved assignment, we can avoid the temporary
//			if (IRUtils.hasSideEffects(expr.getLHS())) {
//				out.print("var ");
//				String temporary = ctx.nextTempName();
//				out.print(" " + temporary + " = ");
//				genExpression(expr.getLHS(), ctx, out, args);
//				out.println(";");
//				out.print(temporary + "[");
//				genExpression(((ArrayAccess) expr.getLHS()).getIndex(), ctx, out, args);
//				out.print("-1] = ");
//				out.print("egl.ezeCopyTo(");
//				genExpression(expr.getRHS(), ctx, out, args);
//				out.print(", ");
//				out.print(temporary + "[");
//				genExpression(((ArrayAccess) expr.getLHS()).getIndex(), ctx, out, args);
//				out.print("-1]");
//				out.print(")");
//			} else if (TypeUtils.isReferenceType(expr.getLHS().getType()) || ctx.mapsToJavaType(expr.getLHS().getType())) {
//				genExpression(((ArrayAccess) expr.getLHS()).getArray(), ctx, out, args);
//				out.print("[");
//				genExpression(((ArrayAccess) expr.getLHS()).getIndex(), ctx, out, args);
//				out.print("-1] = ");
//				genExpression(expr.getRHS(), ctx, out, args);
//			} else {
//				genExpression(((ArrayAccess) expr.getLHS()).getArray(), ctx, out, args);
//				out.print("[");
//				genExpression(((ArrayAccess) expr.getLHS()).getIndex(), ctx, out, args);
//				out.print("-1] = ");
//				out.print("egl.ezeCopyTo(");
//				genExpression(expr.getRHS(), ctx, out, args);
//				out.print(", ");
//				genExpression(expr.getLHS(), ctx, out, args);
//				out.print(")");
//			}
//		} else if (TypeUtils.isReferenceType(expr.getLHS().getType()) || ctx.mapsToJavaType(expr.getLHS().getType())) {
//			genExpression(((ArrayAccess) expr.getLHS()).getArray(), ctx, out, args);
//			out.print("[");
//			genExpression(((ArrayAccess) expr.getLHS()).getIndex(), ctx, out, args);
//			out.print("-1] = ");
//			genExpression(expr.getRHS(), ctx, out, args);
//		} else {
//			// non-nullable array
//			out.print("egl.ezeCopyTo(");
//			genExpression(expr.getRHS(), ctx, out, args);
//			out.print(", ");
//			genExpression(expr.getLHS(), ctx, out, args);
//		}
//	}
//
//	public void genDynamicAssignment(Assignment expr, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
//		genExpression(((DynamicAccess) expr.getLHS()).getExpression(), ctx, out, args);
//		out.print("[");
//		genExpression(((DynamicAccess) expr.getLHS()).getAccess(), ctx, out, args);
//		out.print("] = ");
//		genExpression(expr.getRHS(), ctx, out, args);
//	}
//
//	public void genPrimitiveAssignment(Assignment expr, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
//		// if the lhs is non-nullable but the rhs is nullable, we have a special case
//		if (!expr.getLHS().isNullable() && expr.getRHS().isNullable()) {
//			// if this is a well-behaved assignment, we can avoid the temporary
//			if (IRUtils.hasSideEffects(expr.getRHS())) {
//				String temporary = ctx.nextTempName();
//				genRuntimeTypeName(expr.getRHS().getType(), ctx, out, RuntimeTypeNameKind.JavascriptObject);
//				out.print(" " + temporary + " = ");
//				genExpression(expr.getRHS(), ctx, out, args);
//				out.println(";");
//				genExpression(expr.getLHS(), ctx, out, args);
//				out.print(" = ");
//				out.print("function(x){return x == null ? ");
//				ctx.gen(genDefaultValue, expr.getLHS().getType(), ctx, out, expr.getLHS());
//				out.print(" : x");
//				out.print("}(" + temporary + ")");
//			} else if (TypeUtils.isReferenceType(expr.getRHS().getType()))
//				generateStraightAssignment(expr, ctx, out, args);
//			else {
//				genExpression(expr.getLHS(), ctx, out, args);
//				out.print(" = ");
//				genExpression(expr.getRHS(), ctx, out, args);
//				out.print(" == null ? ");
//				ctx.gen(genDefaultValue, expr.getLHS().getType(), ctx, out, expr.getLHS());
//				out.print(" : ");
//				genExpression(expr.getRHS(), ctx, out, args);
//			}
//		} else
//			generateStraightAssignment(expr, ctx, out, args);
//	}
//
//	public void genValueAssignment(Assignment expr, Context ctx, TabbedWriter out, Object... args) throws TemplateException {
//		// check to see if we are copying LHS boxed temporary variables (inout and out types only)
//		if (expr.getLHS() instanceof MemberName
//			&& ctx.getAttribute(((MemberName) expr.getLHS()).getMember(), Constants.functionArgumentTemporaryVariable) != null
//			&& ((Integer) ctx.getAttribute(((MemberName) expr.getLHS()).getMember(), Constants.functionArgumentTemporaryVariable)).intValue() != 0) {
//			genExpression(expr.getLHS(), ctx, out, args);
//			out.print(" = ");
//			out.print("egl.ezeWrap(");
//			genExpression(expr.getRHS(), ctx, out, args);
//			out.print(")");
//			// check to see if we are unboxing RHS temporary variables (inout and out types only)
//		} else if (expr.getRHS() instanceof MemberName
//			&& ctx.getAttribute(((MemberName) expr.getRHS()).getMember(), Constants.functionArgumentTemporaryVariable) != null
//			&& ((Integer) ctx.getAttribute(((MemberName) expr.getRHS()).getMember(), Constants.functionArgumentTemporaryVariable)).intValue() != 0) {
//			genExpression(expr.getLHS(), ctx, out, args);
//			out.print(" = ");
//			genExpression(expr.getRHS(), ctx, out, args);
//			out.print(".ezeUnbox()");
//		} else {
//			if (expr.getLHS().isNullable()) {
//				genExpression(expr.getLHS(), ctx, out, args);
//				out.print(" = ");
//			}
//			out.print("egl.ezeCopyTo(");
//			genExpression(expr.getRHS(), ctx, out, args);
//			out.print(", ");
//			genExpression(expr.getLHS(), ctx, out, args);
//			out.print(")");
//		}
//	}
//
//	public void generateStraightAssignment(Assignment expr, Context ctx, TabbedWriter out, Object... args) {
//		// check to see if we are copying boxed function parameters
//		if (expr.getLHS() instanceof MemberName && ((MemberName) expr.getLHS()).getMember() instanceof FunctionParameter
//			&& isBoxedParameterType(ctx, (FunctionParameter) ((MemberName) expr.getLHS()).getMember())) {
//			ctx.gen(genAccessor, (EObject) ((MemberName) expr.getLHS()).getMember(), ctx, out, args);
//			out.print(".ezeCopy(");
//			genExpression(expr.getRHS(), ctx, out, args);
//			out.print(")");
//			// check to see if we are copying LHS boxed temporary variables (inout and out types only)
//		} else if (expr.getLHS() instanceof MemberName
//			&& ctx.getAttribute(((MemberName) expr.getLHS()).getMember(), Constants.functionArgumentTemporaryVariable) != null
//			&& ((Integer) ctx.getAttribute(((MemberName) expr.getLHS()).getMember(), Constants.functionArgumentTemporaryVariable)).intValue() != 0) {
//			genExpression(expr.getLHS(), ctx, out, args);
//			out.print(" = ");
//			out.print("AnyObject.ezeWrap(");
//			genExpression(expr.getRHS(), ctx, out, args);
//			out.print(")");
//			// check to see if we are unboxing RHS temporary variables (inout and out types only)
//		} else if (expr.getRHS() instanceof MemberName
//			&& ctx.getAttribute(((MemberName) expr.getRHS()).getMember(), Constants.functionArgumentTemporaryVariable) != null
//			&& ((Integer) ctx.getAttribute(((MemberName) expr.getRHS()).getMember(), Constants.functionArgumentTemporaryVariable)).intValue() != 0) {
//			genExpression(expr.getLHS(), ctx, out, args);
//			out.print(" = ");
//			genExpression(expr.getRHS(), ctx, out, args);
//			out.print(".ezeUnbox()");
//		} else {
//			genExpression(expr.getLHS(), ctx, out, args);
//			out.print(" = ");
//			genExpression(expr.getRHS(), ctx, out, args);
//		}
//	}
}
