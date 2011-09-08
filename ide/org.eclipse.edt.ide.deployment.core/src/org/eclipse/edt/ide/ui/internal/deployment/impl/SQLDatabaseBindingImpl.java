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
package org.eclipse.edt.ide.ui.internal.deployment.impl;

import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;
import org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>SQL Database Binding</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl#getDbms <em>Dbms</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl#getJarList <em>Jar List</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl#getSqlDB <em>Sql DB</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl#getSqlID <em>Sql ID</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl#getSqlJDBCDriverClass <em>Sql JDBC Driver Class</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl#getSqlJNDIName <em>Sql JNDI Name</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl#getSqlPassword <em>Sql Password</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl#getSqlSchema <em>Sql Schema</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.SQLDatabaseBindingImpl#getSqlValidationConnectionURL <em>Sql Validation Connection URL</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SQLDatabaseBindingImpl extends EObjectImpl implements SQLDatabaseBinding {
	/**
	 * The default value of the '{@link #getDbms() <em>Dbms</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDbms()
	 * @generated
	 * @ordered
	 */
	protected static final String DBMS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDbms() <em>Dbms</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDbms()
	 * @generated
	 * @ordered
	 */
	protected String dbms = DBMS_EDEFAULT;

	/**
	 * The default value of the '{@link #getJarList() <em>Jar List</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getJarList()
	 * @generated
	 * @ordered
	 */
	protected static final String JAR_LIST_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getJarList() <em>Jar List</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getJarList()
	 * @generated
	 * @ordered
	 */
	protected String jarList = JAR_LIST_EDEFAULT;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getSqlDB() <em>Sql DB</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlDB()
	 * @generated
	 * @ordered
	 */
	protected static final String SQL_DB_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSqlDB() <em>Sql DB</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlDB()
	 * @generated
	 * @ordered
	 */
	protected String sqlDB = SQL_DB_EDEFAULT;

	/**
	 * The default value of the '{@link #getSqlID() <em>Sql ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlID()
	 * @generated
	 * @ordered
	 */
	protected static final String SQL_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSqlID() <em>Sql ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlID()
	 * @generated
	 * @ordered
	 */
	protected String sqlID = SQL_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getSqlJDBCDriverClass() <em>Sql JDBC Driver Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlJDBCDriverClass()
	 * @generated
	 * @ordered
	 */
	protected static final String SQL_JDBC_DRIVER_CLASS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSqlJDBCDriverClass() <em>Sql JDBC Driver Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlJDBCDriverClass()
	 * @generated
	 * @ordered
	 */
	protected String sqlJDBCDriverClass = SQL_JDBC_DRIVER_CLASS_EDEFAULT;

	/**
	 * The default value of the '{@link #getSqlJNDIName() <em>Sql JNDI Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlJNDIName()
	 * @generated
	 * @ordered
	 */
	protected static final String SQL_JNDI_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSqlJNDIName() <em>Sql JNDI Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlJNDIName()
	 * @generated
	 * @ordered
	 */
	protected String sqlJNDIName = SQL_JNDI_NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getSqlPassword() <em>Sql Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlPassword()
	 * @generated
	 * @ordered
	 */
	protected static final String SQL_PASSWORD_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSqlPassword() <em>Sql Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlPassword()
	 * @generated
	 * @ordered
	 */
	protected String sqlPassword = SQL_PASSWORD_EDEFAULT;

	/**
	 * The default value of the '{@link #getSqlSchema() <em>Sql Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlSchema()
	 * @generated
	 * @ordered
	 */
	protected static final String SQL_SCHEMA_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSqlSchema() <em>Sql Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlSchema()
	 * @generated
	 * @ordered
	 */
	protected String sqlSchema = SQL_SCHEMA_EDEFAULT;

	/**
	 * The default value of the '{@link #getSqlValidationConnectionURL() <em>Sql Validation Connection URL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlValidationConnectionURL()
	 * @generated
	 * @ordered
	 */
	protected static final String SQL_VALIDATION_CONNECTION_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSqlValidationConnectionURL() <em>Sql Validation Connection URL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSqlValidationConnectionURL()
	 * @generated
	 * @ordered
	 */
	protected String sqlValidationConnectionURL = SQL_VALIDATION_CONNECTION_URL_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SQLDatabaseBindingImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.SQL_DATABASE_BINDING;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDbms() {
		return dbms;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDbms(String newDbms) {
		String oldDbms = dbms;
		dbms = newDbms;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.SQL_DATABASE_BINDING__DBMS, oldDbms, dbms));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getJarList() {
		return jarList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setJarList(String newJarList) {
		String oldJarList = jarList;
		jarList = newJarList;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.SQL_DATABASE_BINDING__JAR_LIST, oldJarList, jarList));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.SQL_DATABASE_BINDING__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSqlDB() {
		return sqlDB;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSqlDB(String newSqlDB) {
		String oldSqlDB = sqlDB;
		sqlDB = newSqlDB;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.SQL_DATABASE_BINDING__SQL_DB, oldSqlDB, sqlDB));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSqlID() {
		return sqlID;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSqlID(String newSqlID) {
		String oldSqlID = sqlID;
		sqlID = newSqlID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.SQL_DATABASE_BINDING__SQL_ID, oldSqlID, sqlID));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSqlJDBCDriverClass() {
		return sqlJDBCDriverClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSqlJDBCDriverClass(String newSqlJDBCDriverClass) {
		String oldSqlJDBCDriverClass = sqlJDBCDriverClass;
		sqlJDBCDriverClass = newSqlJDBCDriverClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.SQL_DATABASE_BINDING__SQL_JDBC_DRIVER_CLASS, oldSqlJDBCDriverClass, sqlJDBCDriverClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSqlJNDIName() {
		return sqlJNDIName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSqlJNDIName(String newSqlJNDIName) {
		String oldSqlJNDIName = sqlJNDIName;
		sqlJNDIName = newSqlJNDIName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.SQL_DATABASE_BINDING__SQL_JNDI_NAME, oldSqlJNDIName, sqlJNDIName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSqlPassword() {
		return sqlPassword;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSqlPassword(String newSqlPassword) {
		String oldSqlPassword = sqlPassword;
		sqlPassword = newSqlPassword;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.SQL_DATABASE_BINDING__SQL_PASSWORD, oldSqlPassword, sqlPassword));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSqlSchema() {
		return sqlSchema;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSqlSchema(String newSqlSchema) {
		String oldSqlSchema = sqlSchema;
		sqlSchema = newSqlSchema;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.SQL_DATABASE_BINDING__SQL_SCHEMA, oldSqlSchema, sqlSchema));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSqlValidationConnectionURL() {
		return sqlValidationConnectionURL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSqlValidationConnectionURL(String newSqlValidationConnectionURL) {
		String oldSqlValidationConnectionURL = sqlValidationConnectionURL;
		sqlValidationConnectionURL = newSqlValidationConnectionURL;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.SQL_DATABASE_BINDING__SQL_VALIDATION_CONNECTION_URL, oldSqlValidationConnectionURL, sqlValidationConnectionURL));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DeploymentPackage.SQL_DATABASE_BINDING__DBMS:
				return getDbms();
			case DeploymentPackage.SQL_DATABASE_BINDING__JAR_LIST:
				return getJarList();
			case DeploymentPackage.SQL_DATABASE_BINDING__NAME:
				return getName();
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_DB:
				return getSqlDB();
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_ID:
				return getSqlID();
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_JDBC_DRIVER_CLASS:
				return getSqlJDBCDriverClass();
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_JNDI_NAME:
				return getSqlJNDIName();
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_PASSWORD:
				return getSqlPassword();
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_SCHEMA:
				return getSqlSchema();
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_VALIDATION_CONNECTION_URL:
				return getSqlValidationConnectionURL();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case DeploymentPackage.SQL_DATABASE_BINDING__DBMS:
				setDbms((String)newValue);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__JAR_LIST:
				setJarList((String)newValue);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__NAME:
				setName((String)newValue);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_DB:
				setSqlDB((String)newValue);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_ID:
				setSqlID((String)newValue);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_JDBC_DRIVER_CLASS:
				setSqlJDBCDriverClass((String)newValue);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_JNDI_NAME:
				setSqlJNDIName((String)newValue);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_PASSWORD:
				setSqlPassword((String)newValue);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_SCHEMA:
				setSqlSchema((String)newValue);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_VALIDATION_CONNECTION_URL:
				setSqlValidationConnectionURL((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case DeploymentPackage.SQL_DATABASE_BINDING__DBMS:
				setDbms(DBMS_EDEFAULT);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__JAR_LIST:
				setJarList(JAR_LIST_EDEFAULT);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__NAME:
				setName(NAME_EDEFAULT);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_DB:
				setSqlDB(SQL_DB_EDEFAULT);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_ID:
				setSqlID(SQL_ID_EDEFAULT);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_JDBC_DRIVER_CLASS:
				setSqlJDBCDriverClass(SQL_JDBC_DRIVER_CLASS_EDEFAULT);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_JNDI_NAME:
				setSqlJNDIName(SQL_JNDI_NAME_EDEFAULT);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_PASSWORD:
				setSqlPassword(SQL_PASSWORD_EDEFAULT);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_SCHEMA:
				setSqlSchema(SQL_SCHEMA_EDEFAULT);
				return;
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_VALIDATION_CONNECTION_URL:
				setSqlValidationConnectionURL(SQL_VALIDATION_CONNECTION_URL_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case DeploymentPackage.SQL_DATABASE_BINDING__DBMS:
				return DBMS_EDEFAULT == null ? dbms != null : !DBMS_EDEFAULT.equals(dbms);
			case DeploymentPackage.SQL_DATABASE_BINDING__JAR_LIST:
				return JAR_LIST_EDEFAULT == null ? jarList != null : !JAR_LIST_EDEFAULT.equals(jarList);
			case DeploymentPackage.SQL_DATABASE_BINDING__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_DB:
				return SQL_DB_EDEFAULT == null ? sqlDB != null : !SQL_DB_EDEFAULT.equals(sqlDB);
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_ID:
				return SQL_ID_EDEFAULT == null ? sqlID != null : !SQL_ID_EDEFAULT.equals(sqlID);
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_JDBC_DRIVER_CLASS:
				return SQL_JDBC_DRIVER_CLASS_EDEFAULT == null ? sqlJDBCDriverClass != null : !SQL_JDBC_DRIVER_CLASS_EDEFAULT.equals(sqlJDBCDriverClass);
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_JNDI_NAME:
				return SQL_JNDI_NAME_EDEFAULT == null ? sqlJNDIName != null : !SQL_JNDI_NAME_EDEFAULT.equals(sqlJNDIName);
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_PASSWORD:
				return SQL_PASSWORD_EDEFAULT == null ? sqlPassword != null : !SQL_PASSWORD_EDEFAULT.equals(sqlPassword);
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_SCHEMA:
				return SQL_SCHEMA_EDEFAULT == null ? sqlSchema != null : !SQL_SCHEMA_EDEFAULT.equals(sqlSchema);
			case DeploymentPackage.SQL_DATABASE_BINDING__SQL_VALIDATION_CONNECTION_URL:
				return SQL_VALIDATION_CONNECTION_URL_EDEFAULT == null ? sqlValidationConnectionURL != null : !SQL_VALIDATION_CONNECTION_URL_EDEFAULT.equals(sqlValidationConnectionURL);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (dbms: ");
		result.append(dbms);
		result.append(", jarList: ");
		result.append(jarList);
		result.append(", name: ");
		result.append(name);
		result.append(", sqlDB: ");
		result.append(sqlDB);
		result.append(", sqlID: ");
		result.append(sqlID);
		result.append(", sqlJDBCDriverClass: ");
		result.append(sqlJDBCDriverClass);
		result.append(", sqlJNDIName: ");
		result.append(sqlJNDIName);
		result.append(", sqlPassword: ");
		result.append(sqlPassword);
		result.append(", sqlSchema: ");
		result.append(sqlSchema);
		result.append(", sqlValidationConnectionURL: ");
		result.append(sqlValidationConnectionURL);
		result.append(')');
		return result.toString();
	}

} //SQLDatabaseBindingImpl
