/*******************************************************************************
 * Copyright © 2011, 2012 IBM Corporation and others.
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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.resources.RecoverableResource;
import org.eclipse.edt.javart.util.JavartUtil;

import eglx.lang.AnyException;
import eglx.lang.EDictionary;

public class SQLDataSource implements RecoverableResource {
	
	public static final int TRANSACTION_ISOLATION_NONE = Connection.TRANSACTION_NONE;
	public static final int TRANSACTION_ISOLATION_READ_UNCOMMITTED = Connection.TRANSACTION_READ_UNCOMMITTED;
	public static final int TRANSACTION_ISOLATION_READ_COMMITTED = Connection.TRANSACTION_READ_COMMITTED;
	public static final int TRANSACTION_ISOLATION_REPEATABLE_READ = Connection.TRANSACTION_REPEATABLE_READ;
	public static final int TRANSACTION_ISOLATION_SERIALIZABLE = Connection.TRANSACTION_SERIALIZABLE;
	
	private PreparedStatement SetSchema = null;
	
	protected Connection conn;
	private Map<String, List<Statement>> statements;
	protected String connectionUrl;
	protected Properties properties;
	
	public SQLDataSource(String connectionUrl) {
		this(connectionUrl, org.eclipse.edt.javart.Runtime.getRunUnit());
	}
	
	public SQLDataSource(String connectionUrl, RunUnit ru) {
		this.connectionUrl = connectionUrl;
		this.statements = new HashMap<String, List<Statement>>();
		this.properties = new Properties();
		ru.registerResource(this);
	}
	
	public SQLDataSource(String connectionUrl, EDictionary properties) {
		this(connectionUrl);
		
		if (properties != null && properties.size() > 0) {
			this.properties.putAll(properties);
		}
	}

	public void close() throws SQLException {
		if (conn != null) {
			try {
				conn.close();
				conn = null;
			} catch (java.sql.SQLException e) {
				throw JavartUtil.makeEglException(e);
			}
		}
	}

	public Connection getConnection() throws SQLException {
		if (conn == null) {
			try {
				conn = DriverManager.getConnection(connectionUrl, properties);
			} catch (java.sql.SQLException e) {
				throw JavartUtil.makeEglException(e);
			}
		}
		return conn;
	}

	@Override
	public void commit(RunUnit ru) throws AnyException {
		try {
			if(conn != null && !conn.getAutoCommit()){
				conn.commit();
			}
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
		
	}

	@Override
	public void rollback(RunUnit ru) throws AnyException {
		try {
			if(conn != null && !conn.getAutoCommit()){
				conn.rollback();
			}
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
		
	}

	@Override
	public void exit(RunUnit ru) throws AnyException {
		try {
			for (Entry<String, List<Statement>> entry : statements.entrySet()) {
				for (Statement s : entry.getValue()){
					if(s != null){
						s.close();
					}
				}
			}
			close();
			statements.clear();
			ru.getResourceManager().getResourceList().remove(this);
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
		
	}

	@Override
	public void transferCleanup(RunUnit ru, boolean toTransaction)
			throws AnyException {
		if (toTransaction) {
			try {
				for (Entry<String, List<Statement>> entry : statements.entrySet()) {
					for (Statement s : entry.getValue()){
						if(s != null){
							s.close();
						}
					}
				}
				close();
				statements.clear();
				ru.getResourceManager().getResourceList().remove(this);
			} catch (java.sql.SQLException e) {
				throw JavartUtil.makeEglException(e);
			}
		}
		
	}
	
	public void registerStatement(String typeSignature, int index, Statement stmt) {
		List<Statement> stmts = statements.get(typeSignature);
		if (stmts == null) {
			stmts = new ArrayList<Statement>();
			statements.put(typeSignature, stmts);
		}
		if (index >= stmts.size()) {
			int z = index - stmts.size();
			for (int i=0; i<=z; i++) {
				stmts.add(null);
			}
		}
		stmts.set(index, stmt);
	}
	
	public Statement getStatement(String typeSignature, Integer index) {
		List<Statement> stmts = statements.get(typeSignature);
		return stmts == null 
			? null 
			: index < stmts.size() 
				? stmts.get(index) : null;
	}
	
	public void setCurrentSchema(String schema) throws SQLException {
		try {
			if (SetSchema == null) {
				SetSchema = getConnection().prepareStatement("set schema ? ");
			}
			SetSchema.setString(1, schema);
			SetSchema.executeUpdate();
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
	}
	
	public boolean getAutoCommit() throws SQLException {
		try {
			return getConnection().getAutoCommit();
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
	}
	
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		try {
			getConnection().setAutoCommit( autoCommit );
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
	}
	
	public int getTransactionIsolation() throws SQLException {
		try {
			return getConnection().getTransactionIsolation();
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
	}
	
	public void setTransactionIsolation(int level) throws SQLException {
		try {
			getConnection().setTransactionIsolation(level);
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
	}
	
	public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
		try {
			return getConnection().getMetaData().supportsTransactionIsolationLevel(level);
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
	}
	
	public boolean isClosed() throws SQLException {
		try {
			if(conn != null){
				return getConnection().isClosed();
			}
			else{
				return true;
			}
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
	}
	
	public boolean isReadOnly() throws SQLException {
		try {
			return getConnection().isReadOnly();
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
	}
	
	public boolean isValid(int timeout) throws SQLException {
		try {
			return getConnection().isValid(timeout);
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
	}
	
	public SQLWarning getWarnings() throws SQLException {
		if (conn == null) {
			return null;
		}
		try {
			java.sql.SQLWarning warning = conn.getWarnings();
			if (warning == null) {
				return null;
			}
			return (SQLWarning)JavartUtil.makeEglException(warning);
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
	}
}
