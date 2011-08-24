package org.eclipse.edt.runtime.java.eglx.persistence.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.edt.javart.JavartException;
import org.eclipse.edt.javart.RunUnit;
import org.eclipse.edt.javart.resources.RecoverableResource;

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

	public Connection getConnection() throws SQLException {
		if (conn == null) {
			try {
				conn = DriverManager.getConnection(connectionUrl);
			} catch (java.sql.SQLException e) {
				throw new SQLException(e);
			}
		}
		return conn;
	}

	@Override
	public void commit(RunUnit ru) throws JavartException {
		try {
			conn.commit();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}
		
	}

	@Override
	public void rollback(RunUnit ru) throws JavartException {
		try {
			conn.rollback();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}
		
	}

	@Override
	public void exit(RunUnit ru) throws JavartException {
		try {
			for (Entry<String, List<Statement>> entry : statements.entrySet()) {
				for (Statement s : entry.getValue())
				s.close();
			}
			conn.close();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}
		
	}

	@Override
	public void transferCleanup(RunUnit ru, boolean toTransaction)
			throws JavartException {
		if (toTransaction) {
			try {
				for (Entry<String, List<Statement>> entry : statements.entrySet()) {
					for (Statement s : entry.getValue())
					s.close();
				}
				conn.close();
			} catch (java.sql.SQLException e) {
				throw new SQLException(e);
			}
		}
		
	}
	
	public void registerStatement(String typeSignature, Statement stmt) {
		List<Statement> stmts = statements.get(typeSignature);
		if (stmts == null) {
			stmts = new ArrayList<Statement>();
			statements.put(typeSignature, stmts);
		}
		stmts.add(stmt);
	}
	
	public Statement getStatement(String typeSignature, Integer index) {
		List<Statement> stmts = statements.get(typeSignature);
		return stmts == null 
			? null 
			: index < stmts.size() 
				? stmts.get(index) : null;
	}
	
	public void setCurrentSchema(String schema) throws JavartException {
		try {
			if (SetSchema == null) {
				SetSchema = getConnection().prepareStatement("set schema ? ");
			}
			SetSchema.setString(1, schema);
			SetSchema.executeUpdate();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}
	}
}
