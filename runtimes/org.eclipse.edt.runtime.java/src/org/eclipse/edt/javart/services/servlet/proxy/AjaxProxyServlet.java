/*******************************************************************************
 * Copyright © 2006, 2011 IBM Corporation and others.
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

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.services.servlet.Servlet;

import eglx.http.HttpRequest;
import eglx.http.HttpResponse;



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
	protected String programName() {
		return PROXY_SERVLET;
	}

	@Override
	protected HttpResponse processRequest(String url, RuiBrowserHttpRequest xmlRequest, HttpRequest createHttpRequest) throws Exception {
		ProxyEventHandler proxy = new ProxyEventHandler(program());
		return proxy.runProxy(url, xmlRequest, createHttpRequest);
	}
}
