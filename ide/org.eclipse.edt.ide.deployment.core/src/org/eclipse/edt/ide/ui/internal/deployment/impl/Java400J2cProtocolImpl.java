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
import org.eclipse.edt.ide.ui.internal.deployment.Java400J2cProtocol;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Java400 J2c Protocol</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.Java400J2cProtocolImpl#getConversionTable <em>Conversion Table</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.Java400J2cProtocolImpl#getCurrentLibrary <em>Current Library</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.Java400J2cProtocolImpl#getLibraries <em>Libraries</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.Java400J2cProtocolImpl#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.Java400J2cProtocolImpl#getPassword <em>Password</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.Java400J2cProtocolImpl#getUserID <em>User ID</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class Java400J2cProtocolImpl extends ProtocolImpl implements Java400J2cProtocol {
	/**
	 * The default value of the '{@link #getConversionTable() <em>Conversion Table</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConversionTable()
	 * @generated
	 * @ordered
	 */
	protected static final String CONVERSION_TABLE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getConversionTable() <em>Conversion Table</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConversionTable()
	 * @generated
	 * @ordered
	 */
	protected String conversionTable = CONVERSION_TABLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getCurrentLibrary() <em>Current Library</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCurrentLibrary()
	 * @generated
	 * @ordered
	 */
	protected static final String CURRENT_LIBRARY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCurrentLibrary() <em>Current Library</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCurrentLibrary()
	 * @generated
	 * @ordered
	 */
	protected String currentLibrary = CURRENT_LIBRARY_EDEFAULT;

	/**
	 * The default value of the '{@link #getLibraries() <em>Libraries</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLibraries()
	 * @generated
	 * @ordered
	 */
	protected static final String LIBRARIES_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLibraries() <em>Libraries</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLibraries()
	 * @generated
	 * @ordered
	 */
	protected String libraries = LIBRARIES_EDEFAULT;

	/**
	 * The default value of the '{@link #getLocation() <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocation()
	 * @generated
	 * @ordered
	 */
	protected static final String LOCATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLocation() <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocation()
	 * @generated
	 * @ordered
	 */
	protected String location = LOCATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getPassword() <em>Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPassword()
	 * @generated
	 * @ordered
	 */
	protected static final String PASSWORD_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPassword() <em>Password</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPassword()
	 * @generated
	 * @ordered
	 */
	protected String password = PASSWORD_EDEFAULT;

	/**
	 * The default value of the '{@link #getUserID() <em>User ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUserID()
	 * @generated
	 * @ordered
	 */
	protected static final String USER_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUserID() <em>User ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUserID()
	 * @generated
	 * @ordered
	 */
	protected String userID = USER_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Java400J2cProtocolImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.JAVA400_J2C_PROTOCOL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getConversionTable() {
		return conversionTable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setConversionTable(String newConversionTable) {
		String oldConversionTable = conversionTable;
		conversionTable = newConversionTable;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.JAVA400_J2C_PROTOCOL__CONVERSION_TABLE, oldConversionTable, conversionTable));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCurrentLibrary() {
		return currentLibrary;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCurrentLibrary(String newCurrentLibrary) {
		String oldCurrentLibrary = currentLibrary;
		currentLibrary = newCurrentLibrary;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.JAVA400_J2C_PROTOCOL__CURRENT_LIBRARY, oldCurrentLibrary, currentLibrary));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLibraries() {
		return libraries;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLibraries(String newLibraries) {
		String oldLibraries = libraries;
		libraries = newLibraries;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.JAVA400_J2C_PROTOCOL__LIBRARIES, oldLibraries, libraries));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocation(String newLocation) {
		String oldLocation = location;
		location = newLocation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.JAVA400_J2C_PROTOCOL__LOCATION, oldLocation, location));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPassword(String newPassword) {
		String oldPassword = password;
		password = newPassword;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.JAVA400_J2C_PROTOCOL__PASSWORD, oldPassword, password));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUserID(String newUserID) {
		String oldUserID = userID;
		userID = newUserID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.JAVA400_J2C_PROTOCOL__USER_ID, oldUserID, userID));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__CONVERSION_TABLE:
				return getConversionTable();
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__CURRENT_LIBRARY:
				return getCurrentLibrary();
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__LIBRARIES:
				return getLibraries();
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__LOCATION:
				return getLocation();
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__PASSWORD:
				return getPassword();
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__USER_ID:
				return getUserID();
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
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__CONVERSION_TABLE:
				setConversionTable((String)newValue);
				return;
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__CURRENT_LIBRARY:
				setCurrentLibrary((String)newValue);
				return;
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__LIBRARIES:
				setLibraries((String)newValue);
				return;
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__LOCATION:
				setLocation((String)newValue);
				return;
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__PASSWORD:
				setPassword((String)newValue);
				return;
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__USER_ID:
				setUserID((String)newValue);
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
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__CONVERSION_TABLE:
				setConversionTable(CONVERSION_TABLE_EDEFAULT);
				return;
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__CURRENT_LIBRARY:
				setCurrentLibrary(CURRENT_LIBRARY_EDEFAULT);
				return;
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__LIBRARIES:
				setLibraries(LIBRARIES_EDEFAULT);
				return;
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__LOCATION:
				setLocation(LOCATION_EDEFAULT);
				return;
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__PASSWORD:
				setPassword(PASSWORD_EDEFAULT);
				return;
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__USER_ID:
				setUserID(USER_ID_EDEFAULT);
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
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__CONVERSION_TABLE:
				return CONVERSION_TABLE_EDEFAULT == null ? conversionTable != null : !CONVERSION_TABLE_EDEFAULT.equals(conversionTable);
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__CURRENT_LIBRARY:
				return CURRENT_LIBRARY_EDEFAULT == null ? currentLibrary != null : !CURRENT_LIBRARY_EDEFAULT.equals(currentLibrary);
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__LIBRARIES:
				return LIBRARIES_EDEFAULT == null ? libraries != null : !LIBRARIES_EDEFAULT.equals(libraries);
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__LOCATION:
				return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__PASSWORD:
				return PASSWORD_EDEFAULT == null ? password != null : !PASSWORD_EDEFAULT.equals(password);
			case DeploymentPackage.JAVA400_J2C_PROTOCOL__USER_ID:
				return USER_ID_EDEFAULT == null ? userID != null : !USER_ID_EDEFAULT.equals(userID);
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
		result.append(" (conversionTable: ");
		result.append(conversionTable);
		result.append(", currentLibrary: ");
		result.append(currentLibrary);
		result.append(", libraries: ");
		result.append(libraries);
		result.append(", location: ");
		result.append(location);
		result.append(", password: ");
		result.append(password);
		result.append(", userID: ");
		result.append(userID);
		result.append(')');
		return result.toString();
	}

} //Java400J2cProtocolImpl
