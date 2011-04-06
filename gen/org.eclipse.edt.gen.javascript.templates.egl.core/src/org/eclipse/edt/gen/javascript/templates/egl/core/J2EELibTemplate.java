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
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.Library;
import org.eclipse.edt.mof.egl.Type;

public class J2EELibTemplate extends JavaScriptTemplate {
	// the library gets invoked here, with the invocation expression passed as the 1st argument in the args list. From here,
	// we use the lowercase function name as the lookup for the generation. This means that all system functions are
	// implemented by the lowercase method name. This technique allows a user to add/override system functions simply by
	// extending this class and adding/overriding the system function name as the method name, in lowercase.
	public void genInvocation(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(((InvocationExpression) args[0]).getTarget().getName().toLowerCase(Locale.ENGLISH), (Type) type, ctx, out, args);
	}

	// all system functions are defined below, with the method name as lowercase.
	public void cleareglsessionattrs(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void clearapplicationattr(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void clearrequestattr(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void clearsessionattr(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getauthenticationtype(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getqueryparameter(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getremoteuser(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getrequestattr(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getsessionattr(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getapplicationattr(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void isuserinrole(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void setapplicationattr(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void setrequestattr(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void setsessionattr(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getcontext(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}
}
