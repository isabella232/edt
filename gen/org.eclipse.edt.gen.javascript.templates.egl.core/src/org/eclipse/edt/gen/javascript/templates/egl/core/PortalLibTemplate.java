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
package org.eclipse.edt.gen.javascript.templates.egl.core;

import java.util.Locale;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavascriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Type;

public class PortalLibTemplate extends JavascriptTemplate {
	// the library gets invoked here, with the invocation expression passed as the 1st argument in the args list. From here,
	// we use the lowercase function name as the lookup for the generation. This means that all system functions are
	// implemented by the lowercase method name. This technique allows a user to add/override system functions simply by
	// extending this class and adding/overriding the system function name as the method name, in lowercase.
	public void genInvocation(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(((InvocationExpression) args[0]).getTarget().getName().toLowerCase(Locale.ENGLISH), (Type) type, ctx, out, args);
	}

	// all system functions are defined below, with the method name as lowercase.
	public void getportletsessionattr(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void setportletsessionattr(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void clearportletsessionattr(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void setportletmode(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getportletmode(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void setwindowstate(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getwindowstate(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void ispreferencereadonly(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getpreferencevalue(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getpreferencevalues(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void resetpreference(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void setpreferencevalue(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void setpreferencevalues(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void savepreferences(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void createvaultslot(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void deletevaultslot(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void setcredential(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getcredential(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}
}
