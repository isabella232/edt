/*******************************************************************************
 * Copyright © 2006, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.services.servlet.proxy;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.services.ServiceUtilities;
import org.eclipse.edt.javart.services.servlet.Servlet;
import org.eclipse.edt.javart.services.servlet.ServletUtilities;

import eglx.http.HttpUtilities;
import eglx.http.Request;
import eglx.http.Response;
import eglx.json.JsonUtilities;



/**
 * Servlet implementation class for Servlet: AjaxProxyServlet
 *
 */
 public class AjaxProxyServlet extends Servlet {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	private static final String PROXY_SERVLET = "EGL Rich UI Proxy";
	
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public AjaxProxyServlet() 
	{
		super();
	}   	 	
	
	@Override
	protected String servletName() {
		return PROXY_SERVLET;
	}

	@Override
	protected Response processRequest(String url, Request ruiRequest, HttpServletRequest httpServletReq) throws Exception {
		ProxyEventHandler proxy = new ProxyEventHandler();
		return proxy.runProxy(url, ruiRequest, ServletUtilities.createHttpRequest(ruiRequest.body));
	}
	protected void sendResponse(HttpServletResponse httpServletRes, int status, String content) throws IOException {
		if( status != HttpUtilities.HTTP_STATUS_OK ){
			try
			{
				httpServletRes.setHeader( JsonUtilities.JSON_RPC_ERROR_NAME_VALUE, URLEncoder.encode( content, ServiceUtilities.UTF8 ) );
			}
			catch( Exception e ){}
		}
		PrintWriter pw = httpServletRes.getWriter();
		pw.write( content );
		pw.flush();
		httpServletRes.setStatus( status );
		httpServletRes.flushBuffer();
	}
}
