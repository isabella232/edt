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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.eclipse.edt.javart.Runtime;
import org.eclipse.edt.javart.services.servlet.Invoker;

import eglx.http.Request;


abstract class EglHttpConnection extends Invoker{
	
	protected EglHttpConnection() {
	}
	protected HttpURLConnection getHttpProxyConnection( Request restRequest ) throws IOException
	{
		if( useProxy() )
		{
			return getProxyConnection(restRequest);
		}
		return null;
	}
	protected HttpURLConnection openConnection( Request restRequest ) throws IOException
	{
		HttpURLConnection connection = getHttpProxyConnection(restRequest);
		
		if( connection == null )
		{
            connection = (HttpURLConnection)new URL(restRequest.uri).openConnection();
		}
		return connection;
	}

	private boolean useProxy()
	{
		return"true".equals( Runtime.getRunUnit().getProperties().get(("egl.service.invocation.useProxy") ) );
	}
	
	private static HttpURLConnection getProxyConnection( Request restRequest ) throws IOException
	{
		String urlString = restRequest.uri;
		Proxy proxy = getProxy( urlString );
		if( proxy != null )
		{
			return (HttpURLConnection)new URL(urlString).openConnection( proxy );
		}
		return null;
	}

	private static Proxy getProxy( String urlString )
	{
		Proxy proxy = null;
		ProxySelector ps = ProxySelector.getDefault(); 
		URI uri;
		try {
			uri = new URI(urlString);
			List<Proxy> proxies = ps.select(uri);
			if( proxies != null && proxies.size() > 0 )
			{
				Proxy tempProxy;
				for( int idx = 0; idx < proxies.size(); idx++ )
				{
					/*
					 * first try to use http
					 * then socks
					 */
					tempProxy = (Proxy)proxies.get(idx);
					if( tempProxy.type() == Proxy.Type.HTTP )
					{
						proxy = tempProxy;
						break;
					}
					else if( tempProxy.type() == Proxy.Type.SOCKS && 
							proxy == null )
					{
						proxy = tempProxy;
					}
				}
			}
		} catch (URISyntaxException e) {
		}
		return proxy;
	}

}
