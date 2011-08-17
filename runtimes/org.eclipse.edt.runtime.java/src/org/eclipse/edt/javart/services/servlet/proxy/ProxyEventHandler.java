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

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.resources.ExecutableBase;
import org.eclipse.edt.javart.resources.Trace;
import org.eclipse.edt.javart.services.servlet.ServletUtilities;

import eglx.http.HttpRequest;
import eglx.http.HttpResponse;
import eglx.http.HttpUtilities;
import eglx.services.ServiceBindingException;
import eglx.services.ServiceInvocationException;
import eglx.services.ServiceUtilities;


public class ProxyEventHandler //extends EglHttpConnection
{
	private ExecutableBase program;
	private Trace tracer;
	
	public ProxyEventHandler(ExecutableBase program)
	{
		this.program = program;
	}

	protected ExecutableBase program()
	{
		return program;
	}

	public HttpResponse runProxy(String urlString, RuiBrowserHttpRequest ruiRequest, HttpRequest innerRequest )
	{
		if(trace()){
			tracer.put( new StringBuilder(" Request URL:")
    				.append(ruiRequest.getURL())
    				.append(" method:")
    				.append(ruiRequest.getMethod())
    				.append(" header:")
    				.append(ProxyUtilities.convert( ruiRequest.getHeaders() ))
    				.append(" content:")
    				.append(ruiRequest.getContent()).toString());
		}
		Invoker invoker = null;
		if( urlString.indexOf("___proxy") != -1 && 
					ProxyUtilities.isEGLDedicatedCall(innerRequest))
		{
			invoker = new LocalServiceInvoker(program());
		}
		else if( urlString.indexOf("___proxy") != -1 )
		{
			invoker = new HttpServiceInvoker(program);
		}

		HttpResponse outerResponse = new HttpResponse();
		if(invoker != null){
			HttpResponse innerResponse = null;
			try
			{
				innerResponse = invoker.invoke(ruiRequest, innerRequest);
			}
			catch(ServiceBindingException sbe )
			{
				outerResponse.setStatus(HttpUtilities.HTTP_STATUS_FAILED);
				outerResponse.setStatusMessage(HttpUtilities.HTTP_STATUS_MSG_FAILED);
				ServletUtilities.setBody(program()._runUnit(), outerResponse, sbe);
			}
			catch(ServiceInvocationException sie)
			{
				innerResponse.setStatus(HttpUtilities.HTTP_STATUS_FAILED);
				innerResponse.setStatusMessage(HttpUtilities.HTTP_STATUS_MSG_FAILED);
				ServletUtilities.setBody(program()._runUnit(), innerResponse, sie);
			}
			catch(Throwable t)
			{
				ServletUtilities.setBody(program()._runUnit(), innerResponse, ServiceUtilities.buildServiceInvocationException( program()._runUnit(), Message.SOA_E_WS_PROXY_UNIDENTIFIED, new Object[0], t, invoker.getServiceKind(innerRequest) ));
				innerResponse.setStatus(HttpUtilities.HTTP_STATUS_FAILED);
				innerResponse.setStatusMessage(HttpUtilities.HTTP_STATUS_MSG_FAILED);
			}
			finally
			{
				outerResponse.setStatus(HttpUtilities.HTTP_STATUS_OK);
				outerResponse.setStatusMessage(HttpUtilities.HTTP_STATUS_MSG_OK);
				ServletUtilities.setBody(program(), outerResponse, innerResponse);
			}
		}
		if(trace()){
			tracer.put( new StringBuilder(" Response Status:")
						.append(String.valueOf( outerResponse.getStatus() ))
						.append(" status msg:")
						.append(outerResponse.getStatusMessage())
						.append(" header:")
						.append(ProxyUtilities.convert(outerResponse.getHeaders()))
						.append(" body:")
						.append(outerResponse.getBody()).toString() );
		}
		return outerResponse;
	}
	
	private Trace tracer()
	{
		if( tracer == null )
		{
			tracer = program()._runUnit().getTrace();
		}
		return tracer;
	}
	
	private boolean trace()
    {
		return tracer().traceIsOn( Trace.GENERAL_TRACE ); 
    }

}
