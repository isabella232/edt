package org.eclipse.edt.jtopen.connection.persistent;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EList;
import java.util.List;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import java.lang.Object;
import org.eclipse.edt.runtime.java.eglx.lang.EDictionary;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import eglx.jtopen.IBMiConnection;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="SessionConnections")
public class SessionConnections extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	@org.eclipse.edt.javart.json.Json(name="sessionConnections", clazz=EDictionary.class, asOptions={})
	public eglx.lang.EDictionary sessionConnections;
	
	public SessionConnections() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
		sessionConnections = new EDictionary();
	}
	public List<IBMiConnection> getConnections(String sessionID) {
		eglx.lang.EDictionary connections;
		connections = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeGet(sessionConnections, sessionID), EDictionary.class);
		sessionConnections.removeElement(sessionID);
		List<IBMiConnection> sessionConnections;
		sessionConnections = org.eclipse.edt.runtime.java.eglx.lang.EList.ezeCast(connections.getValues(), "eglx.lang.EList<eglx.jtopen.IBMiConnection>");
		return sessionConnections;
	}
}
