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

import java.util.List;
import java.util.Map;

import eglx.http.Request;



public class ProxyUtilities 
{
	private static final String EGL_DEDICATED_CALL = "EGLDEDICATED";
	public static final String EGL_SOAP_CALL = "EGLSOAP";
	public static final String EGL_REST_CALL = "EGLREST";
	public static final String HTTP10STATUS = "HTTP/1.0";

	
	private ProxyUtilities() 
	{
	}

	public static boolean isEGLDedicatedCall( Request innerRequest )	
	{
		return innerRequest.headers != null && innerRequest.headers.containsKey( EGL_DEDICATED_CALL );
	}
	
    public static String convert( Map<?, ?> map, String EOL )
    {
    	StringBuilder buffer = new StringBuilder();
    	if(map != null){
	    	for( Map.Entry<?, ?> entry : map.entrySet())
	    	{
	    		boolean nullKey = entry.getKey() == null || entry.getKey().toString() == null;
	    		boolean addEOL = true;
	    		if(!nullKey)
	    		{
	    			buffer.append(entry.getKey().toString());
	    			buffer.append( ':' );
	    		}
	    		if( entry.getValue() instanceof Map<?, ?> )
	    		{
	    			buffer.append( '(' );
	    			buffer.append( convert((Map<?, ?>)entry.getValue(), EOL));
	    			buffer.append( ')' );
	    		}
	    		else if(entry.getValue() instanceof List<?>)
	    		{
	    			boolean addComma = false;
	    			for(Object value : (List<?>)entry.getValue()){
	    				if(addComma){
	    					buffer.append(',');	
	    				}
	    				addComma = true;
	    				//the status must appear first, so insert it at the beginning
	    				if(value!= null && value.toString().indexOf(HTTP10STATUS) > -1){
	    					buffer.insert(0, EOL);
	    					buffer.insert(0, value.toString());
	    					addEOL = false;
	    				}
	    				else{
	    					buffer.append(value.toString());
	    				}
	    			}
	    		}
	    		else if(entry.getValue() != null )
	    		{
	       			buffer.append(entry.getValue().toString());
	    		}
	    		if(addEOL){
	    			buffer.append(EOL);
	    		}
	    	}
    	}
		buffer.append(EOL);
    	return buffer.toString();
    }
	    
	public static boolean isSoapCall( Request innerRequest )	
	{
		return innerRequest.headers != null && innerRequest.headers.containsKey( EGL_SOAP_CALL );
	}
}
