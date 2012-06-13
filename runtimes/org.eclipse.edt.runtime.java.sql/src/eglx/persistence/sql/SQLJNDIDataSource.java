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
package eglx.persistence.sql;

import java.sql.Connection;

import javax.sql.CommonDataSource;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.Runtime;
import org.eclipse.edt.javart.messages.Message;

import eglx.lang.EDictionary;

public class SQLJNDIDataSource extends SQLDataSource {
	
	public SQLJNDIDataSource(String connectionUrl) {
		super(connectionUrl);
	}
	
	public SQLJNDIDataSource(String connectionUrl, RunUnit ru) {
		super(connectionUrl, ru);
	}
	
	public SQLJNDIDataSource(String connectionUrl, EDictionary properties) {
		super(connectionUrl, properties);
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (conn == null) {
			String jndiName = connectionUrl;
			
			// Initial attempt is for an indirect lookup.
			if (!jndiName.startsWith("java:comp/env/")) {
				jndiName = "java:comp/env/" + jndiName;
			}
			
			CommonDataSource ds;
			try {
				ds = (CommonDataSource)Runtime.getRunUnit().jndiLookup(jndiName);
			} catch (Exception ex) {
				// Try a direct lookup.
				try {
					ds = (CommonDataSource)Runtime.getRunUnit().jndiLookup(connectionUrl);
				}
				catch (Exception ex2) {
					// Throw an error based on the indirect lookup's exception.
					SQLException sqlEx = new SQLException();
					throw sqlEx.fillInMessage(Message.JNDI_LOOKUP_ERROR, jndiName, ex);
				}
			}
			
			String user = properties.getProperty("user");
			String password = properties.getProperty("password");
			try {
				if ((user == null || user.length() == 0) && (password == null || password.length() == 0)) {
					if (ds instanceof DataSource) {
						conn = ((DataSource)ds).getConnection();
					}
					else if (ds instanceof ConnectionPoolDataSource) {
						conn = ((ConnectionPoolDataSource)ds).getPooledConnection().getConnection();
					}
					else if (ds instanceof XADataSource) {
						conn = ((XADataSource)ds).getXAConnection().getConnection();
					}
					else {
						SQLException sqlEx = new SQLException();
						throw sqlEx.fillInMessage(Message.JNDI_UNKNOWN_TYPE, ds == null ? "null" : ds.getClass().getCanonicalName());
					}
				}
				else
				{
					if (ds instanceof DataSource) {
						conn = ((DataSource)ds).getConnection(user, password);
					}
					else if (ds instanceof ConnectionPoolDataSource) {
						conn = ((ConnectionPoolDataSource)ds).getPooledConnection(user, password).getConnection();
					}
					else if (ds instanceof XADataSource) {
						conn = ((XADataSource)ds).getXAConnection(user, password).getConnection();
					}
					else {
						SQLException sqlEx = new SQLException();
						throw sqlEx.fillInMessage(Message.JNDI_UNKNOWN_TYPE, ds == null ? "null" : ds.getClass().getCanonicalName());
					}
				}
			} catch (java.sql.SQLException e) {
				throw SQLUtilities.makeEglException(e);
			}
		}
		return conn;
	}
}
