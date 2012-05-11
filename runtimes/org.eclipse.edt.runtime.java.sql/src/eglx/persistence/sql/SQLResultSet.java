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

import java.lang.reflect.Field;
import java.sql.ResultSet;

public class SQLResultSet {
	private ResultSet resultSet;
	private String[] updateFieldNames;
	
	public static final int CONCUR_READ_ONLY = ResultSet.CONCUR_READ_ONLY;
	public static final int CONCUR_UPDATABLE = ResultSet.CONCUR_UPDATABLE;
	public static final int HOLD_CURSORS_OVER_COMMIT = ResultSet.HOLD_CURSORS_OVER_COMMIT;
	public static final int CLOSE_CURSORS_AT_COMMIT = ResultSet.CLOSE_CURSORS_AT_COMMIT;
	public static final int TYPE_FORWARD_ONLY = ResultSet.TYPE_FORWARD_ONLY;
	public static final int TYPE_SCROLL_INSENSITIVE = ResultSet.TYPE_SCROLL_INSENSITIVE;
	public static final int TYPE_SCROLL_SENSITIVE = ResultSet.TYPE_SCROLL_SENSITIVE;
	
	public SQLResultSet(ResultSet rs) {
		this.resultSet = rs;
	}
	
	public ResultSet getResultSet() {
		return resultSet;
	}

	public void close() throws SQLException {
		if (resultSet != null) {
			try {
				resultSet.close();
				resultSet = null;
			} catch (java.sql.SQLException e) {
				throw SQLUtilities.makeEglException(e);
			}
		}
	}

	public void setUpdateFieldNames(String[] updateFieldNames) {
		this.updateFieldNames = updateFieldNames;
	}

	public void updateUsing(Object...objs) throws java.sql.SQLException {
		int i = 1;
		for (Object obj : objs) {
			resultSet.updateObject(i, obj);
			i++;
		}
	}
	
	public void updateColumnsUsing(Object obj) throws java.sql.SQLException {
		int i = 1;
		for (String fName : updateFieldNames) {
			try {
				Field f = obj.getClass().getField(fName);
				Object value = f.get(obj);
				resultSet.updateObject(i, value);
			} catch (SecurityException e) {
				// Ignore
			} catch (NoSuchFieldException e) {
				// Ignore
			} catch (IllegalArgumentException e) {
				// Ignore
			} catch (IllegalAccessException e) {
				// Ignore
			}
		}
	}
	
	public boolean setAbsolute(int row) throws SQLException {
		
		try {
			return resultSet.absolute(row);
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}
	}
	
	public boolean setRelative(int rows) throws SQLException {	
		try {
			return resultSet.relative(rows);
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}	
	}

	public boolean setNext() throws SQLException {	
		try {
			return resultSet.next();
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}	
	}
	public boolean setPrevious() throws SQLException {	
		try {
			return resultSet.previous();
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}	
	}
	public boolean setFirst() throws SQLException {	
		try {
			return resultSet.first();
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}	
	}
	public boolean setLast() throws SQLException {	
		try {
			return resultSet.last();
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}	
	}

	public boolean isLast() throws SQLException {	
		try {
			return resultSet.isLast();
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}	
	}

	public boolean isFirst() throws SQLException {	
		try {
			return resultSet.isFirst();
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}	
	}

	public void deleteRow() throws SQLException {
		try {
			resultSet.deleteRow();
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}
	}
	
	public void refreshRow() throws SQLException {
		try {
			resultSet.refreshRow();
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}
	}

	public void updateRow() throws SQLException {
		try {
			resultSet.updateRow();
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}
	}
	
	public void cancelRowUpdates() throws SQLException {
		try {
			resultSet.cancelRowUpdates();
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}
	}

	public SQLWarning getWarnings() throws SQLException {
		try {
			java.sql.SQLWarning warning = resultSet.getWarnings();
			if (warning == null) {
				return null;
			}
			return (SQLWarning)SQLUtilities.makeEglException(warning);
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}
	}

	public int getHoldability() throws SQLException {
		try {
			return resultSet.getHoldability();
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}
	}
	
	public int getPosition() throws SQLException {
		try {
			return resultSet.getRow();
		} catch (java.sql.SQLException e) {
			throw SQLUtilities.makeEglException(e);
		}
	}
}


