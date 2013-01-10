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
