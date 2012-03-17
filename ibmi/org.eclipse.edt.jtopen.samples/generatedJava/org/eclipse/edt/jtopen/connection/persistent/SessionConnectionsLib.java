package org.eclipse.edt.jtopen.connection.persistent;
import org.eclipse.edt.javart.resources.*;
import org.eclipse.edt.javart.*;
import org.eclipse.edt.runtime.java.eglx.lang.EDictionary;
import org.eclipse.edt.runtime.java.eglx.lang.EString;
import java.lang.String;
import org.eclipse.edt.runtime.java.eglx.lang.EAny;
import java.lang.Object;
import eglx.jtopen.IBMiConnection;
import org.eclipse.edt.runtime.java.eglx.lang.EBoolean;
import java.lang.Boolean;
import javax.servlet.http.HttpSession;
import eglx.lang.AnyException;
import eglx.http.ServletContext;
import eglx.jtopen.JTOpenConnection;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.edt.jtopen.connection.persistent.SessionConnectionPool;
import eglx.lang.SysLib;
import org.eclipse.edt.jtopen.connection.persistent.SessionConnections;
import com.ibm.as400.access.AS400;
@SuppressWarnings("unused")
@javax.xml.bind.annotation.XmlRootElement(name="SessionConnectionsLib")
public class SessionConnectionsLib extends ExecutableBase {
	private static final long serialVersionUID = 10L;
	@javax.xml.bind.annotation.XmlTransient
	public boolean trace;
	public SessionConnectionsLib() {
		super();
		ezeInitialize();
	}
	public void ezeInitialize() {
		trace = true;
	}
	@org.eclipse.edt.javart.json.Json(name="trace", clazz=EBoolean.class, asOptions={})
	public boolean getTrace() {
		return trace;
	}
	public void setTrace(Boolean ezeValue) {
		trace = ezeValue;
	}
	public IBMiConnection getSessionConnection(String system, String userid, String password) {
		String key;
		key = createKey(system, userid);
		eglx.lang.EDictionary connections;
		connections = getConnections(getSessionId());
		IBMiConnection connection = null;
		boolean eze$Temp1;
		eze$Temp1 = !(connections.containsKey(key));
		if (eze$Temp1) {
			try {
				AS400 as400;
				as400 = new AS400(system, userid, password);
				as400.setGuiAvailable(false);
				as400.validateSignon();
				connection = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(new JTOpenConnection(as400), IBMiConnection.class);
			}
			catch ( java.lang.Exception eze$Temp3 )
			{
				org.eclipse.edt.javart.util.JavartUtil.checkHandleable( eze$Temp3 );
				AnyException exception;
				if ( eze$Temp3 instanceof AnyException )
				{
					exception = (AnyException)eze$Temp3;
				}
				else
				{
					exception = org.eclipse.edt.javart.util.JavartUtil.makeEglException(eze$Temp3);
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
		org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeSet(connections, createKey(conn.getAS400().getSystemName(), conn.getAS400().getUserId()), EAny.asAny(conn));
	}
	private eglx.lang.EDictionary getConnections(String sessionId) {
		eglx.lang.EDictionary sessionConnections = null;
		boolean eze$Temp4;
		eze$Temp4 = !(getSessionConnections().sessionConnections.containsKey(sessionId));
		if (eze$Temp4) {
			sessionConnections = new EDictionary();
			org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeSet(getSessionConnections().sessionConnections, sessionId, EAny.asAny(sessionConnections));
		}
		else {
			sessionConnections = org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeCast(org.eclipse.edt.runtime.java.eglx.lang.EAny.ezeGet(getSessionConnections().sessionConnections, sessionId), EDictionary.class);
		}
		return org.eclipse.edt.javart.util.JavartUtil.checkNullable(sessionConnections);
	}
	public eglx.lang.EDictionary getConnections() {
		eglx.lang.EDictionary eze$Temp6;
		eze$Temp6 = getSessionConnections().sessionConnections;
		return eze$Temp6;
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
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(request, null))) {
			HttpSession session;
			session = request.getSession();
			if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(session, null))) {
				sessionid = session.getId();
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
		if ((org.eclipse.edt.runtime.java.eglx.lang.NullType.notEquals(request, null))) {
			session = request.getSession();
		}
		return org.eclipse.edt.javart.util.JavartUtil.checkNullable(session);
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
