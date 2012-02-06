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
package org.eclipse.edt.javart.resources.egldd;

public class SQLDatabaseBinding extends Binding {

	public static final String ATTRIBUTE_BINDING_SQL_dbms = "dbms";
	public static final String ATTRIBUTE_BINDING_SQL_sqlJDBCDriverClass = "sqlJDBCDriverClass";
	public static final String ATTRIBUTE_BINDING_SQL_sqlDB = "sqlDB";
	public static final String ATTRIBUTE_BINDING_SQL_sqlID = "sqlID";
	public static final String ATTRIBUTE_BINDING_SQL_sqlPassword = "sqlPassword";
	public static final String ATTRIBUTE_BINDING_SQL_sqlSchema = "sqlSchema";
	public static final String ATTRIBUTE_BINDING_SQL_sqlValidationConnectionURL = "sqlValidationConnectionURL";
	public static final String ATTRIBUTE_BINDING_SQL_jarList = "jarList";
	public static final String ATTRIBUTE_BINDING_SQL_deployAsJndi = "deployAsJndi";
	public static final String ATTRIBUTE_BINDING_SQL_jndiApplicationAuth = "jndiApplicationAuth";
	public static final String ATTRIBUTE_BINDING_SQL_jndiName = "jndiName";

	public SQLDatabaseBinding (Binding binding)
    {
    	super(binding);
    }
	public int getBindingType()
	{
		return SQLDATABASEBINDING;
	}
	
	public String getDbms() {
		return ParameterUtil.getStringValue(getParameter(ATTRIBUTE_BINDING_SQL_dbms), "");
	}

	public String getSqlDB() {
		return ParameterUtil.getStringValue(getParameter(ATTRIBUTE_BINDING_SQL_sqlDB), "");
	}

	public String getSqlID() {
		return ParameterUtil.getStringValue(getParameter(ATTRIBUTE_BINDING_SQL_sqlID), "");
	}

	public String getSqlJDBCDriverClass() {
		return ParameterUtil.getStringValue(getParameter(ATTRIBUTE_BINDING_SQL_sqlJDBCDriverClass), "");
	}

	public String getSqlPassword() {
		return ParameterUtil.getStringValue(getParameter(ATTRIBUTE_BINDING_SQL_sqlPassword), "");
	}

	public String getSqlSchema() {
		return ParameterUtil.getStringValue(getParameter(ATTRIBUTE_BINDING_SQL_sqlSchema), "");
	}

	public String getSqlValidationConnectionURL() {
		return ParameterUtil.getStringValue(getParameter(ATTRIBUTE_BINDING_SQL_sqlValidationConnectionURL), "");
	}

	public String getJarList() {
		return ParameterUtil.getStringValue(getParameter(ATTRIBUTE_BINDING_SQL_jarList), "");
	}
	
	public boolean isDeployAsJndi() {
		return ParameterUtil.getBooleanValue(getParameter(ATTRIBUTE_BINDING_SQL_deployAsJndi), false);
	}
	
	public boolean isApplicationAuthentication() {
		return ParameterUtil.getBooleanValue(getParameter(ATTRIBUTE_BINDING_SQL_jndiApplicationAuth), false);
	}
	
	public String getJndiName() {
		return ParameterUtil.getStringValue(getParameter(ATTRIBUTE_BINDING_SQL_jndiName), "");
	}
}
