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
package org.eclipse.edt.javart.services.servlet.rest.rpc;

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.edt.javart.Constants;

import eglx.http.HttpMethod;
import eglx.http.HttpUtilities;
import eglx.services.Encoding;

public class RestServiceProjectInfo	implements Serializable
{
	private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
	class ServiceFunctionInfo implements Serializable
	{
		private static final long serialVersionUID = 75L;
		private String className;
		private String uri;
		private String functionName;
		private Encoding inEncoding;
		private Encoding outEncoding;
		private boolean isStatefulService;
		private boolean isHostProgramService;
			ServiceFunctionInfo(String uri, String className, boolean isStatefulService, boolean isHostProgramService, String functionName, Encoding inEncoding, Encoding outEncoding) 
		{
			this.className = className;
			this.uri = uri;
			this.functionName = functionName;
			this.inEncoding = inEncoding;
			this.outEncoding = outEncoding;
			this.isStatefulService = isStatefulService;
			this.isHostProgramService = isHostProgramService;
		}
		String getClassName() 
		{
			return className;
		}
		String getFunctionName() 
		{
			return functionName;
		}
		Encoding getInEncoding() 
		{
			return inEncoding;
		}
		Encoding getOutEncoding() 
		{
			return outEncoding;
		}
		public boolean isStatefulService()
		{
			return isStatefulService;
		}
		public String toString()
		{
			return uri + " - " + className + '.' + functionName + " inEncoding:" + encoding( inEncoding ) + " outEncoding:" + encoding( outEncoding ) + (isStatefulService ? " stateful" : "");
		}
		boolean isHostProgramService()
		{
			return isHostProgramService;
		}
	}
	private static String encoding( Encoding encoding )
	{
		if( Encoding.XML.equals(encoding) )
		{
			return "Encoding.XML";
		}
		else if( Encoding.JSON.equals(encoding) )
		{
			return "Encoding.JSON";
		}
		else if( Encoding.NONE.equals(encoding) )
		{
			return "Encoding.NONE";
		}
		else if( Encoding._FORM.equals(encoding) )
		{
			return "Encoding._FORM";
		}
		else{
			return "Encoding.USE_CONTENTTYPE";
		}
	}
	private String contextRoot;
	private URL[] classpath;
	/*
	 * this is written to support true rest services with uri's that contain substitution variables
	 * The map key is the httpMethod and the value is a map of uri based on the number of segments in the uri
	 * The key to the uri map is the number of segments and the value is a map of ServiceFunction
	 * The key to the service map is the uri (for now it's fixed but when we have real rest services we just create a new key removing the substitution variable)
	 */
	private Map<String, Map<Integer, Map<String, ServiceFunctionInfo>>> httpMethods;
	RestServiceProjectInfo( String contextRoot, URL[] classpath ) 
	{
		this.classpath = classpath;
		this.httpMethods = new HashMap<String, Map<Integer, Map<String, ServiceFunctionInfo>>>();
		this.contextRoot = contextRoot;
	}
	
	String getContextRoot() {
		return contextRoot;
	}

	URL[] getClasspath() 
	{
		return classpath;
	}
	
	void setClasspath(URL[] classpath) 
	{
		this.classpath = classpath;
	}

	private static Map<Integer, Map<String, ServiceFunctionInfo>> httpMethod( RestServiceProjectInfo projectInfo, String httpMethodName )
	{
		Map<Integer, Map<String, ServiceFunctionInfo>> httpMethodMap = projectInfo.httpMethods.get( httpMethodName.toLowerCase() );
		if( httpMethodMap == null )
		{
			httpMethodMap = new HashMap<Integer, Map<String, ServiceFunctionInfo>>();
			projectInfo.httpMethods.put( httpMethodName.toLowerCase(), httpMethodMap );
		}
		return httpMethodMap;
	}
	private static void addURI( String uri, Map<Integer, Map<String, ServiceFunctionInfo>> httpMethodMap, ServiceFunctionInfo serviceFunction )
	{
		StringTokenizer tokens = new StringTokenizer( uri, "/" );
		Integer tokenCount = Integer.valueOf( tokens.countTokens() );
		Map<String, ServiceFunctionInfo> uris = httpMethodMap.get( tokenCount );
		if( uris == null )
		{
			uris = new HashMap<String, ServiceFunctionInfo>();
			httpMethodMap.put( tokenCount, uris );
			
		}
		uris.put(uri.toLowerCase(), serviceFunction );
	}
	
	private static void removeURI( String uri, Map<Integer, Map<String, ServiceFunctionInfo>> httpMethodMap )
	{
		StringTokenizer tokens = new StringTokenizer( uri, "/" );
		Integer tokenCount = Integer.valueOf( tokens.countTokens() );
		Map<String, ServiceFunctionInfo> uris = httpMethodMap.get( tokenCount );
		if( uris == null )
		{
			uris = new HashMap<String, ServiceFunctionInfo>();
			httpMethodMap.put( tokenCount, uris );
			
		}
		uris.remove(uri.toLowerCase() );
	}
	
