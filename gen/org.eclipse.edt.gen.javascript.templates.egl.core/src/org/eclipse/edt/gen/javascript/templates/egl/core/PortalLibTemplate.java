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

import org.eclipse.edt.gen.javascript.Context;
import org.eclipse.edt.gen.javascript.templates.NativeTypeTemplate;
import org.eclipse.edt.mof.codegen.api.TabbedWriter;
import org.eclipse.edt.mof.egl.EGLClass;
import org.eclipse.edt.mof.egl.InvocationExpression;

public class PortalLibTemplate extends NativeTypeTemplate {
	private static final String getPortletSessionAttr = "getPortletSessionAttr";
	private static final String setPortletSessionAttr = "setPortletSessionAttr";
	private static final String clearPortletSessionAttr = "clearPortletSessionAttr";
	private static final String setPortletMode = "setPortletMode";
	private static final String getPortletMode = "getPortletMode";
	private static final String setWindowState = "setWindowState";
	private static final String getWindowState = "getWindowState";
	private static final String isPreferenceReadOnly = "isPreferenceReadOnly";
	private static final String getPreferenceValue = "getPreferenceValue";
	private static final String getPreferenceValues = "getPreferenceValues";
	private static final String resetPreference = "resetPreference";
	private static final String setPreferenceValue = "setPreferenceValue";
	private static final String setPreferenceValues = "setPreferenceValues";
	private static final String savePreferences = "savePreferences";
	private static final String createVaultSlot = "createVaultSlot";
	private static final String deleteVaultSlot = "deleteVaultSlot";
	private static final String setCredential = "setCredential";
	private static final String getCredential = "getCredential";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(getPortletSessionAttr))
			genGetPortletSessionAttr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setPortletSessionAttr))
			genSetPortletSessionAttr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(clearPortletSessionAttr))
			genClearPortletSessionAttr(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setPortletMode))
			genSetPortletMode(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getPortletMode))
			genGetPortletMode(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setWindowState))
			genSetWindowState(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getWindowState))
			genGetWindowState(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(isPreferenceReadOnly))
			genIsPreferenceReadOnly(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getPreferenceValue))
			genGetPreferenceValue(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getPreferenceValues))
			genGetPreferenceValues(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(resetPreference))
			genResetPreference(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setPreferenceValue))
			genSetPreferenceValue(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setPreferenceValues))
			genSetPreferenceValues(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(savePreferences))
			genSavePreferences(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(createVaultSlot))
			genCreateVaultSlot(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(deleteVaultSlot))
			genDeleteVaultSlot(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setCredential))
			genSetCredential(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getCredential))
			genGetCredential(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genGetPortletSessionAttr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetPortletSessionAttr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genClearPortletSessionAttr(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetPortletMode(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetPortletMode(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetWindowState(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetWindowState(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genIsPreferenceReadOnly(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetPreferenceValue(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetPreferenceValues(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genResetPreference(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetPreferenceValue(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetPreferenceValues(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSavePreferences(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genCreateVaultSlot(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genDeleteVaultSlot(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetCredential(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetCredential(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
