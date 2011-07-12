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
package org.eclipse.edt.gen.javascript.templates.eglx.http;

import java.util.Locale;

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.JavaScriptTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.InvocationExpression;
import org.eclipse.edt.mof.egl.Library;

public class HttpLibTemplate extends JavaScriptTemplate {
	// the library gets invoked here, with the invocation expression passed as the 1st argument in the args list. From here,
	// we use the lowercase function name as the lookup for the generation. This means that all system functions are
	// implemented by the lowercase method name. This technique allows a user to add/override system functions simply by
	// extending this class and adding/overriding the system function name as the method name, in lowercase.
	public void genInvocation(Library type, Context ctx, TabbedWriter out, InvocationExpression arg) {
		ctx.invoke(arg.getTarget().getName().toLowerCase(Locale.ENGLISH), type, ctx, out, arg);
	}


	public void convertfromurlencoded(Library type, Context ctx, TabbedWriter out, InvocationExpression arg) {
		ctx.invoke(genInvocation, arg, ctx, out);
	}

	public void converttourlencoded(Library type, Context ctx, TabbedWriter out, InvocationExpression arg) {
		ctx.invoke(genInvocation, arg, ctx, out);
	}

	public void sethttpbasicauthentication(Library type, Context ctx, TabbedWriter out, InvocationExpression arg) {
		ctx.invoke(genInvocation, arg, ctx, out);
	}

	public void setproxybasicauthentication(Library type, Context ctx, TabbedWriter out, InvocationExpression arg) {
		ctx.invoke(genInvocation, arg, ctx, out);
	}

}
