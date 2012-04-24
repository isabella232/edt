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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class RuiBrowserHttpRequest
{
	private String URL;
	private Map<String, String> headers;
	private Map<String, String> arguments;
	private String method;
	private String content;
	private Map<String, String> contentArguments;
	public static String CONTENT_LENGTH_ID = "Content-Length";
	public static String JSON_RPC_GET_METHOD_ID = "GET";
	public static final int MAX_NUMBER_CHARS = Integer.MAX_VALUE;

	private RuiBrowserHttpRequest(){
	}

	public static RuiBrowserHttpRequest createNewRequest(javax.servlet.http.HttpServletRequest httpServletReq ) throws IOException {

		RuiBrowserHttpRequest newRequest = new RuiBrowserHttpRequest();
		newRequest.method = httpServletReq.getMethod();
		newRequest.URL = httpServletReq.getRequestURI(); //processRequestURL(request);
		newRequest.headers = processHeaders(httpServletReq);
		if( JSON_RPC_GET_METHOD_ID.equalsIgnoreCase( newRequest.method ) )
		{
			newRequest.arguments = httpServletReq.getParameterMap();//processArgs(request);
		}
		BufferedReader reader = httpServletReq.getReader();
		newRequest.content = processContent(httpServletReq.getContentLength(), reader);

		return newRequest;
	}

	public static RuiBrowserHttpRequest createNewRequest(Socket client) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "utf-8"));
		String request = reader.readLine();

		if(request != null){
			RuiBrowserHttpRequest newRequest = new RuiBrowserHttpRequest();
			newRequest.method = request.substring(0, request.indexOf(' ')).trim();
			request = request.substring(request.indexOf(' ')); // strip GET header
			request = request.substring(0, request.lastIndexOf(' '));	// strip HTTP/1.0 trailer
			newRequest.URL = processRequestURL(request);
			newRequest.arguments = processArgs(request);
			newRequest.headers = processHeaders(reader);
			newRequest.content = processContent(getContentLength(newRequest), reader);
			return newRequest;
		}else{
			return null;
		}
	}

	private static int getContentLength(RuiBrowserHttpRequest request) {
		String lengthString = (String)request.headers.get(CONTENT_LENGTH_ID);
		if(lengthString == null || lengthString.length() == 0 )
		{
			for( Iterator<String> itr = request.headers.keySet().iterator(); itr.hasNext(); ){
				String key = itr.next();
				if( CONTENT_LENGTH_ID.equalsIgnoreCase((String)key) ){
					lengthString = (String)request.headers.get(key);
					break;
				}
			}
		}
		int contentLength = -1;
		if( lengthString != null ){
			try{
				contentLength = Integer.parseInt(lengthString);
			}
			catch(NumberFormatException nfe ){}
		}
		return contentLength;
	}

	private static Map<String, String> processContentArgs(String request) {
		return processArgs("?" + request);
	}
	
	/**
	 * @return The content parsed as arguments. The content uses '&' as a delimeter,
	 *         so any values in the key-value pairs must have '&' escaped, else
	 *         the parsing may throw an exception. Do not call this method if
	 *         this is not guarenteed by the content for a particular request
	 *         (e.g. JSON requests)!
	 */
	public Map<String, String> getContentArguments() {
		if (this.contentArguments == null) {
			this.contentArguments = processContentArgs(content);
		}
		return this.contentArguments;
	}
	public String getURL(){
		return URL;
	}

	public String getMethod(){
		return method;
	}

	public Map<String, String> getHeaders(){
		return headers;
	}

	public String getContent(){
		return content;
	}

	private static String processContent(int contentLength, BufferedReader reader) throws IOException
	{
		StringBuilder contentBuffer = new StringBuilder();
		if( contentLength > 0 ){	
			//int nRead = reader.read();
			for (int i = 0; i < contentLength; )
			{				
				char nRead = (char)reader.read();
				contentBuffer.append(nRead);
				
				//need to calculate the byte count of a character
				String str = Character.toString(nRead);
				byte[] bytes = str.getBytes("utf-8");
				i += bytes.length;
			}
		}
		return contentBuffer.toString();
	}

	public static Map<String, String> processHeaders(javax.servlet.http.HttpServletRequest httpServletReq) throws IOException{
		Map<String, String> result = new HashMap<String, String>();
		Enumeration headerEnum = httpServletReq.getHeaderNames();
		while(headerEnum.hasMoreElements()){
			String headername = (String)headerEnum.nextElement();
			result.put(headername, httpServletReq.getHeader(headername));
		}

		return result;
	}

	private static Map<String, String> processHeaders(BufferedReader reader) throws IOException{
		Map<String, String> result = new HashMap<String, String>();
		String header = reader.readLine();
		while(header != null && header.length() > 0){
			String label = header.substring(0, header.indexOf(':'));
			String value = header.substring(header.indexOf(':') + 2);
			result.put(label, value);
			header = reader.readLine();
		}

		return result;
	}

	private static String processRequestURL(String request) {
    	int questionMarkIndex = request.indexOf('?');
		if (questionMarkIndex != -1) {
			return request.substring(0, questionMarkIndex);
		}
		return request;
	}

	private static Map<String, String> processArgs(String request) {
		Map<String, String> arguments = new HashMap<String, String>();
		int questionMarkIndex = request.indexOf('?');
		if (questionMarkIndex != -1) {
			request = request.substring(questionMarkIndex+1);
			StringTokenizer arg = new StringTokenizer(request, "&");
			while (arg.hasMoreTokens()) {
				try{
					String nextToken = arg.nextToken();
					String key = nextToken.substring(0, nextToken.indexOf("="));
					String value = URLDecoder.decode(nextToken.substring(nextToken.indexOf("=") + 1), "UTF-8");
					if("contextKey".equals(key)){
						value = value.replace(".js", "");
					}
					arguments.put(key, value);
				} catch (UnsupportedEncodingException e) {
					// Ignore this argument
				}
				
			}
		}
		return arguments;
	}

	public String toString(){
		StringBuilder result = new StringBuilder();

		result.append("METHOD: " + method + "\n");
		result.append("URL: " + URL + "\n");
		result.append("Arguments: \n");
		for (Iterator<String> iter = arguments.keySet().iterator(); iter.hasNext();) {
			String argument = iter.next();
			result.append("\t" + argument + " = " + (String)arguments.get(argument) + "\n");
		}
		result.append("Headers: \n");
		for (Iterator<String> iter = headers.keySet().iterator(); iter.hasNext();) {
			String header = iter.next();
			result.append("\t" + header + " = " + headers.get(header) + "\n");
		}

		return result.toString();
	}

	public Map<String, String> getArguments() {
		return arguments;
	}

	public void setURL(String url) {
		this.URL = url;
	}
}
