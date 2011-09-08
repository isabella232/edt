/*******************************************************************************
 * Copyright © 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.edt.ide.deployment.core.model;

public class SQLDatabaseBinding extends Binding {

	protected String dbms;
	protected String sqlDB;
	protected String sqlID;
	protected String sqlJDBCDriverClass;
	protected String sqlJNDIName;
	protected String sqlPassword;
	protected String sqlSchema;
	protected String sqlValidationConnectionURL;
	protected String jarList;
	
	public SQLDatabaseBinding(String name,
			String dbms,
			String sqlDB,
			String sqlID,
			String sqlJDBCDriverClass,
			String sqlJNDIName,
			String sqlPassword,
			String sqlSchema,
			String sqlValidationConnectionURL,
			String jarList
			) {
		super(name);
		this.name = name;
		this.dbms = dbms;
		this.sqlDB = sqlDB;
		this.sqlID = sqlID;
		this.sqlJDBCDriverClass = sqlJDBCDriverClass;
		this.sqlJNDIName = sqlJNDIName;
		this.sqlPassword = sqlPassword;
		this.sqlSchema = sqlSchema;
		this.sqlValidationConnectionURL = sqlValidationConnectionURL;
		this.jarList = jarList;
	}
	
	public int getBindingType()
	{
		return SQLDATABASEBINDING;
	}
	
	public String toBindXML(String indent){
		StringBuffer buf = new StringBuffer();
		buf.append(indent + "<sqlDatabaseBinding"); 
		if (name != null)
		{
			buf.append(" name=\"" + name + "\"");
		}
		if (sqlJDBCDriverClass != null)
		{
			buf.append(" sqlJDBCDriverClass=\"" + sqlJDBCDriverClass + "\"");
		}
		if (sqlDB != null)
		{
			buf.append(" sqlDB=\"" + sqlDB + "\"");
		}
		if (sqlID != null)
		{
			buf.append(" sqlID=\"" + sqlID + "\"");
		}
		if (sqlPassword != null)
		{
			buf.append(" sqlPassword=\"" + sqlPassword + "\"");
		}
		if (sqlJNDIName != null)
		{
			buf.append(" sqlJNDIName=\"" + sqlJNDIName + "\"");
		}
		if (sqlSchema != null)
		{
			buf.append(" sqlSchema=\"" + sqlSchema + "\"");
		}
		if (sqlValidationConnectionURL != null)
		{
			buf.append(" sqlValidationConnectionURL=\"" + sqlValidationConnectionURL + "\"");
		}
		if (jarList != null)
		{
			buf.append(" jarList=\"" + jarList + "\"");
		}
		buf.append("/>\n");

		return buf.toString();
	}

	public String getDbms() {
		return dbms;
	}

	public void setDbms(String dbms) {
		this.dbms = dbms;
	}

	public String getSqlDB() {
		return sqlDB;
	}

	public void setSqlDB(String sqlDB) {
		this.sqlDB = sqlDB;
	}

	public String getSqlID() {
		return sqlID;
	}

	public void setSqlID(String sqlID) {
		this.sqlID = sqlID;
	}

	public String getSqlJDBCDriverClass() {
		return sqlJDBCDriverClass;
	}

	public void setSqlJDBCDriverClass(String sqlJDBCDriverClass) {
		this.sqlJDBCDriverClass = sqlJDBCDriverClass;
	}

	public String getSqlJNDIName() {
		return sqlJNDIName;
	}

	public void setSqlJNDIName(String sqlJNDIName) {
		this.sqlJNDIName = sqlJNDIName;
	}

	public String getSqlPassword() {
		return sqlPassword;
	}

	public void setSqlPassword(String sqlPassword) {
		this.sqlPassword = sqlPassword;
	}

	public String getSqlSchema() {
		return sqlSchema;
	}

	public void setSqlSchema(String sqlSchema) {
		this.sqlSchema = sqlSchema;
	}

	public String getSqlValidationConnectionURL() {
		return sqlValidationConnectionURL;
	}

	public void setSqlValidationConnectionURL(String sqlValidationConnectionURL) {
		this.sqlValidationConnectionURL = sqlValidationConnectionURL;
	}

	public String getJarList() {
		return jarList;
	}

	public void setJarList(String jarList) {
		this.jarList = jarList;
	}

	

}
