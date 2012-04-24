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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



class RestRpcUtilities 
{
	private static final String EGL_FUNCTION_ATTR = "eglfunction";
	private static final String CONTEXT_ROOT_ELEM = "contextroot";
	private static final String CLASSPATH_ELEM = "classpath";
	private static final String URI_ELEM = "uri";
	private static final String CLASSNAME_ATTR = "classname";
	private static final String STATEFUL_SERVICE_ATTR = "stateful";
	private static final String HOST_PROGRAM_SERVICE_ATTR = "hostProgramService";
	private static final String SERVICE_MAPPING_ELEM = "servicemapping";
	private static final String HTTP_FUNCTION_ATTR = "httpmethod";
	private static final String IN_ENCODING_ATTR = "in-encoding";
	private static final String OUT_ENCODING_ATTR = "out-encoding";
	private static final String TRUE_VALUE = "true";
	private static final String SERVICE_SERVLET = "EGL REST Service servlet";
	private static final String URI_MAPPING_FILE_SUFFIX = "-uri.xml";
	private static String CUSTOM_EGLSOAP_RESPONSE_HEADER = "CUSTOMEGLSOAPRESPONSEHEADER";
	private static final int FORMAT_NONE = 0;
	private static final int FORMAT_XML = 1;
	private static final int FORMAT_JSON = 2;
	private static final int FORMAT_FORM = 3;

	private RestRpcUtilities() 
	{
	}

	static RestServiceProjectInfo getRestServiceInfo( String resource )
	{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		RestServiceProjectInfo projectInfo = null;
		try
		{
			DocumentBuilder dom = domFactory.newDocumentBuilder();
			InputStream is;
			File file = new File( resource );
			if( file.exists() && file.isAbsolute() )
			{
				is = new FileInputStream( file );
			}
			else
			{
				is = RestRpcUtilities.class.getClassLoader().getResourceAsStream( resource );
			}
			Document doc = dom.parse( is );
			projectInfo = processDoc( doc );
		}
		catch( Exception e )
		{
			projectInfo = new RestServiceProjectInfo( "RestServiceProjectInfo", new URL[0] );
		}
		return projectInfo;
	}
	
	
	private static RestServiceProjectInfo processDoc( Document doc )
	{
		Element serviceMappings = (Element)doc.getFirstChild();
		URL[] classpath = getClasspath( serviceMappings );
		String contextRoot = getContextRoot( serviceMappings );
		RestServiceProjectInfo projectInfo = new RestServiceProjectInfo( contextRoot, classpath );
		NodeList serviceMapping = serviceMappings.getElementsByTagName(SERVICE_MAPPING_ELEM);
		int nodesCnt2 = serviceMapping == null ? 0 : serviceMapping.getLength();
		for( int idx2 = 0; idx2 < nodesCnt2; idx2++ )
		{
			processServiceMapping( (Element)serviceMapping.item(idx2), projectInfo );
		}
		return projectInfo;
	}
	
	private static String getContextRoot( Element serviceMappings )
	{
		String contextRoot = "";
		NodeList list = serviceMappings.getElementsByTagName( CONTEXT_ROOT_ELEM );
		if( list != null && list.getLength() > 0 )
		{
			contextRoot = getNodeTextValue( list.item( 0 ) );
		}
		return contextRoot;
	}
	private static URL[] getClasspath( Element serviceMappings )
	{
		String classpath = "";
		NodeList list = serviceMappings.getElementsByTagName( CLASSPATH_ELEM );
		if( list != null && list.getLength() > 0 )
		{
			classpath = getNodeTextValue( list.item( 0 ) );
		}
		ArrayList<URL> urls = new ArrayList<URL>();
		if( classpath != null )
		{
			StringTokenizer parser = new StringTokenizer( classpath, ";" );
			File file;
			for( int idx = 0; parser.hasMoreElements(); idx++ )
			{
				file = new File( parser.nextToken() );
				if( file.exists() )
				{
					try
					{
						urls.add( new URI( file.getAbsolutePath() ).toURL() );
					}
					catch( Exception e ){}
				}
			}
		}
		return (URL[])urls.toArray( new URL[ urls.size() ]);
	}
	private static void processServiceMapping( Element serviceMapping, RestServiceProjectInfo projectInfo )
	{
		String className = serviceMapping.getAttribute(CLASSNAME_ATTR);
		String stateful = serviceMapping.getAttribute(STATEFUL_SERVICE_ATTR);
		boolean isStatefulService = stateful != null && stateful.equalsIgnoreCase( TRUE_VALUE );
		String hostProgramService = serviceMapping.getAttribute(HOST_PROGRAM_SERVICE_ATTR);
		boolean isHostProgramService = hostProgramService != null && hostProgramService.equalsIgnoreCase( TRUE_VALUE );
		
		if( className != null && className.length() > 0 )
		{
			NodeList urls = serviceMapping.getElementsByTagName(URI_ELEM);
			Element url;
			String eglFunctionName;
			String httpMethodName;
			String inEncoding;
			String outEncoding;
			int urlNodesCnt = urls == null ? 0 : urls.getLength();
			for( int idx2 = 0; idx2 < urlNodesCnt; idx2++ )
			{
				url = (Element)urls.item(idx2);
				if( url != null )
				{
					String urlValue = getNodeTextValue( url );
					if( urlValue != null )
					{
						urlValue = urlValue.trim();
						eglFunctionName = url.getAttribute(EGL_FUNCTION_ATTR);
						httpMethodName = url.getAttribute(HTTP_FUNCTION_ATTR);
						inEncoding = url.getAttribute(IN_ENCODING_ATTR);
						outEncoding = url.getAttribute(OUT_ENCODING_ATTR);
						projectInfo.addURI(urlValue, httpMethodName, className, isStatefulService, isHostProgramService, eglFunctionName, inEncoding, outEncoding);
					}
				}
			}
		}
	}
	
	private static String getNodeTextValue( Node node )
	{
		String value = "";
		NodeList nodes = node.getChildNodes();
		for( int idx = 0; idx < nodes.getLength(); idx++ )
		{
			node = nodes.item(idx);
			if( node.getNodeType() == Node.TEXT_NODE )
			{
				value = node.getNodeValue();
				break;
			}
		}
		return value;
	}
}