	void addURI( String uri, String httpMethodName, String className, boolean isStatefulService, boolean isHostProgramService, String eglFunction, String inEncoding, String outEncoding )
	{
		ServiceFunctionInfo serviceFunctionInfo = this.new ServiceFunctionInfo( uri, className, isStatefulService, isHostProgramService, eglFunction, convertEncoding( inEncoding ), convertEncoding( outEncoding ) );
		
		Map<Integer, Map<String, ServiceFunctionInfo>> httpMethodMap = httpMethod( this, httpMethodName );
		addURI( uri, httpMethodMap, serviceFunctionInfo );
	}
	
	void removeURI( String uri, String httpMethodName ) {
		Map<Integer, Map<String, ServiceFunctionInfo>> httpMethodMap = httpMethod( this, httpMethodName );
		removeURI( uri, httpMethodMap );
	}
	
	private Encoding convertEncoding( String encoding )
	{
		Encoding retEncoding;
		if( String.valueOf(Encoding.XML).equals(encoding) )
		{
			retEncoding = Encoding.XML;
		}
		else if( String.valueOf(Encoding.JSON).equals(encoding) )
		{
			retEncoding = Encoding.JSON;
		}
		else if( String.valueOf(Encoding.NONE).equals(encoding) )
		{
			retEncoding = Encoding.NONE;
		}
		else if( String.valueOf(Encoding._FORM).equals(encoding) )
		{
			retEncoding = Encoding._FORM;
		}
		else{
			retEncoding = Encoding.USE_CONTENTTYPE;
		}
		return retEncoding;
	}
	ServiceFunctionInfo getServiceFunctionInfo( String uri, HttpMethod httpMethod ) 
	{
		ServiceFunctionInfo serviceFunctionInfo = null;
		Map<Integer, Map<String, ServiceFunctionInfo>> httpMethodMap = httpMethods.get( HttpUtilities.httpMethodToString(httpMethod).toLowerCase() );
		if( httpMethodMap != null )
		{
			StringTokenizer tokens = new StringTokenizer( uri, "/" );
			Integer tokenCount = Integer.valueOf( tokens.countTokens() );
			Map<String, ServiceFunctionInfo> uris = httpMethodMap.get( tokenCount );
			if( uris != null )
			{
				ServiceFunctionInfo serviceFunction = uris.get(uri.toLowerCase()); 
				if( serviceFunction != null )
				{
					serviceFunctionInfo = serviceFunction;
				}
			}
		}
		return serviceFunctionInfo;
	}

	public String toString()
	{
		String indent = "    ";
		String returnVal = "ContextRoot:" + contextRoot + "\n";
		if( classpath != null && classpath.length > 0 )
		{
			returnVal += indent + "Classpath:";
			for( int idx = 0; idx < classpath.length; idx++ )
			{
				if( idx > 1 )
				{
					returnVal += ';';
				}
				returnVal += classpath[idx].toString();
			}
			returnVal += '\n';
		}
		returnVal += methodsToString( httpMethods, indent + "    " );
		return returnVal;
	}
	private static String methodsToString( Map<String, Map<Integer, Map<String, ServiceFunctionInfo>>> httpMethods, String indent )
	{
		String returnVal = "";
		Map.Entry<String, Map<Integer, Map<String, ServiceFunctionInfo>>> methodMapEntry;
		for( Iterator<Map.Entry<String, Map<Integer, Map<String, ServiceFunctionInfo>>>> itr1 = httpMethods.entrySet().iterator(); itr1.hasNext();)
		{
			methodMapEntry = itr1.next();
			returnVal += "HTTP Method:" + (String)methodMapEntry.getKey() + '\n';
			returnVal += urisToString( methodMapEntry.getValue(), indent + "    " );
		}
		return returnVal;
	}
	private static String urisToString(Map<Integer, Map<String, ServiceFunctionInfo>> urisMap, String indent )
	{
		String returnVal = "";
		Map<String, ServiceFunctionInfo> uris;
		Map.Entry<String, ServiceFunctionInfo> uriFunction;
		for( Iterator<Map<String, ServiceFunctionInfo>> itr1 = urisMap.values().iterator(); itr1.hasNext();)
		{
			uris = itr1.next();
			for( Iterator<Map.Entry<String, ServiceFunctionInfo>> itr2 = uris.entrySet().iterator(); itr2.hasNext();)
			{
				uriFunction = itr2.next();
				returnVal += indent + uriFunction.getValue().toString() + '\n';
			}
		}
		return returnVal;
	}
}
