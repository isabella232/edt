/*******************************************************************************
 * Copyright © 2011, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.javart.services.servlet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.edt.javart.Runtime;
import org.eclipse.edt.javart.messages.Message;
import org.eclipse.edt.javart.services.ServiceBase;
import org.eclipse.edt.javart.services.ServiceUtilities;
import org.eclipse.edt.javart.util.JavaAliaser;

import eglx.lang.AnyException;
import eglx.services.ServiceKind;

public abstract class LocalServiceInvoker extends Invoker{
	private long elapseTime;

	private String serviceClassName;
	private ServiceKind serviceKind;
	private ServiceBase service;
	private Class<ServiceBase> serviceClass;
	
	private static Map<String, String> cachedAliases = new HashMap<String, String>();
	
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
		throw ServiceUtilities.buildServiceInvocationException(Message.SOA_E_FUNCTION_NOT_FOUND, new String[]{functionName, this.getClass().getName()}, null, ServiceKind.EGL);
	}
	protected Class<ServiceBase> getServiceClass() throws AnyException
	{
		if(serviceClass == null){
			try {
				String aliasedServiceClassName = cachedAliases.get(serviceClassName);
				if(aliasedServiceClassName == null){
					int idx = serviceClassName.lastIndexOf('.');
					if( idx != -1){
						StringBuilder buf = new StringBuilder(JavaAliaser.packageNameAlias(serviceClassName.substring(0, idx)));
						aliasedServiceClassName = buf.append('.').append(JavaAliaser.getAlias(serviceClassName.substring(idx + 1))).toString();
					}
					else{
						aliasedServiceClassName = JavaAliaser.getAlias(serviceClassName);
					}
					cachedAliases.put(serviceClassName, aliasedServiceClassName);
				}
				serviceClass = (Class<ServiceBase>)Class.forName( aliasedServiceClassName, true, Runtime.getRunUnit().getClass().getClassLoader() );
			} 
			catch(Exception e)
			{
				throw ServiceUtilities.buildServiceInvocationException( 
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
				throw ServiceUtilities.buildServiceInvocationException( 
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
