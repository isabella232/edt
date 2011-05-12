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
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.Type;
import org.eclipse.edt.mof.egl.utils.TypeUtils;

public class MemberAccessTemplate extends JavaScriptTemplate {

	public void genExpression(MemberAccess expr, Context ctx, TabbedWriter out, Object... args) {
		Member member = expr.getMember();
		if (member != null && member.getContainer() != null && member.getContainer() instanceof Type)
			ctx.gen(genContainerBasedMemberAccess, (Type) member.getContainer(), ctx, out, expr, member);
		else
			genMemberAccess(expr, ctx, out, args);
	}

	public void genMemberAccess(MemberAccess expr, Context ctx, TabbedWriter out, Object... args) {
		if (TypeUtils.isReferenceType(expr.getQualifier().getType()) || expr.getQualifier().isNullable()) {
			// TODO sbg doesn't seem to be quite the right place out.print("egl.checkNull(");
			ctx.gen(genExpression, expr.getQualifier(), ctx, out, args);
			// TODO sbg doesn't seem to be quite the right place out.print(")");
		} else
			ctx.gen(genExpression, expr.getQualifier(), ctx, out, args);
		out.print(".");
		ctx.gen(genAccessor, expr.getMember(), ctx, out, args);
	}
}
