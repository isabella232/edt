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
package org.eclipse.edt.gen.java.templates.eglx.lang;

import org.eclipse.edt.gen.java.Context;
import org.eclipse.edt.gen.java.templates.JavaTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.Assignment;
import org.eclipse.edt.mof.egl.Field;
import org.eclipse.edt.mof.egl.Member;
import org.eclipse.edt.mof.egl.MemberAccess;
import org.eclipse.edt.mof.egl.Type;

public class AnyExceptionTypeTemplate extends JavaTemplate {

	public void genRuntimeTypeName(Type type, Context ctx, TabbedWriter out, TypeNameKind arg) {
		ctx.invoke(genPartName, type, ctx, out);
	}

	public void genSuperClass(Type type, Context ctx, TabbedWriter out) {
		out.print("eglx.lang.AnyException");
	}

	public void genContainerBasedAssignment(Type type, Context ctx, TabbedWriter out, Assignment arg1, Field arg2) {
		if (arg2.getName().equalsIgnoreCase("message") || arg2.getName().equalsIgnoreCase("messageid")) {
			ctx.invoke(genExpression, arg1.getLHS().getQualifier(), ctx, out, arg1.getLHS().getQualifier());
			out.print(".set");
			out.print(arg2.getName().substring(0, 1).toUpperCase());
			if (arg2.getName().length() > 1)
				out.print(arg2.getName().substring(1));
			out.print("(");
			ctx.invoke(genExpression, arg1.getRHS(), ctx, out);
			out.print(")");
		} else
			ctx.invoke(genAssignment, arg1, ctx, out);
	}

	public void genContainerBasedMemberAccess(Type type, Context ctx, TabbedWriter out, MemberAccess arg1, Member arg2) {
		if (arg2.getName().equalsIgnoreCase("message") || arg2.getName().equalsIgnoreCase("messageid")) {
			ctx.invoke(genExpression, arg1.getQualifier(), ctx, out, arg1.getQualifier());
			out.print(".get");
			out.print(arg2.getName().substring(0, 1).toUpperCase());
			if (arg2.getName().length() > 1)
				out.print(arg2.getName().substring(1));
			out.print("()");
		} else
			ctx.invoke(genMemberAccess, arg1, ctx, out);
	}
}
