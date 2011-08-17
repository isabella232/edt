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

import org.eclipse.edt.ide.ui.internal.deployment.CICSECIProtocol;
import org.eclipse.edt.ide.ui.internal.deployment.DeploymentPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>CICSECI Protocol</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSECIProtocolImpl#getConversionTable <em>Conversion Table</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSECIProtocolImpl#getCtgLocation <em>Ctg Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSECIProtocolImpl#getCtgPort <em>Ctg Port</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSECIProtocolImpl#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.edt.ide.ui.internal.deployment.impl.CICSECIProtocolImpl#getServerID <em>Server ID</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CICSECIProtocolImpl extends ProtocolImpl implements CICSECIProtocol {
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
	 * The default value of the '{@link #getCtgLocation() <em>Ctg Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCtgLocation()
	 * @generated
	 * @ordered
	 */
	protected static final String CTG_LOCATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCtgLocation() <em>Ctg Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCtgLocation()
	 * @generated
	 * @ordered
	 */
	protected String ctgLocation = CTG_LOCATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getCtgPort() <em>Ctg Port</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCtgPort()
	 * @generated
	 * @ordered
	 */
	protected static final String CTG_PORT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCtgPort() <em>Ctg Port</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCtgPort()
	 * @generated
	 * @ordered
	 */
	protected String ctgPort = CTG_PORT_EDEFAULT;

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
	 * The default value of the '{@link #getServerID() <em>Server ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServerID()
	 * @generated
	 * @ordered
	 */
	protected static final String SERVER_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getServerID() <em>Server ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getServerID()
	 * @generated
	 * @ordered
	 */
	protected String serverID = SERVER_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CICSECIProtocolImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DeploymentPackage.Literals.CICSECI_PROTOCOL;
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
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.CICSECI_PROTOCOL__CONVERSION_TABLE, oldConversionTable, conversionTable));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCtgLocation() {
		return ctgLocation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCtgLocation(String newCtgLocation) {
		String oldCtgLocation = ctgLocation;
		ctgLocation = newCtgLocation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.CICSECI_PROTOCOL__CTG_LOCATION, oldCtgLocation, ctgLocation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCtgPort() {
		return ctgPort;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCtgPort(String newCtgPort) {
		String oldCtgPort = ctgPort;
		ctgPort = newCtgPort;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.CICSECI_PROTOCOL__CTG_PORT, oldCtgPort, ctgPort));
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
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.CICSECI_PROTOCOL__LOCATION, oldLocation, location));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getServerID() {
		return serverID;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setServerID(String newServerID) {
		String oldServerID = serverID;
		serverID = newServerID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, DeploymentPackage.CICSECI_PROTOCOL__SERVER_ID, oldServerID, serverID));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case DeploymentPackage.CICSECI_PROTOCOL__CONVERSION_TABLE:
				return getConversionTable();
			case DeploymentPackage.CICSECI_PROTOCOL__CTG_LOCATION:
				return getCtgLocation();
			case DeploymentPackage.CICSECI_PROTOCOL__CTG_PORT:
				return getCtgPort();
			case DeploymentPackage.CICSECI_PROTOCOL__LOCATION:
				return getLocation();
			case DeploymentPackage.CICSECI_PROTOCOL__SERVER_ID:
				return getServerID();
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
			case DeploymentPackage.CICSECI_PROTOCOL__CONVERSION_TABLE:
				setConversionTable((String)newValue);
				return;
			case DeploymentPackage.CICSECI_PROTOCOL__CTG_LOCATION:
				setCtgLocation((String)newValue);
				return;
			case DeploymentPackage.CICSECI_PROTOCOL__CTG_PORT:
				setCtgPort((String)newValue);
				return;
			case DeploymentPackage.CICSECI_PROTOCOL__LOCATION:
				setLocation((String)newValue);
				return;
			case DeploymentPackage.CICSECI_PROTOCOL__SERVER_ID:
				setServerID((String)newValue);
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
			case DeploymentPackage.CICSECI_PROTOCOL__CONVERSION_TABLE:
				setConversionTable(CONVERSION_TABLE_EDEFAULT);
				return;
			case DeploymentPackage.CICSECI_PROTOCOL__CTG_LOCATION:
				setCtgLocation(CTG_LOCATION_EDEFAULT);
				return;
			case DeploymentPackage.CICSECI_PROTOCOL__CTG_PORT:
				setCtgPort(CTG_PORT_EDEFAULT);
				return;
			case DeploymentPackage.CICSECI_PROTOCOL__LOCATION:
				setLocation(LOCATION_EDEFAULT);
				return;
			case DeploymentPackage.CICSECI_PROTOCOL__SERVER_ID:
				setServerID(SERVER_ID_EDEFAULT);
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
			case DeploymentPackage.CICSECI_PROTOCOL__CONVERSION_TABLE:
				return CONVERSION_TABLE_EDEFAULT == null ? conversionTable != null : !CONVERSION_TABLE_EDEFAULT.equals(conversionTable);
			case DeploymentPackage.CICSECI_PROTOCOL__CTG_LOCATION:
				return CTG_LOCATION_EDEFAULT == null ? ctgLocation != null : !CTG_LOCATION_EDEFAULT.equals(ctgLocation);
			case DeploymentPackage.CICSECI_PROTOCOL__CTG_PORT:
				return CTG_PORT_EDEFAULT == null ? ctgPort != null : !CTG_PORT_EDEFAULT.equals(ctgPort);
			case DeploymentPackage.CICSECI_PROTOCOL__LOCATION:
				return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
			case DeploymentPackage.CICSECI_PROTOCOL__SERVER_ID:
				return SERVER_ID_EDEFAULT == null ? serverID != null : !SERVER_ID_EDEFAULT.equals(serverID);
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
		result.append(", ctgLocation: ");
		result.append(ctgLocation);
		result.append(", ctgPort: ");
		result.append(ctgPort);
		result.append(", location: ");
		result.append(location);
		result.append(", serverID: ");
		result.append(serverID);
		result.append(')');
		return result.toString();
	}

} //CICSECIProtocolImpl
