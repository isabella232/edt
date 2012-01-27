package eglx.jtopen;

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

}
