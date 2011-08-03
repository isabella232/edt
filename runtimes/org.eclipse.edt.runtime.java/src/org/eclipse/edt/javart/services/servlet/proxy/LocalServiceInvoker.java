/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.edt.javart.services.servlet.proxy;

import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.services.servlet.ServletUtilities;

import eglx.http.HttpRequest;
import eglx.http.HttpResponse;
import eglx.http.HttpUtilities;
import eglx.services.Encoding;
import eglx.services.ServiceKind;

public class LocalServiceInvoker extends ServiceInvoker{
	private long elapseTime;
	
	public LocalServiceInvoker(ExecutableBase program) {
		super(program);
	}
	HttpResponse invoke( RuiBrowserHttpRequest ruiRequest, HttpRequest innerRequest )throws Exception
	{
		if (trace()){
			tracer().put( "Dedicated service." );
		}
		String ruiRequestBody = ruiRequest.getContent(); 
		HttpResponse innerResponse = new HttpResponse();
		HttpRequest request = ServletUtilities.createHttpRequest(program(), ruiRequestBody);
		traceElapsedTime( true );
		FunctionInfo info = getFunctionInfo( Encoding.JSON, Encoding.JSON, request.getBody() );

		if (trace()){
			tracer().put( "  invoking function " + info != null && info.getFunctionName() != null ? info.getFunctionName() : "null" );
		}
		String result = wrapperProxyReturn( invokeEglService( info ) );
		traceElapsedTime( false );
		if (trace()){
			tracer().put( "return data from dedicated service:" + result == null ? "null" : result );
		}
		innerResponse.setBody( result );
		boolean failed = resultContainsError( result );
		innerResponse.setStatus( failed ? HttpUtilities.HTTP_STATUS_FAILED: HttpUtilities.HTTP_STATUS_OK );
		innerResponse.setStatusMessage(failed ? HttpUtilities.HTTP_STATUS_MSG_FAILED : HttpUtilities.HTTP_STATUS_MSG_OK);
		return innerResponse;
	}
	private String wrapperProxyReturn( Object returnVal )
	{
		return returnVal == null ? "{}" : returnVal.toString();
	}
    protected void traceElapsedTime( boolean start )
    {
    	if( start )
    	{
    		elapseTime = System.currentTimeMillis();
    	}
    	else
    	{
			elapseTime = System.currentTimeMillis() - elapseTime;
			if (trace()){
				tracer().put( "Service response time:" + String.valueOf( elapseTime ) );
			}
    	}
    }

	protected boolean resultContainsError( String result )
	{
		return result.indexOf( "{\"error\" : {" ) != -1;
	}

	@Override
	ServiceKind getServiceKind(HttpRequest innerRequest) {
		return ServiceKind.EGL;
	}
}
