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
package org.eclipse.edt.ide.deployment.services.internal.testserver;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * Very basic data source that instantiates a new driver on each call. No connection pooling is used.
 */
public class SimpleDataSource implements DataSource {
	
	private boolean initDriver;
	private String driverClassName;
	private String url;
	private String user;
	private String password;
	
	public void setDriverClassName(String driver) {
		boolean changed;
		if (driver == null) {
			changed = this.driverClassName != null;
		}
		else {
			changed = !driver.equals(this.driverClassName);
		}
		
		if (changed) {
			this.driverClassName = driver;
			this.initDriver = true;
		}
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setUsername(String user) {
		this.user = user;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public int getLoginTimeout() throws SQLException {
		return DriverManager.getLoginTimeout();
	}
	
	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return DriverManager.getLogWriter();
	}
	
	@Override
	public void setLoginTimeout( int seconds ) throws SQLException {
		DriverManager.setLoginTimeout(seconds);
	}
	
	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		DriverManager.setLogWriter(out);
	}
	
	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		try {
            return iface.cast(this);
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return iface.isInstance(this);
	}
	
	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(user, password);
	}
	
	@Override
	public Connection getConnection(String theUsername, String thePassword) throws SQLException {
		initDriver();
		return DriverManager.getConnection(url, theUsername, thePassword);
	}
	
	protected void initDriver() {
		if (initDriver) {
			initDriver = false;
			try {
				Class.forName(driverClassName);
			}
			catch (Throwable t) {
			}
		}
	}
}
