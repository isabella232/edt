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

public class ServiceLibTemplate extends NativeTypeTemplate {
	private static final String setWebServiceLocation = "setWebServiceLocation";
	private static final String getWebServiceLocation = "getWebServiceLocation";
	private static final String setTCPIPLocation = "setTCPIPLocation";
	private static final String getTCPIPLocation = "getTCPIPLocation";
	private static final String bindService = "bindService";
	private static final String convertFromJSON = "convertFromJSON";
	private static final String convertToJSON = "convertToJSON";
	private static final String convertFromURLEncoded = "convertFromURLEncoded";
	private static final String convertToURLEncoded = "convertToURLEncoded";
	private static final String setRestRequestHeaders = "setRestRequestHeaders";
	private static final String getRestRequestHeaders = "getRestRequestHeaders";
	private static final String setSOAPRequestHeaders = "setSOAPRequestHeaders";
	private static final String getSOAPRequestHeaders = "getSOAPRequestHeaders";
	private static final String setHTTPBasicAuthentication = "setHTTPBasicAuthentication";
	private static final String setProxyBasicAuthentication = "setProxyBasicAuthentication";
	private static final String setRestServiceLocation = "setRestServiceLocation";
	private static final String getRestServiceLocation = "getRestServiceLocation";
	private static final String getCurrentCallbackResponse = "getCurrentCallbackResponse";
	private static final String getOriginalRequest = "getOriginalRequest";
	private static final String endStatefulServiceSession = "endStatefulServiceSession";
	private static final String setCCSID = "setCCSID";

	public void genInvocation(EGLClass type, Context ctx, TabbedWriter out, Object... args) {
		InvocationExpression expr = (InvocationExpression) args[0];
		if (expr.getTarget().getName().equalsIgnoreCase(setWebServiceLocation))
			genSetWebServiceLocation(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getWebServiceLocation))
			genGetWebServiceLocation(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setTCPIPLocation))
			genSetTCPIPLocation(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getTCPIPLocation))
			genGetTCPIPLocation(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(bindService))
			genBindService(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(convertFromJSON))
			genConvertFromJSON(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(convertToJSON))
			genConvertToJSON(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(convertFromURLEncoded))
			genConvertFromURLEncoded(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(convertToURLEncoded))
			genConvertToURLEncoded(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setRestRequestHeaders))
			genSetRestRequestHeaders(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getRestRequestHeaders))
			genGetRestRequestHeaders(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setSOAPRequestHeaders))
			genSetSOAPRequestHeaders(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getSOAPRequestHeaders))
			genGetSOAPRequestHeaders(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setHTTPBasicAuthentication))
			genSetHTTPBasicAuthentication(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setProxyBasicAuthentication))
			genSetProxyBasicAuthentication(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setRestServiceLocation))
			genSetRestServiceLocation(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getRestServiceLocation))
			genGetRestServiceLocation(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getCurrentCallbackResponse))
			genGetCurrentCallbackResponse(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(getOriginalRequest))
			genGetOriginalRequest(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(endStatefulServiceSession))
			genEndStatefulServiceSession(expr, ctx, out, args);
		else if (expr.getTarget().getName().equalsIgnoreCase(setCCSID))
			genSetCCSID(expr, ctx, out, args);
		else
			genNoImplementation(expr, ctx, out, args);
	}

	public void genSetWebServiceLocation(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetWebServiceLocation(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetTCPIPLocation(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetTCPIPLocation(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genBindService(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConvertFromJSON(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConvertToJSON(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConvertFromURLEncoded(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genConvertToURLEncoded(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetRestRequestHeaders(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetRestRequestHeaders(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetSOAPRequestHeaders(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetSOAPRequestHeaders(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetHTTPBasicAuthentication(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetProxyBasicAuthentication(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetRestServiceLocation(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetRestServiceLocation(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetCurrentCallbackResponse(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genGetOriginalRequest(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genEndStatefulServiceSession(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}

	public void genSetCCSID(InvocationExpression expr, Context ctx, TabbedWriter out, Object... args) {
		ctx.gen(genInvocation, expr, ctx, out, args);
	}
}
