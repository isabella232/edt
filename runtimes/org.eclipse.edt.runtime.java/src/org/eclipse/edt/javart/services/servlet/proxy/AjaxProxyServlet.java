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

import javax.servlet.http.HttpServletRequest;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.services.servlet.Servlet;
import org.eclipse.edt.javart.services.servlet.ServletUtilities;

import eglx.http.Request;
import eglx.http.Response;



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
}
