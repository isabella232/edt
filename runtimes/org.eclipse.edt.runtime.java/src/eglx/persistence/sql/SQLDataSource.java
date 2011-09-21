/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
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

import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.resources.RecoverableResource;
import org.eclipse.edt.javart.util.JavartUtil;

import egl.lang.AnyException;

public class SQLDataSource implements RecoverableResource {
	private PreparedStatement SetSchema = null;
	
	private Connection conn;
	private Map<String, List<Statement>> statements;
	private String connectionUrl;
	
	public SQLDataSource(String connectionUrl, RunUnit ru) {
		this.connectionUrl = connectionUrl;
		ru.registerResource(this);
		statements = new HashMap<String, List<Statement>>();
	}
	
	public SQLDataSource(String connectionUrl) {
		this.connectionUrl = connectionUrl;
		org.eclipse.edt.javart.Runtime.getRunUnit().registerResource(this);
		statements = new HashMap<String, List<Statement>>();
	}


	public Connection getConnection() throws SQLException {
		if (conn == null) {
			try {
				conn = DriverManager.getConnection(connectionUrl);
			} catch (java.sql.SQLException e) {
				throw JavartUtil.makeEglException(e);
			}
		}
		return conn;
	}

	@Override
	public void commit(RunUnit ru) throws AnyException {
		try {
			if(conn != null){
				conn.commit();
			}
		} catch (java.sql.SQLException e) {
			throw JavartUtil.makeEglException(e);
		}
		
	}

	@Override
	public void rollback(RunUnit ru) throws AnyException {
		try {
			if(conn != null){
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
				for (Statement s : entry.getValue())
				s.close();
			}
			if(conn != null){
				conn.close();
			}
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
					for (Statement s : entry.getValue())
					s.close();
				}
				if(conn != null){
					conn.close();
				}
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
}
