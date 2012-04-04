/*******************************************************************************
 * Copyright © 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package eglx.jtopen;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400ConnectionPool;

public class JTOpenConnections {
	
	private static AS400ConnectionPool connectionPool;

	private static boolean shutDownAdded = false;
	
	private static void addShutDown() {
		if(!shutDownAdded){
			// Make sure all connections are closed when the JVM terminates.
			Runtime.getRuntime().addShutdownHook( 
					new Thread()
					{
						public void run()
						{
							//cleanup session connections
							if(connectionPool != null){
								try{
									connectionPool.close();
								}
								catch(Throwable t){}
								}
						}

					} );
			shutDownAdded = true;
		}
	}

	public static AS400ConnectionPool getAS400ConnectionPool(){
		if(connectionPool == null){
			connectionPool = new AS400ConnectionPool();
			addShutDown();
		}
		return connectionPool;
	}

	public static void returnConnectionToPool(IBMiConnection conn){
		if(conn instanceof JTOpenConnection){
			AS400 as400 = conn.getAS400();
			((JTOpenConnection)conn).as400 = null;
			getAS400ConnectionPool().returnConnectionToPool(as400);
		}
	}
}
