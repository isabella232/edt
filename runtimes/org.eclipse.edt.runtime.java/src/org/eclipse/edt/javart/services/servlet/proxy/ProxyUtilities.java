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

import java.util.Iterator;
import java.util.Map;

import eglx.http.Request;



public class ProxyUtilities 
{
	private static final String EGL_DEDICATED_CALL = "EGLDEDICATED";
	public static final String EGL_SOAP_CALL = "EGLSOAP";
	public static final String EGL_REST_CALL = "EGLREST";

	
	private ProxyUtilities() 
	{
	}

	public static boolean isEGLDedicatedCall( Request innerRequest )	
	{
		return innerRequest.headers != null && innerRequest.headers.containsKey( EGL_DEDICATED_CALL );
	}
	
    static String convert( Map<?, ?> map )
    {
    	StringBuilder buffer = new StringBuilder();
    	if(map != null){
	    	Map.Entry<?, ?> entry;
	    	for( Iterator<?> itr = map.entrySet().iterator(); itr.hasNext(); buffer.append( ' ' ) )
	    	{
	    		entry = (Map.Entry<?, ?>)itr.next();
	    		if( entry.getKey() != null )
	    		{
	    			buffer.append( entry.getKey().toString() );
	    			buffer.append( ':' );
	    		}
	    		if( entry.getValue() instanceof Map<?, ?> )
	    		{
	    			buffer.append( '(' );
	    			buffer.append( convert( (Map<?, ?>)entry.getValue() ) );
	    			buffer.append( ')' );
	    		}
	    		else if(  entry.getValue() != null )
	    		{
	       			buffer.append( entry.getValue().toString() );
	    		}
	    	}
    	}
    	return buffer.toString();
    }
	    
	public static boolean isSoapCall( Request innerRequest )	
	{
		return innerRequest.headers != null && innerRequest.headers.containsKey( EGL_SOAP_CALL );
	}
}
