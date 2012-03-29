/*
 * Licensed Materials - Property of IBM
 *
 * Copyright IBM Corporation 2006, 2010. All Rights Reserved.
 *
 * U.S. Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA DP Schedule Contract with IBM Corp.
 */
package org.eclipse.edt.jtopen.connection.persistent;

import java.util.List;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import eglx.jtopen.IBMiConnection;

public class IBMiConnectionHttpSessionListener implements HttpSessionListener
{

	public void sessionCreated( HttpSessionEvent arg0 )
	{
	}

	public void sessionDestroyed( HttpSessionEvent arg0 )
	{
		String sessionId = arg0.getSession().getId();
		List<IBMiConnection> idleConnections = SessionConnectionPool.getSessionConnectionPool().getConnections(sessionId);
		for(IBMiConnection connection : idleConnections){
			try{
				connection.getAS400().disconnectAllServices();
			}
			catch(Exception e){}
		}
	}

}
