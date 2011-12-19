package eglx.jtopen;

import java.util.HashMap;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400ConnectionPool;

public class AS400EDTConnection {
	
	private static AS400ConnectionPool connectionPool;

	private static SessionConnectionPool sessionConnectionPool;
	
	private static boolean shutDownAdded = false;
	
	private static AS400EDTConnection INSTANCE = new AS400EDTConnection();
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
							if(sessionConnectionPool != null){
								try{
									sessionConnectionPool.close();
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

	public static SessionConnectionPool getSessionConnectionPool(){
		if(sessionConnectionPool == null){
			sessionConnectionPool = INSTANCE.new SessionConnectionPool();
			addShutDown();
		}
		return sessionConnectionPool;
	}

	private class SessionConnectionPool{
		private HashMap<String, AS400> sessionConnectionPool;
		
		public SessionConnectionPool() {
			sessionConnectionPool = new HashMap<String, AS400>();
		}
		public void close(){
			if(sessionConnectionPool != null){
				for(AS400 as400 : sessionConnectionPool.values()){
					try{
						as400.disconnectAllServices();
					}
					catch(Throwable t){}
				}
			}
		}
		public AS400 getConnection(String sessionId, String systemName, String UserId){
			return null;
		}
		public void returnConnectionToPool(AS400 connection){
			
		}
		public void closeConnection(AS400 connection){
			
		}
	}

}
