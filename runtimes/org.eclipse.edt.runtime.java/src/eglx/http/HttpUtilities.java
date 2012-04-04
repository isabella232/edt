/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.services.ServiceUtilities;
import org.eclipse.edt.javart.services.servlet.ServiceInvoker;

import eglx.lang.AnyException;
import eglx.lang.EDictionary;
import eglx.lang.InvalidArgumentException;
import eglx.services.Encoding;

public class HttpUtilities {
	public static final int HTTP_STATUS_FAILED = 500;
	public static final String HTTP_STATUS_MSG_FAILED = "FAILED";
	public static final int HTTP_STATUS_OK = 200;
	public static final String HTTP_STATUS_MSG_OK = "OK";
	private static final String HTTP_AUTHENTICATION_ID = "Authorization";
	private static final String HTTP_AUTHENTICATION_USER_ID = "egl_user";
	private static final String HTTP_AUTHENTICATION_PWD_ID = "egl_pwd";
	private static final String CONTENT_TYPE_VALUE_TEXT_HTML = "text/html";
	private static final String CONTENT_TYPE_VALUE_CHARSET_UTF8 = "; charset=UTF-8";
	private static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_METHOD_POST = "POST";
	public static final String HTTP_METHOD_PUT = "PUT";
	private static final String HTTP_METHOD_DELETE = "DELETE";
	public static final String HTTP_RESPONSE_TIMEOUT = "edt.proxy.invocation.timeout";
	private static final String EGL_PRIVATE_CALL = "EGLDEDICATED";
	private static final String CONTENT_TYPE_VALUE_TEXT_PLAIN = "text/plain";
	public static final String CONTENT_TEXT_KEY = "text";
	public static final String CONTENT_TYPE_KEY = "Content-Type";
	private static final String CONTENT_TYPE_VALUE_APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE_VALUE_APPLICATION_XML = "application/xml";
	private static final String CONTENT_TYPE_VALUE_APPLICATION_FORMDATA = "application/x-www-form-urlencoded";

	private HttpUtilities() {
	}
	
	public static void addContentType(EDictionary headers, Encoding encoding, String charset){
		StringBuilder contentType = new StringBuilder();
		if(Encoding._FORM == encoding){
			contentType.append(CONTENT_TYPE_VALUE_APPLICATION_FORMDATA);
		}
		else if(Encoding.XML == encoding){
			contentType.append(CONTENT_TYPE_VALUE_APPLICATION_XML);
		}
		else if(Encoding.JSON == encoding){
			contentType.append(CONTENT_TYPE_VALUE_APPLICATION_JSON);
		}
		else{
			contentType.append(CONTENT_TYPE_VALUE_TEXT_HTML);
		}
		contentType.append("; charset=");
    	if(charset == null || charset.isEmpty()){
    		charset = "UTF-8";
    	}
   		contentType.append(charset);
   		headers.put(CONTENT_TYPE_KEY, contentType.toString());
	}
	
	static Map<String, String> contentTypes = new HashMap<String, String>();
	static {
		contentTypes.put(CONTENT_TEXT_KEY, CONTENT_TYPE_VALUE_TEXT_PLAIN + CONTENT_TYPE_VALUE_CHARSET_UTF8);
		contentTypes.put("txt",  CONTENT_TYPE_VALUE_TEXT_PLAIN + CONTENT_TYPE_VALUE_CHARSET_UTF8);

		contentTypes.put("zip",  "application/zip");

		contentTypes.put("snd",  "audio/basic");
		contentTypes.put("au",   "audio/basic");
		contentTypes.put("wav",  "audio/x-wav");

		contentTypes.put("gif",  "image/gif");
		contentTypes.put("png",  "image/png");
		contentTypes.put("jpg",  "image/jpeg");
		contentTypes.put("jpeg", "image/jpeg");

		contentTypes.put("css",  "text/css");

		contentTypes.put("egl",  CONTENT_TYPE_VALUE_TEXT_HTML + CONTENT_TYPE_VALUE_CHARSET_UTF8);
		contentTypes.put("htm",  CONTENT_TYPE_VALUE_TEXT_HTML + CONTENT_TYPE_VALUE_CHARSET_UTF8);
		contentTypes.put("html", CONTENT_TYPE_VALUE_TEXT_HTML + CONTENT_TYPE_VALUE_CHARSET_UTF8);
	}

	public static String getContentType(String key) {
		return contentTypes.get(key);
	}
	public static void assignBody(Response response, AnyException jrte){
		
	}
	
	static void setAuthentication( ServiceInvoker invoker, Map headers )
	{
		if( !setAuthenticationFromEgl( invoker, headers ) )
		{
			setAuthenticationFromHttpAuthentication( invoker, headers );
		}
	}
	private static boolean setAuthenticationFromEgl( ServiceInvoker invoker, Map headers )
	{
		boolean set = false;
		String encodedUser = (String)headers.get( HTTP_AUTHENTICATION_USER_ID );
		String encodedPassword = (String)headers.get( HTTP_AUTHENTICATION_PWD_ID );
			if( encodedUser != null && encodedUser.length() > 0 )
			{
				String user = Base64.decode( encodedUser );
				String password = Base64.decode( encodedPassword );
				invoker.setUserId( user );
				invoker.setPassword( password );
				set = true;
			}
		return set;
	}
	
