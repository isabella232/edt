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
