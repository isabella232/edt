package eglx.jtopen;

import com.ibm.as400.access.AS400;

public class JTOpenConnection extends IBMiConnection {

	private AS400 as400;
	private String library;
	public JTOpenConnection(AS400 as400) {
		this.as400 = as400;
	}
	public JTOpenConnection(AS400 as400, String library) {
		this(as400);
		this.library = library;
	}
	@Override
	public AS400 getAS400() {
		return as400;
	}
	public String getLibrary() {
		return library;
	}
}
