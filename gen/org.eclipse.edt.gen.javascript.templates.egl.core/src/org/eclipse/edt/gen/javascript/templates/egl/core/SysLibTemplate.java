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

public class SysLibTemplate extends JavascriptTemplate {

	// the library gets invoked here, with the invocation expression passed as the 1st argument in the args list. From here,
	// we use the lowercase function name as the lookup for the generation. This means that all system functions are
	// implemented by the lowercase method name. This technique allows a user to add/override system functions simply by
	// extending this class and adding/overriding the system function name as the method name, in lowercase.
	public void genInvocation(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(((InvocationExpression) args[0]).getTarget().getName().toLowerCase(Locale.ENGLISH), (Type) type, ctx, out, args);
	}

	// all system functions are defined below, with the method name as lowercase.
	public void audit(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void bytes(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void calculatechkdigitmod10(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void calculatechkdigitmod11(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void callcmd(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void conditionasint(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void convert(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void convertbidi(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void convertencodedtexttostring(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void convertnumbertounicodenum(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void convertnumbertounsignedunicodenum(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void convertstringtoencodedtext(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void convertunicodenumtonumber(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void convertunsignedunicodenumtonumber(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void commit(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void errorlog(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getcmdlineargcount(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getcmdlinearg(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getmessage(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getproperty(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void maximumsize(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void purge(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void rollback(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void seterror(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void seterrorforcomponentid(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void setlocale(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void setremoteuser(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void size(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void startcmd(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void startlog(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void verifychkdigitmod10(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void verifychkdigitmod11(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void wait(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void writestderr(Library type, Context ctx, TabbedWriter out, Object... args) {
		out.print("egl.println(");
		ctx.foreach(((InvocationExpression) args[0]).getArguments(), ',', genExpression, ctx, out, args);
		out.print(")");
	}

	public void writestdout(Library type, Context ctx, TabbedWriter out, Object... args) {
		out.print("egl.println(");
		ctx.foreach(((InvocationExpression) args[0]).getArguments(), ',', genExpression, ctx, out, args);
		out.print(")");
	}
}
