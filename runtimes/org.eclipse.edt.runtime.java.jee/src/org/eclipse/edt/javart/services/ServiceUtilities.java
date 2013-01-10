/*******************************************************************************
 * Copyright © 2006, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.services;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.edt.javart.util.JavartUtil;

import eglx.lang.AnyException;
import eglx.services.ServiceInvocationException;
import eglx.services.ServiceKind;



public class ServiceUtilities 
{
	public static String UTF8 = "UTF-8";

	
	private ServiceUtilities() 
	{
	}

	
	public static ServiceInvocationException buildServiceInvocationException(String id, Object[] params, Throwable t, ServiceKind serviceKind )
	{
		return ServiceUtilities.buildInvocationException(id, params, 
				"", "", "", t, serviceKind );
	}
	public static ServiceInvocationException buildInvocationException(String id, Object[] params, String detail1, String detail2, String detail3, Throwable t, ServiceKind serviceKind ) 
	{
		String message = JavartUtil.errorMessage( id, params);
		while( t instanceof InvocationTargetException &&
				((InvocationTargetException)t).getCause() != null ){
			t = ((InvocationTargetException)t).getCause();
		}
		ServiceInvocationException sie = new ServiceInvocationException();
		if(detail3 == null || detail3.length() == 0){
			detail3 = getMessage(t);
		}
		sie.setMessage((message==null)?"":message);
		sie.setMessageID( (id==null)?"":id );
		sie.setDetail1((detail1==null)?"":detail1);
		sie.setDetail2((detail2==null)?"":detail2);
		sie.setDetail3((detail3==null)?"":detail3);
		sie.setSource(serviceKind);
		if ( t != null )
		{
			sie.initCause( t );
		}
		return sie;
	}

	public static String getMessage( Throwable t )
	{
		while( t instanceof InvocationTargetException &&
				((InvocationTargetException)t).getCause() != null ){
			t = ((InvocationTargetException)t).getCause();
		}
		if( t instanceof AnyException )
		{
			return t.getMessage();
		}
		else if( t != null && t.getMessage() != null )
		{
			return t.getClass().getName() + ":" + t.getMessage();
		}
		else if( t != null &&  t.toString() != null )
		{
			return t.toString();
		}
		else
		{
			return "";
		}

	}
    static String convert( Map<?, ?> map )
    {
    	StringBuilder buffer = new StringBuilder();
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
    	return buffer.toString();
    }
	
    public static String convert(ServiceKind serviceKind){
    	if(ServiceKind.EGL.equals(serviceKind)){
    		return "EGL";
    	}
    	else if(ServiceKind.NATIVE.equals(serviceKind)){
    		return "NATIVE";
    	}
    	else if(ServiceKind.REST.equals(serviceKind)){
    		return "REST";
    	}
    	else if(ServiceKind.WEB.equals(serviceKind)){
    		return "WEB";
    	}
    	else{
    		return "";
    	}
    }
}
