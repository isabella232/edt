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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Future;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.resources.Trace;
import org.eclipse.edt.javart.services.servlet.ServletUtilities;
import org.eclipse.edt.javart.util.JavartUtil;

import egl.lang.EDictionary;
import eglx._service.ServiceKind;
import eglx._service.ServiceUtilities;
import eglx.http.HttpRequest;
import eglx.http.HttpResponse;
import eglx.http.HttpUtilities;
import eglx.json.JsonUtilities;


public class HttpServiceHandler
{
	private ExecutableBase program;
	private static final String USE_CONTENT_TYPE_CHARSET = "useContentTypeCharset";
	private static final String RESPONSE_CHARSET_PROPERTY_KEY = "com.ibm.egl.service.rest.invocation.response.charset";
	HttpServiceHandler( ExecutableBase program )
	{
		this.program = program;
	}
	private static final String SESSION_ID = "JSESSIONID";
	private static final String EGL_SESSION_ID = "egl_statefulsessionid";
	private static final String COOKIE_ID = "SET-COOKIE";
	private class HttpStreamReader implements Runnable
	{
		private URLConnection connection;
		private InputStream is;
		private boolean doneReading;
		private String value;
		private Exception e;
		BufferedReader reader;


		public HttpStreamReader( URLConnection connection )
		{
			this.connection = connection;
			doneReading = false;
		}

		public void run()
		{
			e = null;
			StringBuilder body = new StringBuilder();
			reader = null;
			try
			{
				is = connection.getInputStream();
				String responseCharset = getResponseCharset(program);
				if(responseCharset != null && responseCharset.length() > 0){
					try{
						if(USE_CONTENT_TYPE_CHARSET.equalsIgnoreCase(responseCharset)){
							String contentType = connection.getContentType();
							int idx;
							if(contentType != null && 
									(idx = contentType.toLowerCase().indexOf("charset=")) != -1){
								responseCharset = contentType.substring(idx + 8);
							}
						}
						reader = new BufferedReader(new InputStreamReader(is, responseCharset));
					}
					catch(Throwable t){}
				}
				if(reader == null){
					reader = new BufferedReader(new InputStreamReader(is, ServiceUtilities.UTF8));
				}
				int charCnt = 0;
				for (int nRead = reader.read(); charCnt < RuiBrowserHttpRequest.MAX_NUMBER_CHARS && nRead != -1; nRead = reader.read(), charCnt++ )
				{
					body.append((char)nRead);
				}
				value = body.toString();
			}
			catch( Exception e )
			{
				this.e = e;
			}
			finally
			{
				if( reader != null )
				{
					try
					{
						reader.close();
					}
					catch( IOException ioe ){}
					reader = null;
				}
			}
			doneReading = true;
		}

		public void close()
		{
			if( reader != null )
			{
				try
				{
					reader.close();
				}
				catch( IOException ioe ){}
				reader = null;
			}
			if( is != null )
			{
				try
				{
					is.close();
				}
				catch( IOException ioe ){}
				is = null;
			}
		}
		public boolean exception()
		{
			return e != null;
		}
		public boolean isDoneReading()
		{
			return doneReading;
		}

		public String getReadValue()
		{
			return value;
		}

