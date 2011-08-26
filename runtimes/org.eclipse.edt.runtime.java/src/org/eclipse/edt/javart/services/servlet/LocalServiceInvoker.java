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
package org.eclipse.edt.javart.services.servlet;

import java.lang.reflect.Method;

import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.services.ServiceBase;

import egl.lang.AnyException;
import eglx.services.ServiceKind;
import eglx.services.ServiceUtilities;

public abstract class LocalServiceInvoker extends Invoker{
	private long elapseTime;

	private String serviceClassName;
	private ServiceKind serviceKind;
	private ServiceBase service;
	private Class<ServiceBase> serviceClass;
	public LocalServiceInvoker(String serviceClassName, ServiceKind serviceKind) {
		this.serviceClassName = serviceClassName;
		this.serviceKind = serviceKind;
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

	protected Method getMethod(String functionName)throws AnyException{
		for(Method method: getServiceClass().getMethods()){
			if(method.getName().equalsIgnoreCase(functionName)){
				return method;
			}
		}
		throw ServiceUtilities.buildServiceInvocationException(getRunUnit(), Message.SOA_E_FUNCTION_NOT_FOUND, new String[]{functionName, this.getClass().getName()}, null, ServiceKind.EGL);
	}
	protected Class<ServiceBase> getServiceClass() throws AnyException
	{
		if(serviceClass == null){
			try {
				serviceClass = (Class<ServiceBase>)Class.forName( serviceClassName, true, getRunUnit().getClass().getClassLoader() );
			} 
			catch(Exception e)
			{
				ServiceUtilities.buildServiceInvocationException( 
						getRunUnit(),
						Message.SOA_E_LOAD_LOCAL_SERVICE,
						new Object[] { serviceClassName }, e, serviceKind);
			}
		}
		return serviceClass;
	}
	protected ServiceBase getService() throws AnyException
	{
		if(service == null){
			// create a constructor object
			try 
			{
				service = getServiceClass().newInstance();
			}
			catch(AnyException ae){
				throw ae;
			}
			catch(Exception e){
				ServiceUtilities.buildServiceInvocationException( 
						getRunUnit(),
						Message.SOA_E_LOAD_LOCAL_SERVICE,
						new Object[] { serviceClassName }, e, serviceKind);
			}
		}
		return service;
	}
	public String getServiceClassName() {
		return serviceClassName;
	}
	public ServiceKind getServiceKind() {
		return serviceKind;
	}
}
