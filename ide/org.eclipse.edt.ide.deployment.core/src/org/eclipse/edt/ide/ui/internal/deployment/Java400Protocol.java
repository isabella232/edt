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
 * A representation of the model object '<em><b>Java400 Protocol</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getConversionTable <em>Conversion Table</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getLibrary <em>Library</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getPassword <em>Password</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getUserID <em>User ID</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getJava400Protocol()
 * @model extendedMetaData="name='Java400Protocol' kind='empty'"
 * @generated
 */
public interface Java400Protocol extends Protocol {
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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getJava400Protocol_ConversionTable()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='conversionTable'"
	 * @generated
	 */
	String getConversionTable();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getConversionTable <em>Conversion Table</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Conversion Table</em>' attribute.
	 * @see #getConversionTable()
	 * @generated
	 */
	void setConversionTable(String value);

	/**
	 * Returns the value of the '<em><b>Library</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Library</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Library</em>' attribute.
	 * @see #setLibrary(String)
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getJava400Protocol_Library()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='library'"
	 * @generated
	 */
	String getLibrary();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getLibrary <em>Library</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Library</em>' attribute.
	 * @see #getLibrary()
	 * @generated
	 */
	void setLibrary(String value);

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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getJava400Protocol_Location()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='location'"
	 * @generated
	 */
	String getLocation();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getLocation <em>Location</em>}' attribute.
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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getJava400Protocol_Password()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='password'"
	 * @generated
	 */
	String getPassword();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getPassword <em>Password</em>}' attribute.
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
	 * @see org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage#getJava400Protocol_UserID()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='userID'"
	 * @generated
	 */
	String getUserID();

	/**
	 * Sets the value of the '{@link org.eclipse.edt.ide.ui.internal.deployment.Java400Protocol#getUserID <em>User ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>User ID</em>' attribute.
	 * @see #getUserID()
	 * @generated
	 */
	void setUserID(String value);

} // Java400Protocol