		public Exception getException()
		{
			return e;
		}

	}
	HttpResponse invokeRestService( HttpRequest restRequest, HttpURLConnection connection ) throws InterruptedException, Exception
	{
		EDictionary headers = restRequest.getHeaders();
		HttpResponse response;
		try
		{
			response = new HttpResponse();
			try
			{
				connection.setRequestMethod( HttpUtilities.httpMethodToString(restRequest.getMethod()) );
				Trace tracer = program._runUnit().getTrace();
				if ( tracer.traceIsOn( Trace.GENERAL_TRACE ) ) 
				{
					tracer.put("REST request URL:" + restRequest.getUri());
				}

				for ( Iterator<Map.Entry<String, Object>> iter = headers.entrySet().iterator(); iter.hasNext(); )
				{
					Map.Entry<String, Object> entry = iter.next();
					connection.setRequestProperty( entry.getKey(), entry.getValue().toString() );
				}

				// writes the request out in utf-8
				byte[] resource = restRequest.getBody().getBytes( ServiceUtilities.UTF8 );
				connection.setDoInput( true );
				connection.setUseCaches( false );

				long startTime = System.currentTimeMillis();
				if ( resource != null && resource.length > 0 )
				{
					connection.setDoOutput( true );
					DataOutputStream os = new DataOutputStream( connection.getOutputStream() );
					os.write( resource, 0, resource.length );
					os.flush();
					os.close();
				}

				HttpStreamReader httpsr = new HttpStreamReader( connection );
				Future<?> threadResult = JavartUtil.getThreadPool().submit( httpsr );
				Thread.yield();
				if ( !httpsr.isDoneReading() )
				{
					long expireTime = -1;
					boolean infinite = true;
					try
					{
						expireTime = Integer.parseInt( (String)headers.get( HttpUtilities.HTTP_RESPONSE_TIMEOUT ) );
						expireTime += System.currentTimeMillis();
						infinite = false;
					}
					catch ( Exception e )
					{
					}
					while ( !httpsr.isDoneReading()
							&& (infinite || expireTime > System.currentTimeMillis()) )
					{
						Thread.sleep( 50 );
					}
					if ( !httpsr.isDoneReading() )
					{
						threadResult.cancel( true );
						httpsr.close();
						connection.disconnect();
						connection = null;
						String message = JavartUtil.errorMessage( program,
								Message.SOA_E_WS_PROXY_SERVICE_TIMEOUT,
								new String[] { restRequest.getUri() } );
						throw new IOException( message );
					}
					else if ( httpsr.exception() )
					{
						throw httpsr.getException();
					}
				}
				if ( tracer.traceIsOn( Trace.GENERAL_TRACE ) ) 
				{
					tracer.put( "Service response time:" + String.valueOf( System.currentTimeMillis() - startTime ) );
				}
				response.setBody( httpsr.getReadValue() );
				response.setStatus( connection.getResponseCode() );
				response.setStatusMessage( connection.getResponseMessage() );
				Map<String,List<String>> header = connection.getHeaderFields();
				if ( header != null )
				{
					populate( response, header );
					setServiceSessionId( response, header, headers.get( EGL_SESSION_ID ) );
				}

			}
			catch ( IOException ioe )
			{
				boolean useConnection = false;
				try
				{
					useConnection = connection != null
							&& connection.getResponseCode() != -1;
				}
				catch ( Throwable t )
				{
				}
				boolean bodyIsSet = false;
				if ( useConnection )
				{
					try
					{
						response.setStatus( connection.getResponseCode() );
						response.setStatusMessage( connection.getResponseMessage() );
						String eglException;
						if ( (eglException = connection.getHeaderField( JsonUtilities.JSON_RPC_ERROR_NAME_VALUE )) != null )
						{
							try
							{
								eglException = URLDecoder.decode( eglException,
										ServiceUtilities.UTF8 );
								response.setBody( eglException );
								bodyIsSet = true;
							}
							catch ( Exception e )
							{
							}
						}
					}
					catch ( IOException ie )
					{
						response.setStatus( HttpUtilities.HTTP_STATUS_FAILED );
						response.setStatusMessage( HttpUtilities.HTTP_STATUS_MSG_FAILED );
					}

				}
				else
				{
					response.setStatus( HttpUtilities.HTTP_STATUS_FAILED );
					response.setStatusMessage( HttpUtilities.HTTP_STATUS_MSG_FAILED );
				}
				if ( !bodyIsSet )
				{
					ServletUtilities.setBody(response, ServiceUtilities.buildServiceInvocationException( program,
									Message.SOA_E_WS_PROXY_COMMUNICATION,
									new String[] { restRequest.getUri() }, ioe,
									ServiceKind.REST ) );
				}
			}
		}
		finally
		{
			if ( connection != null )
			{
				connection.disconnect();
			}
		}
		return response;
	}
	private void populate( HttpResponse response, Map<String,List<String>> header )
	{
		Map.Entry<String,List<String>> entry;
		for( Iterator<Map.Entry<String,List<String>>> itr = header.entrySet().iterator(); itr.hasNext(); )
		{
			entry = itr.next();
			response.getHeaders().put(entry.getKey() == null ? null : entry.getKey(), entry.getValue() == null ? null :  entry.getValue().toString() );
		}
	}

	private void setServiceSessionId( HttpResponse response, Map<String,List<String>> header, Object sessionKey )
	{
		//first establish the sessionKey
		//then look through the header for a SET-COOKIE
		//then see if the value contains the sessionKey
		//if it does put it in the header for the browser
		if( sessionKey == null || (sessionKey instanceof String && ((String)sessionKey).length() == 0) )
		{
			sessionKey = SESSION_ID;
		}
		Map.Entry<String,List<String>> entry;
		for( Iterator<Map.Entry<String,List<String>>> itr = header.entrySet().iterator(); itr.hasNext(); )
		{
			entry = itr.next();
			if( entry.getKey() instanceof String &&
					((String)entry.getKey()).equalsIgnoreCase( COOKIE_ID ) )
			{
				String sessionId = getSessionId( entry.getValue(), sessionKey.toString() );
				if( sessionId != null )
				{
					response.getHeaders().put( EGL_SESSION_ID, sessionId );
					break;
				}
			}
		}
	}
	private String getSessionId( Object value, String sessionKey )
	{
		String str = null;
		if( value instanceof Collection<?> )
		{
			for( Iterator<?> itr = ((Collection<?>)value).iterator(); itr.hasNext(); )
			{
				str = getSessionId( itr.next(), sessionKey );
				if( str != null )
				{
					break;
				}
			}
		}
		else if( value instanceof String &&
				((String)value).indexOf( sessionKey ) > -1 )
		{
			StringTokenizer subStrings = new StringTokenizer( (String)value, ";" );
			String id;
			while( subStrings.hasMoreElements() )
			{
				id = subStrings.nextToken();
				if( id.indexOf( sessionKey ) > -1 )
				{
					str =  (String)value;
					break;
				}
			}
		}
		return str;
	}
	
	private String getResponseCharset(ExecutableBase program)
	{
		return program._runUnit().getProperties().get((RESPONSE_CHARSET_PROPERTY_KEY) );
	}
	

}
