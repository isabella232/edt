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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Java400 J2c Protocol</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getConversionTable <em>Conversion Table</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getCurrentLibrary <em>Current Library</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getLibraries <em>Libraries</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getPassword <em>Password</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getUserID <em>User ID</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getJava400J2cProtocol()
 * @model extendedMetaData="name='Java400J2cProtocol' kind='empty'"
 * @generated
 */
public interface Java400J2cProtocol extends Protocol {
	/**
	 * Returns the value of the '<em><b>Conversion Table</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Conversion Table</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Conversion Table</em>' attribute.
	 * @see #setConversionTable(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getJava400J2cProtocol_ConversionTable()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='conversionTable'"
	 * @generated
	 */
	String getConversionTable();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getConversionTable <em>Conversion Table</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Conversion Table</em>' attribute.
	 * @see #getConversionTable()
	 * @generated
	 */
	void setConversionTable(String value);

	/**
	 * Returns the value of the '<em><b>Current Library</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Current Library</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Current Library</em>' attribute.
	 * @see #setCurrentLibrary(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getJava400J2cProtocol_CurrentLibrary()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='currentLibrary'"
	 * @generated
	 */
	String getCurrentLibrary();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getCurrentLibrary <em>Current Library</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Current Library</em>' attribute.
	 * @see #getCurrentLibrary()
	 * @generated
	 */
	void setCurrentLibrary(String value);

	/**
	 * Returns the value of the '<em><b>Libraries</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Libraries</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Libraries</em>' attribute.
	 * @see #setLibraries(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getJava400J2cProtocol_Libraries()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='libraries'"
	 * @generated
	 */
	String getLibraries();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getLibraries <em>Libraries</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Libraries</em>' attribute.
	 * @see #getLibraries()
	 * @generated
	 */
	void setLibraries(String value);

	/**
	 * Returns the value of the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Location</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Location</em>' attribute.
	 * @see #setLocation(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getJava400J2cProtocol_Location()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='location'"
	 * @generated
	 */
	String getLocation();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getLocation <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Location</em>' attribute.
	 * @see #getLocation()
	 * @generated
	 */
	void setLocation(String value);

	/**
	 * Returns the value of the '<em><b>Password</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Password</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Password</em>' attribute.
	 * @see #setPassword(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getJava400J2cProtocol_Password()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='password'"
	 * @generated
	 */
	String getPassword();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getPassword <em>Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Password</em>' attribute.
	 * @see #getPassword()
	 * @generated
	 */
	void setPassword(String value);

	/**
	 * Returns the value of the '<em><b>User ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>User ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>User ID</em>' attribute.
	 * @see #setUserID(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getJava400J2cProtocol_UserID()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='userID'"
	 * @generated
	 */
	String getUserID();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol#getUserID <em>User ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>User ID</em>' attribute.
	 * @see #getUserID()
	 * @generated
	 */
	void setUserID(String value);

} // Java400J2cProtocol
