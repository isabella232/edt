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
package org.eclipse.edt.gen.java.templates;

import org.eclipse.edt.gen.java.CommonUtilities;
import org.eclipse.edt.gen.java.Constants;
import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Expression;
import org.eclipse.edt.mof.egl.FunctionParameter;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberName;
import org.eclipse.edt.mof.egl.Type;

public class MemberNameTemplate extends NameTemplate {

	public void genAssignment(MemberName expr, Context ctx, TabbedWriter out, Object... args) {
		// check to see if we are copying boxed function parameters
		if (expr.getMember() instanceof FunctionParameter && CommonUtilities.isBoxedParameterType((FunctionParameter) expr.getMember(), ctx)) {
			ctx.gen(genAccessor, expr.getMember(), ctx, out, args);
			out.print(".ezeCopy(");
			genExpression((Expression) args[0], ctx, out, args);
			out.print(")");
			// check to see if we are copying LHS boxed temporary variables (inout and out types only)
		} else if (ctx.getAttribute(expr.getMember(), Constants.Annotation_functionArgumentTemporaryVariable) != null
			&& ((Integer) ctx.getAttribute(expr.getMember(), Constants.Annotation_functionArgumentTemporaryVariable)).intValue() != 0) {
			genExpression((Expression) expr, ctx, out, args);
			out.print(" = ");
			out.print("AnyObject.ezeWrap(");
			genExpression((Expression) args[0], ctx, out, args);
			out.print(")");
			// check to see if we are unboxing RHS temporary variables (inout and out types only)
		} else if ((Expression) args[0] instanceof MemberName
			&& ctx.getAttribute(((MemberName) (Expression) args[0]).getMember(), Constants.Annotation_functionArgumentTemporaryVariable) != null
			&& ((Integer) ctx.getAttribute(((MemberName) (Expression) args[0]).getMember(), Constants.Annotation_functionArgumentTemporaryVariable)).intValue() != 0) {
			genExpression((Expression) expr, ctx, out, args);
			out.print(" = ");
			genExpression((Expression) args[0], ctx, out, args);
			out.print(".ezeUnbox()");
		} else
			super.genAssignment(expr, ctx, out, args);
	}

	public void genExpression(MemberName expr, Context ctx, TabbedWriter out, Object... args) {
		Member member = expr.getMember();
		if (member != null && member.getContainer() != null && member.getContainer() instanceof Type)
			ctx.gen(genContainerBasedMemberName, (Type) member.getContainer(), ctx, out, expr, member);
		else
			genMemberName(expr, ctx, out, args);
	}

	public void genMemberName(MemberName expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genAccessor, expr.getMember(), ctx, out, args);
		if (expr.getMember() instanceof FunctionParameter && CommonUtilities.isBoxedParameterType((FunctionParameter) expr.getMember(), ctx))
			out.print(".ezeUnbox()");
	}
}
