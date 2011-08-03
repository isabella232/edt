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
package org.eclipse.edt.javart.services.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.edt.javart.Constants;
import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.json.ParseException;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.resources.RunUnitBase;
import org.eclipse.edt.javart.resources.StartupInfo;
import org.eclipse.edt.javart.resources.Trace;
import org.eclipse.edt.javart.services.servlet.proxy.RuiBrowserHttpRequest;

import eglx.http.HttpRequest;
import eglx.http.HttpResponse;
import eglx.http.HttpUtilities;
import eglx.json.JsonUtilities;
import eglx.services.ServiceKind;
import eglx.services.ServiceUtilities;



/**
 * Servlet implementation class for Servlet: AjaxProxyServlet
 *
 */
 public abstract class Servlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	private static final String EGL_HTTP_SESSION_ID_KEY = "egl.gateway.session.id";
	private ExecutableBase program;
	private Trace tracer;
	
	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config);
	}
	
	protected Trace tracer()
	{
		if( tracer == null )
		{
			tracer = program()._runUnit().getTrace();
		}
		return tracer;
	}
	
	protected abstract String programName();
	protected ExecutableBase program()
	{
		if( program == null )
		{
			try
			{
				program = new ExecutableBase(new RunUnitBase(new StartupInfo( programName(), "", true ))){private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;};
			}
			catch(JavartException e){}
		}
		return program;
	}
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public Servlet() 
	{
		super();
	}   	 	
	
	protected void trace( String msg )
	{
		if ( tracer().traceIsOn( Trace.GENERAL_TRACE ) ) 
		{
			tracer().put( msg );
		}
	}
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest httpServletReq, HttpServletResponse httpServletRes) throws ServletException, IOException 
	{
		doHttp(httpServletReq, httpServletRes);
	}

//	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException 
	{
		doHttp(req, resp);
	}
	
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException 
	{
		doHttp(req, resp);
	}

	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException 
	{
		doHttp(req, resp);
	}

	private void doHttp(HttpServletRequest httpServletReq, HttpServletResponse httpServletRes)
	{
		HttpSession session = httpServletReq.getSession();
		trace("HttpServletRequest.getContextPath():" + httpServletReq.getContextPath());
		trace("HttpServletRequest.getPathInfo():" + httpServletReq.getPathInfo());
		String url = null;
		ServiceKind serviceKind = null;
		RuiBrowserHttpRequest xmlRequest = null;
		HttpResponse outerResponse = null;
		try
		{
			//TODO first check for a dedicated service because it is executed here
			//TODO we are going to forward the request
			//TODO then set the url
			//TODO timeout???, authentication????, session????, token?????
			trace( programName() + " sessionId:" + (session == null ? "null" : session.getId()) );
			xmlRequest = RuiBrowserHttpRequest.createNewRequest( httpServletReq );
			if(xmlRequest != null)
			{
				url = xmlRequest.getURL(); 
				//debug("server@"+portNumber+": url="+url);
				if ( tracer().traceIsOn( Trace.GENERAL_TRACE ) ) 
				{
					tracer().put( "REQUEST:" );
					tracer().put( "    URL:" + url != null ? url : "null");
					tracer().put( "    content:" + xmlRequest.getContent() != null ? xmlRequest.getContent() : "null");
					tracer().put( "    httpMethod:" + xmlRequest.getMethod() != null ? xmlRequest.getMethod() : "null" );
				}
				outerResponse = processRequest( url, xmlRequest, ServletUtilities.createHttpRequest(program(), xmlRequest.getContent()) );
			}
		}
		catch(Throwable t)
		{
			outerResponse = buildResponse( program(), httpServletReq.getRequestURL().toString(), t, 
								xmlRequest != null && xmlRequest.getContent() != null ? xmlRequest.getContent() : "", 
								serviceKind);
		}
		String content = outerResponse.getBody();
		log(content, outerResponse);
		String sessionId = (String)outerResponse.getHeaders().get(EGL_HTTP_SESSION_ID_KEY);
		if( sessionId != null )
		{
			httpServletRes.setHeader(EGL_HTTP_SESSION_ID_KEY, sessionId);
		}
		write( httpServletRes, content, outerResponse.getStatus() );
	}   	
	
	protected abstract HttpResponse processRequest(String url, RuiBrowserHttpRequest xmlRequest, HttpRequest createHttpRequest) throws Exception;

	private HttpResponse buildResponse( ExecutableBase program, String url,  Throwable t, String requestContent, ServiceKind serviceKind )
	{
		HttpResponse outerResponse = new HttpResponse();
		//handle as inner exception
		JavartException jrte;

		if( t instanceof ParseException )
		{
			jrte = ServiceUtilities.buildServiceInvocationException( program, Message.SOA_E_WS_REST_BAD_CONTENT, new String[]{requestContent}, t, serviceKind );
		}
		else if( t instanceof IOException )
		{
			//handle as outer exception
			jrte = ServiceUtilities.buildServiceInvocationException( program, Message.SOA_E_WS_PROXY_COMMUNICATION, new String[]{url}, t, serviceKind );
		}
		else
		{
			jrte = ServiceUtilities.buildServiceInvocationException( program, Message.SOA_E_WS_PROXY_UNIDENTIFIED, new Object[0], t, serviceKind );
		}
		HttpResponse innerResponse = new HttpResponse();
		ServletUtilities.setBody(innerResponse, jrte);
		innerResponse.setStatus(HttpUtilities.HTTP_STATUS_FAILED);
		innerResponse.setStatusMessage(HttpUtilities.HTTP_STATUS_MSG_FAILED);
		ServletUtilities.setBody(outerResponse, innerResponse);
		outerResponse.setStatus(HttpUtilities.HTTP_STATUS_OK);
		outerResponse.setStatusMessage(HttpUtilities.HTTP_STATUS_MSG_OK);
		return outerResponse;
	}
	private void log( String content, HttpResponse response )
	{
/*		String str;
		if( response.getBody() instanceof HttpResponse )
		{
			str = "Multi-Level";
		}
		else
		{
			str = "Single-Level";
		}
		System.out.println( str + "\n      " + content );
		*/
	}
	private void write( HttpServletResponse httpServletRes, String content, int status )
	{
		try
		{
			httpServletRes.setContentType( HttpUtilities.getContentType(HttpUtilities.CONTENT_TEXT_KEY) );
			if( status == HttpUtilities.HTTP_STATUS_OK )
			{
				PrintWriter pw = httpServletRes.getWriter();
				pw.write( content );
				pw.flush();
				httpServletRes.setStatus( status );
				httpServletRes.flushBuffer();
			}
			else
			{
				try
				{
					httpServletRes.setHeader( JsonUtilities.JSON_RPC_ERROR_NAME_VALUE, URLEncoder.encode( content, ServiceUtilities.UTF8 ) );
				}
				catch( Exception e ){}
				httpServletRes.sendError( status );
			}
		}
		catch( Throwable t2 ){t2.printStackTrace();}

	}

}
