/*******************************************************************************
 * Copyright © 2012, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package eglx.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ServletContext {
	
	private HttpServletRequest httpServletReq;
	private HttpServletResponse httpServletRes;
	public ServletContext(HttpServletRequest httpServletReq,
			HttpServletResponse httpServletRes) {
		super();
		this.httpServletReq = httpServletReq;
		this.httpServletRes = httpServletRes;
	}
	/**
	 * The per-thread RunUnits, used in application servers.
	 */
	private static ThreadLocal<ServletContext> threadContexts;
	
	public static void setThreadRunUnit( ServletContext context )
	{
		
		if ( threadContexts == null )
		{
			threadContexts = new ThreadLocal<ServletContext>();
		}
		
		threadContexts.set( context );
	}
	public HttpServletRequest getHttpServletReq() {
		return httpServletReq;
	}
	public HttpServletResponse getHttpServletRes() {
		return httpServletRes;
	}
	
	public static HttpServletRequest getHttpServletRequest()
	{
		if ( threadContexts != null && threadContexts.get() != null)
		{
			return threadContexts.get().getHttpServletReq();
		}
		else
		{
			return null;
		}
	}
	public static HttpServletResponse getHttpServletResponse()
	{
		if ( threadContexts != null && threadContexts.get() != null)
		{
			return threadContexts.get().getHttpServletRes();
		}
		else
		{
			return null;
		}
	}
}