	private static boolean setAuthenticationFromHttpAuthentication( ServiceInvoker invoker, Map headers )
	{
		boolean set = false;
		String credentials = (String)headers.get( HTTP_AUTHENTICATION_ID );
		if( credentials == null )
		{
			credentials = (String)headers.get( HTTP_AUTHENTICATION_ID.toLowerCase() );
		}
		final String BASIC = "basic ";
		int idx;
		if( credentials != null && (idx = credentials.toLowerCase().indexOf( BASIC )) > -1 )
		{
			credentials = credentials.substring( idx + BASIC.length() );
			credentials = Base64.decode( credentials );
			if( (idx = credentials.indexOf(':')) > -1 ) 
			{
				String user = credentials.substring( 0, idx );
				String password = credentials.substring( idx + 1 );
				invoker.setUserId( user );
				invoker.setPassword( password );
				set = true;
			}
		}
		return set;
	}
	static void setAuthenticationFromHttpAuthentication( String user, String password, Map headers )
	{
		if( user != null && user.length() > 0 &&
				password != null && password.length() > 0 )
		{
			StringBuilder authentication = new StringBuilder(user).append(':').append(password);
			headers.put( HTTP_AUTHENTICATION_ID, new StringBuilder("Basic ").append(Base64.encode( authentication.toString() )).toString() );
		}
	}
	public static void validateUrl(Request restRequest ) throws IOException, AnyException
	{
		String urlStr = restRequest.uri;
		if( urlStr == null || urlStr.trim().length() == 0 )
		{
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage( Message.SOA_E_WS_PROXY_EMPTY_URL_EXCEPTION, urlStr );
		}
		if( urlStr.trim().toLowerCase().indexOf("http") == -1 )
		{
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage( Message.SOA_E_WS_PROXY_INVALID_HTTP_EXCEPTION, urlStr );
		}
		try
		{
			new URL(urlStr);
		}
		catch( MalformedURLException mfue )
		{
			InvalidArgumentException ex = new InvalidArgumentException();
			throw ex.fillInMessage( Message.SOA_E_WS_PROXY_INVALID_URL_EXCEPTION, urlStr, ServiceUtilities.getMessage( mfue ) );
		}
	}
	static String urlEncode( Map<String, String> parameters, boolean isQueryParameters ) throws UnsupportedEncodingException
	{
		if(parameters.size() == 0)
		{
			return "";
		}
		
		StringBuilder retVal = new StringBuilder();
		if(isQueryParameters)
		{
			retVal.append('?');
		}
		
		for (Iterator<String> iter = parameters.keySet().iterator(); iter.hasNext();) {
			String key = URLEncoder.encode(iter.next(), ServiceUtilities.UTF8);
			String value = URLEncoder.encode(parameters.get(key), ServiceUtilities.UTF8);
			retVal.append(key);
			retVal.append('=');
			retVal.append(value);
			
			if(iter.hasNext()){
				retVal.append('&');
			}				
		}
	
		return retVal.toString();
	}
    static String unescape(String msg) 
    {
    	try 
    	{
    	    return URLDecoder.decode(msg, "UTF-8");
    	} 
    	catch (UnsupportedEncodingException e) 
    	{
    	    e.printStackTrace();
    	    return msg;
    	}
    }

    public static String httpMethodToString(HttpMethod method){
    	if(HttpMethod._DELETE.equals(method)){
    		return HTTP_METHOD_DELETE;
    	}
    	else if(HttpMethod._GET.equals(method)){
    		return HTTP_METHOD_GET;
    	}
    	else if(HttpMethod.POST.equals(method)){
    		return HTTP_METHOD_POST;
    	}
    	else if(HttpMethod.PUT.equals(method)){
    		return HTTP_METHOD_PUT;
    	}
    	else{
    		return "";
    	}
    }
	public static HttpMethod convert(String method){
		if(HTTP_METHOD_DELETE.equalsIgnoreCase(method)){
			return HttpMethod._DELETE;
		}
		else if(HTTP_METHOD_GET.equalsIgnoreCase(method)){
			return HttpMethod._GET;
		}
		else if(HTTP_METHOD_POST.equalsIgnoreCase(method)){
			return HttpMethod.POST;
		}
		else if(HTTP_METHOD_PUT.equalsIgnoreCase(method)){
			return HttpMethod.PUT;
		}
		else{
			return null;
		}
	}
	public static HttpMethod convert(int method){
		if(HttpMethod._DELETE.getValue() == method){
			return HttpMethod._DELETE;
		}
		else if(HttpMethod._GET.getValue() == method){
			return HttpMethod._GET;
		}
		else if(HttpMethod.POST.getValue() == method){
			return HttpMethod.POST;
		}
		else if(HttpMethod.PUT.getValue() == method){
			return HttpMethod.PUT;
		}
		else{
			return null;
		}
	}

}
