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
package org.eclipse.edt.ide.ui.internal.deployment;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>SQL Database Binding</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getDbms <em>Dbms</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getJarList <em>Jar List</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlDB <em>Sql DB</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlID <em>Sql ID</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlJDBCDriverClass <em>Sql JDBC Driver Class</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlJNDIName <em>Sql JNDI Name</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlPassword <em>Sql Password</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlSchema <em>Sql Schema</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlValidationConnectionURL <em>Sql Validation Connection URL</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSQLDatabaseBinding()
 * @model extendedMetaData="name='SQLDatabaseBinding' kind='empty'"
 * @generated
 */
public interface SQLDatabaseBinding extends EObject {
	/**
	 * Returns the value of the '<em><b>Dbms</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dbms</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dbms</em>' attribute.
	 * @see #setDbms(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSQLDatabaseBinding_Dbms()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='dbms'"
	 * @generated
	 */
	String getDbms();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getDbms <em>Dbms</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dbms</em>' attribute.
	 * @see #getDbms()
	 * @generated
	 */
	void setDbms(String value);

	/**
	 * Returns the value of the '<em><b>Jar List</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Jar List</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Jar List</em>' attribute.
	 * @see #setJarList(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSQLDatabaseBinding_JarList()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='jarList'"
	 * @generated
	 */
	String getJarList();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getJarList <em>Jar List</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Jar List</em>' attribute.
	 * @see #getJarList()
	 * @generated
	 */
	void setJarList(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSQLDatabaseBinding_Name()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.NCName" required="true"
	 *        extendedMetaData="kind='attribute' name='name'"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Sql DB</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sql DB</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sql DB</em>' attribute.
	 * @see #setSqlDB(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSQLDatabaseBinding_SqlDB()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='sqlDB'"
	 * @generated
	 */
	String getSqlDB();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlDB <em>Sql DB</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sql DB</em>' attribute.
	 * @see #getSqlDB()
	 * @generated
	 */
	void setSqlDB(String value);

	/**
	 * Returns the value of the '<em><b>Sql ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sql ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sql ID</em>' attribute.
	 * @see #setSqlID(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSQLDatabaseBinding_SqlID()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='sqlID'"
	 * @generated
	 */
	String getSqlID();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlID <em>Sql ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sql ID</em>' attribute.
	 * @see #getSqlID()
	 * @generated
	 */
	void setSqlID(String value);

	/**
	 * Returns the value of the '<em><b>Sql JDBC Driver Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sql JDBC Driver Class</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sql JDBC Driver Class</em>' attribute.
	 * @see #setSqlJDBCDriverClass(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSQLDatabaseBinding_SqlJDBCDriverClass()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='sqlJDBCDriverClass'"
	 * @generated
	 */
	String getSqlJDBCDriverClass();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlJDBCDriverClass <em>Sql JDBC Driver Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sql JDBC Driver Class</em>' attribute.
	 * @see #getSqlJDBCDriverClass()
	 * @generated
	 */
	void setSqlJDBCDriverClass(String value);

	/**
	 * Returns the value of the '<em><b>Sql JNDI Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sql JNDI Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sql JNDI Name</em>' attribute.
	 * @see #setSqlJNDIName(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSQLDatabaseBinding_SqlJNDIName()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='sqlJNDIName'"
	 * @generated
	 */
	String getSqlJNDIName();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlJNDIName <em>Sql JNDI Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sql JNDI Name</em>' attribute.
	 * @see #getSqlJNDIName()
	 * @generated
	 */
	void setSqlJNDIName(String value);

	/**
	 * Returns the value of the '<em><b>Sql Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sql Password</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sql Password</em>' attribute.
	 * @see #setSqlPassword(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSQLDatabaseBinding_SqlPassword()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='sqlPassword'"
	 * @generated
	 */
	String getSqlPassword();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlPassword <em>Sql Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sql Password</em>' attribute.
	 * @see #getSqlPassword()
	 * @generated
	 */
	void setSqlPassword(String value);

	/**
	 * Returns the value of the '<em><b>Sql Schema</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sql Schema</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sql Schema</em>' attribute.
	 * @see #setSqlSchema(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSQLDatabaseBinding_SqlSchema()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='sqlSchema'"
	 * @generated
	 */
	String getSqlSchema();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlSchema <em>Sql Schema</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sql Schema</em>' attribute.
	 * @see #getSqlSchema()
	 * @generated
	 */
	void setSqlSchema(String value);

	/**
	 * Returns the value of the '<em><b>Sql Validation Connection URL</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sql Validation Connection URL</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sql Validation Connection URL</em>' attribute.
	 * @see #setSqlValidationConnectionURL(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getSQLDatabaseBinding_SqlValidationConnectionURL()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='sqlValidationConnectionURL'"
	 * @generated
	 */
	String getSqlValidationConnectionURL();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.SQLDatabaseBinding#getSqlValidationConnectionURL <em>Sql Validation Connection URL</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sql Validation Connection URL</em>' attribute.
	 * @see #getSqlValidationConnectionURL()
	 * @generated
	 */
	void setSqlValidationConnectionURL(String value);

} // SQLDatabaseBinding
