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
package org.eclipse.edt.javart.services.servlet.rest.rpc;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.resources.Trace;
import org.eclipse.edt.javart.services.servlet.Servlet;
import org.eclipse.edt.javart.services.servlet.proxy.RuiBrowserHttpRequest;

import eglx.http.HttpRequest;
import eglx.http.HttpResponse;



/**
 * Servlet implementation class for Servlet: AjaxProxyServlet
 *
 */
 public class ServiceServlet extends Servlet {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	static final String SERVICE_SERVLET = "EGL REST Service servlet";
	private static final String URIXML_ID = "urixml";
	private String urixml;

	private RestServiceProjectInfo restServiceProjectInfo;
	
	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
		urixml = config.getInitParameter(URIXML_ID);
		restServiceProjectInfo = RestRpcUtilities.getRestServiceInfo( urixml );
		traceInfos();
	}
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public ServiceServlet() 
	{
		super();
	}   	 	
	
	@Override
	protected String programName() {
		return SERVICE_SERVLET;
	}

	@Override
	protected HttpResponse processRequest(String url, RuiBrowserHttpRequest xmlRequest, HttpRequest createHttpRequest) throws Exception {
		return null;
	}
	private void traceInfos()
	{
		if( tracer().traceIsOn( Trace.GENERAL_TRACE ) )
		{
			StringBuilder buf = new StringBuilder();
			String servlet = getContext();
			buf.append( "EGL REST service servlet " + servlet + " starting" + '\n' );
			buf.append( restServiceProjectInfo.toString() );
			trace( buf.toString() );
		}
	}
	private String getContext()
	{
		return urixml.substring( 0, urixml.indexOf( ".xml" ) );
	}
}
