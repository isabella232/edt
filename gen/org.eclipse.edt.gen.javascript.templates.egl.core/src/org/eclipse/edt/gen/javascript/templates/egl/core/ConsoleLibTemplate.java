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

public class ConsoleLibTemplate extends JavascriptTemplate {
	// the library gets invoked here, with the invocation expression passed as the 1st argument in the args list. From here,
	// we use the lowercase function name as the lookup for the generation. This means that all system functions are
	// implemented by the lowercase method name. This technique allows a user to add/override system functions simply by
	// extending this class and adding/overriding the system function name as the method name, in lowercase.
	public void genInvocation(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(((InvocationExpression) args[0]).getTarget().getName().toLowerCase(Locale.ENGLISH), (Type) type, ctx, out, args);
	}

	// all system functions are defined below, with the method name as lowercase.
	public void displaylinemode(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void promptlinemode(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void activatewindow(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void activatewindowbyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void clearactivewindow(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void clearwindow(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void clearwindowbyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void closeactivewindow(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void closewindow(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void closewindowbyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void drawbox(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void drawboxwithcolor(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void displayatline(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void displayatposition(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void displaymessage(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void displayerror(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getfieldbuf(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void hideerrorwindow(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void openwindow(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void openwindowbyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void openwindowwithform(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void openwindowwithformbyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void clearactiveform(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void clearform(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void clearfields(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void clearfieldsbyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void displayform(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void displayformbyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void displayfields(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void displayfieldsbyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void gotofield(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void gotofieldbyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void nextfield(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void previousfield(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void iscurrentfield(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void iscurrentfieldbyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void isfieldmodified(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void isfieldmodifiedbyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getkey(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getkeycode(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void getkeyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void lastkeytyped(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void cancelarraydelete(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void cancelarrayinsert(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void currentarrayscreenline(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void currentarraydataline(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void scrolldownpage(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void scrolldownlines(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void scrolluppage(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void scrolluplines(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void setarrayline(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void setcurrentarraycount(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void showhelp(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void hidemenuitem(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void hidemenuitembyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void gotomenuitem(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void gotomenuitembyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void showmenuitem(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void showmenuitembyname(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void currentarraycount(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void showallmenuitems(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void hideallmenuitems(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void updatewindowattributes(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void registerconsoleform(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void openfiledialog(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}

	public void opendirectorydialog(Library type, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, (InvocationExpression) args[0], ctx, out, args);
	}
}
