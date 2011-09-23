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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Future;

import org.eclipse.edt.javart.AnyBoxedObject;
import org.eclipse.edt.javart.Runtime;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.Trace;
import org.eclipse.edt.javart.util.JavartUtil;

import eglx.http.Request;
import eglx.http.Response;
import eglx.http.HttpUtilities;
import eglx.json.JsonUtilities;
import eglx.lang.EDictionary;
import eglx.services.ServiceKind;
import eglx.services.ServiceUtilities;


public class HttpServiceHandler
{
	HttpServiceHandler()
	{
	}
	private static final String SESSION_ID = "JSESSIONID";
	private static final String EGL_SESSION_ID = "egl_statefulsessionid";
	private static final String RESPONSE_CHARSET = "edt.service.response.charset";
	private static final String COOKIE_ID = "SET-COOKIE";
	private class HttpStreamReader implements Runnable
	{
		private URLConnection connection;
		private boolean doneReading;
		private String value;
		private Throwable t;
		private String responseCharset;


		private HttpStreamReader( URLConnection connection, String responseCharset )
		{
			this.connection = connection;
			doneReading = false;
			this.responseCharset = responseCharset;
		}

		public void run()
		{
			t = null;
			InputStream is = null;
			try
			{
				is = connection.getInputStream();
				value = readStream(is);
			}
			catch( Throwable t )
			{
				if( is != null )
				{
					try
					{
						is.close();
					}
					catch( IOException ioe ){}
					is = null;
				}
				this.t = t;
				if(connection instanceof HttpURLConnection){
					try{
						is = ((HttpURLConnection)connection).getErrorStream();
						value = readStream(is);
					}
					catch( Throwable t1 ){}
				}
			}
			finally{
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
			doneReading = true;
		}

		private String readStream(InputStream is) throws IOException, UnsupportedEncodingException {
			StringBuilder body = new StringBuilder();
			BufferedReader reader = null;
			try{
				try{
					if(responseCharset == null){
						responseCharset = getCharSet(connection.getContentType());
					}
					reader = new BufferedReader(new InputStreamReader(is, responseCharset));
				}
				catch(Throwable t){}
				if(reader == null){
					reader = new BufferedReader(new InputStreamReader(is, ServiceUtilities.UTF8));
				}
				int charCnt = 0;
				for (int nRead = reader.read(); charCnt < RuiBrowserHttpRequest.MAX_NUMBER_CHARS && nRead != -1; nRead = reader.read(), charCnt++ )
				{
					body.append((char)nRead);
				}
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
			return body.toString();
		}

		public boolean exception()
		{
			return t != null;
		}
		public boolean isDoneReading()
		{
			return doneReading;
		}

		public String getReadValue()
		{
			return value;
		}

		public Throwable getException()
		{
			return t;
		}

	}
	Response invokeRestService( Request request, HttpURLConnection connection ) throws Throwable
	{
		EDictionary headers = request.headers;
		Response response;
		try
		{
			response = new Response();
			HttpStreamReader httpsr = null;
			try
			{
				connection.setRequestMethod( HttpUtilities.httpMethodToString(request.method) );
				Trace tracer = Runtime.getRunUnit().getTrace();
				if ( tracer.traceIsOn( Trace.GENERAL_TRACE ) ) 
				{
					tracer.put("REST request URL:" + request.uri);
				}

				String contentType = null;
				String responseCharset = null;
				for ( Iterator<Map.Entry<String, Object>> iter = headers.entrySet().iterator(); iter.hasNext(); )
				{
					Map.Entry<String, Object> entry = iter.next();
					Object entryValue = entry.getValue();
					if(entryValue instanceof AnyBoxedObject<?>){
						entryValue = ((AnyBoxedObject<?>)entryValue).ezeUnbox();
					}
					if(entry.getKey().equalsIgnoreCase(HttpUtilities.CONTENT_TYPE_KEY)){
						contentType = entryValue.toString();
					}
					else if(entry.getKey().equalsIgnoreCase(RESPONSE_CHARSET)){
						responseCharset = entryValue.toString();
						entryValue = null;//don't pass this to the remote service
					}
					if(entryValue != null){
						connection.setRequestProperty( entry.getKey(), entryValue.toString() );
					}
				}

				String charset = getCharSet(contentType);
				byte[] resource = request.body == null ? new byte[0] : request.body.getBytes( charset == null ? ServiceUtilities.UTF8 : charset );
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

				httpsr = new HttpStreamReader( connection, responseCharset == null ? ServiceUtilities.UTF8 : responseCharset );
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
						connection.disconnect();
						connection = null;
						String message = JavartUtil.errorMessage(
								Message.SOA_E_WS_PROXY_SERVICE_TIMEOUT,
								request.uri );
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
				response.body = httpsr.getReadValue();
				response.status = connection.getResponseCode();
				response.statusMessage = connection.getResponseMessage();
				Map<String,List<String>> header = connection.getHeaderFields();
				if ( header != null )
				{
					if(response.headers == null){
						response.headers = new org.eclipse.edt.runtime.java.eglx.lang.EDictionary();
					}
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
				if ( useConnection )
				{
					try
					{
						response.status = connection.getResponseCode();
						response.statusMessage = connection.getResponseMessage();
						String eglException;
						if ( (eglException = connection.getHeaderField( JsonUtilities.JSON_RPC_ERROR_NAME_VALUE )) != null )
						{
							try
							{
								eglException = URLDecoder.decode( eglException, ServiceUtilities.UTF8 );
								response.body = eglException;
							}
							catch ( Exception e )
							{
							}
						}
						else if (httpsr != null && httpsr.exception() && 
								httpsr.getReadValue() != null && !httpsr.getReadValue().isEmpty()){
					    	response.body = eglx.json.JsonUtilities.createJsonAnyException(ServiceUtilities.buildInvocationException(
									Message.SOA_E_WS_PROXY_COMMUNICATION,
									new String[] { request.uri }, 
									response.status.toString(), 
									response.statusMessage,
									ServiceUtilities.getMessage(ioe) + ":\n" + httpsr.getReadValue(),
									null,
									ServiceKind.REST ));
						}
					}
					catch ( IOException ie )
					{
						response.status = HttpUtilities.HTTP_STATUS_FAILED;
						response.statusMessage = HttpUtilities.HTTP_STATUS_MSG_FAILED;
					}

				}
				else
				{
					response.status = HttpUtilities.HTTP_STATUS_FAILED;
					response.statusMessage = HttpUtilities.HTTP_STATUS_MSG_FAILED;
				}
				if((response.body == null || response.body.isEmpty()))
				{
			    	response.body = eglx.json.JsonUtilities.createJsonAnyException(ServiceUtilities.buildServiceInvocationException(
							Message.SOA_E_WS_PROXY_COMMUNICATION,
							new String[] { request.uri }, ioe,
							ServiceKind.REST ));
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
	
	private void populate( Response response, Map<String,List<String>> header )
	{
		Map.Entry<String,List<String>> entry;
		for( Iterator<Map.Entry<String,List<String>>> itr = header.entrySet().iterator(); itr.hasNext(); )
		{
			entry = itr.next();
			if(entry.getKey() != null){
				response.headers.put(entry.getKey(), entry.getValue() == null ? null :  entry.getValue().toString() );
			}
		}
	}

	private void setServiceSessionId( Response response, Map<String,List<String>> header, Object sessionKey )
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
					response.headers.put( EGL_SESSION_ID, sessionId );
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
	private String getCharSet(String contentType) {
		int idx;
		if(contentType != null && 
				(idx = contentType.toLowerCase().indexOf("charset=")) != -1){
			return contentType.substring(idx + 8);
		}
		return null;
	}

	
}
