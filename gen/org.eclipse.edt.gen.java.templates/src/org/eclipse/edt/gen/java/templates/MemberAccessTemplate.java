/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Function;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.Type;

public class MemberAccessTemplate extends JavaTemplate {

	public void genExpression(MemberAccess expr, Context ctx, TabbedWriter out) {
		Member member = expr.getMember();
		if (member != null && member.getContainer() != null && member.getContainer() instanceof Type)
			ctx.invoke(genContainerBasedMemberAccess, (Type) member.getContainer(), ctx, out, expr, member);
		else
			genMemberAccess(expr, ctx, out);
	}

	public void genMemberAccess(MemberAccess expr, Context ctx, TabbedWriter out) {
		// if this is a delegate, then the qualifier is one of the delegate's genAccessor arguments
		if (expr.getMember() instanceof Function)
			ctx.invoke(genAccessor, expr.getMember(), ctx, out, expr);
		else {
			ctx.invoke(genExpression, expr.getQualifier(), ctx, out, expr.getQualifier());
			out.print(".");
			ctx.invoke(genAccessor, expr.getMember(), ctx, out);
		}
	}
}
