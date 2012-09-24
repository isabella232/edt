package org.eclipse.edt.jtopen.connection.persistent;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import eglx.jtopen.IBMiConnection;
import eglx.lang.SysLib;
import org.eclipse.edt.jtopen.connection.persistent.SessionConnectionPool;
import org.eclipse.edt.runtime.java.eglx.lang.EBoolean;
import java.lang.Boolean;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.eglx.lang.EDictionary;
import org.eclipse.edt.jtopen.connection.persistent.SessionConnections;
import com.ibm.as400.access.AS400;
import eglx.jtopen.JTOpenConnection;
import eglx.lang.AnyException;
import eglx.http.ServletContext;
import javax.servlet.http.HttpSession;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import java.lang.Object;
import javax.servlet.http.HttpServletRequest;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="SessionConnectionsLib")
public class SessionConnectionsLib extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	public boolean trace;
	public SessionConnectionsLib() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
		trace = true;
	}
	public boolean getTrace() {
		return trace;
	}
	public void setTrace(boolean ezeValue) {
		trace = ezeValue;
	}
	public IBMiConnection getSessionConnection(String system, String userid, String password) {
		String key;
		key = createKey(system, userid);
		eglx.lang.EDictionary connections;
		connections = getConnections(getSessionId());
		IBMiConnection connection = null;
		if (!(connections.containsKey(key))) {
			try {
				AS400 as400;
				as400 = new AS400(system, userid, password);
				as400.setGuiAvailable(false);
				as400.validateSignon();
				connection = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(new JTOpenConnection(as400), IBMiConnection.class);
			}
			catch ( java.lang.Exception eze$Temp2 ) {
				org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp2 );
				AnyException exception;
				if ( eze$Temp2 instanceof AnyException ) {
					exception = (AnyException)eze$Temp2;
				}
				else {
					exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp2);
				}
				{
					throw exception;
				}
			}
		}
		else {
			connection = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeGet(connections, key), IBMiConnection.class);
			connections.removeElement(key);
		}
		return connection;
	}
	public void returnConnectionToPool(IBMiConnection conn) {
		eglx.lang.EDictionary connections;
		connections = getConnections(getSessionId());
		org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeSet(connections, createKey(org.eclipse.edt.javart.util.JavartUtil.checkNullable(conn.getAS400().getSystemName()), org.eclipse.edt.javart.util.JavartUtil.checkNullable(conn.getAS400().getUserId())), EAny.asAny(conn));
	}
	private eglx.lang.EDictionary getConnections(String sessionId) {
		eglx.lang.EDictionary sessionConnections = null;
		if (!(getSessionConnections().sessionConnections.containsKey(sessionId))) {
			sessionConnections = new EDictionary();
			org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeSet(getSessionConnections().sessionConnections, sessionId, EAny.asAny(sessionConnections));
		}
		else {
			sessionConnections = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeGet(getSessionConnections().sessionConnections, sessionId), EDictionary.class);
		}
		return org.eclipse.edt.javart.util.JavartUtil.checkNullable(sessionConnections);
	}
	public eglx.lang.EDictionary getConnections() {
		return getSessionConnections().sessionConnections;
	}
	private SessionConnections getSessionConnections() {
		SessionConnections sessionConnections;
		sessionConnections = SessionConnectionPool.getSessionConnectionPool();
		return org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(sessionConnections, SessionConnections.class);
	}
	private String getSessionId() {
		String sessionid = "";
		HttpServletRequest request;
		request = ServletContext.getHttpServletRequest();
		if ((request != null)) {
			HttpSession session;
			session =  (HttpSession) org.eclipse.edt.javart.util.JavartUtil.checkNullable(request.getSession());
			if ((session != null)) {
				sessionid =  (String) org.eclipse.edt.javart.util.JavartUtil.checkNullable(session.getId());
			}
		}
		if (trace) {
			SysLib.writeStdout((("SessionConnectionsLib.getSessionId:") + sessionid));
		}
		return sessionid;
	}
	public HttpSession getSession() {
		HttpSession session = null;
		HttpServletRequest request;
		request = ServletContext.getHttpServletRequest();
		if ((request != null)) {
			session = request.getSession();
		}
		return session;
	}
	private String createKey(String system, String userid) {
		String key;
		key = ((((userid) + "@")) + system);
		if (trace) {
			SysLib.writeStdout((("SessionConnectionsLib.createKey:") + key));
		}
		return key;
	}
}
