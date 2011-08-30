package eglx.persistence.sql;

import java.lang.reflect.Field;
import java.sql.ResultSet;

import org.eclipse.edt.javart.RunUnit;

public class SQLResultSet {
	private ResultSet resultSet;
	private String[] updateFieldNames;
	
	public SQLResultSet(ResultSet rs, RunUnit ru) {
		this.resultSet = rs;
	}
	
	public ResultSet getResultSet() {
		return resultSet;
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
			throw new SQLException(e);
		}
	}
	
	public boolean setRelative(int rows) throws SQLException {	
		try {
			return resultSet.relative(rows);
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}	
	}

	public boolean setNext() throws SQLException {	
		try {
			return resultSet.next();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}	
	}
	public boolean setPrevious() throws SQLException {	
		try {
			return resultSet.previous();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}	
	}
	public boolean setFirst() throws SQLException {	
		try {
			return resultSet.first();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}	
	}
	public boolean setLast() throws SQLException {	
		try {
			return resultSet.last();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}	
	}

	public boolean isLast() throws SQLException {	
		try {
			return resultSet.isLast();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}	
	}

	public boolean isFirst() throws SQLException {	
		try {
			return resultSet.isFirst();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}	
	}

	public void deleteRow() throws SQLException {
		try {
			resultSet.deleteRow();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}
	}
	
	public void refreshRow() throws SQLException {
		try {
			resultSet.refreshRow();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}
	}

	public void updateRow() throws SQLException {
		try {
			resultSet.updateRow();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}
	}
	
	public void cancelRowUpdates() throws SQLException {
		try {
			resultSet.cancelRowUpdates();
		} catch (java.sql.SQLException e) {
			throw new SQLException(e);
		}
	}


}
