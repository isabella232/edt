/*******************************************************************************
 * Copyright © 2013 IBM Corporation and others.
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

import java.util.HashMap;
import java.util.List;


import eglx.jtopen.IBMiConnection;

public class SessionConnectionPool extends HashMap<String,List<IBMiConnection>>{
	
	private static final long serialVersionUID = 1L;
	private static SessionConnections connectionPool;

	public static SessionConnections getSessionConnectionPool(){
		if(connectionPool == null){
			connectionPool = new SessionConnections();
		}
		return connectionPool;
	}

}
